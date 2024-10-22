package com.aidarn.richedit.data

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.TextFieldValue
import com.aidarn.gitcook.data.ComplexTypeConverters
import com.aidarn.richedit.Style
import com.aidarn.richedit.StyleType
import kotlin.time.Duration


internal class Markup internal constructor(private val tokens: List<Token>) :
    List<Token> by tokens {
    companion object {
        fun String.toMarkup(): Markup {
            return Markup(MarkupParser().parse(this))
        }
    }

    fun toRenderElements(): List<RenderElement> {
        if (tokens.isEmpty()) {
            return listOf(RenderElement.Text(TextFieldValue()))
        }
        val res = mutableListOf<RenderElement>()
        var i = 0
        var isLastTokenIsTextToken = false
        while (i < tokens.size) {
            isLastTokenIsTextToken = when {
                isTextToken(tokens[i]) -> {
                    val j = tokens.findNextNotTextToken(i)
                    val textElement = tokens.slice(i..j).toTextElement()
                    res.add(textElement)
                    i = j
                    true
                }

                isTimerToken(tokens[i]) -> {
                    if (!isLastTokenIsTextToken) {
                        res.add(RenderElement.Text(TextFieldValue()))
                    }
                    val timerToken = tokens[i] as Token.Timer
                    res.add(RenderElement.Timer(timerToken.duration))
                    false
                }

                isImageToken(tokens[i]) -> {
                    if (!isLastTokenIsTextToken) {
                        res.add(RenderElement.Text(TextFieldValue()))
                    }
                    val imageToken = tokens[i] as Token.Image
                    res.add(
                        RenderElement.Image(
                            imageToken.imageModel,
                            imageToken.placeholderText
                        )
                    )
                    false
                }

                else -> false
            }
            i += 1
        }
        if (res.last() !is RenderElement.Text) {
            res.add(RenderElement.Text(TextFieldValue()))
        }

        return res
    }
}

private class MarkupParser {
    private val markupBuilder = MarkupBuilder(mutableListOf())
    private val buffer = StringBuilder()
    private fun flushBuffer() {
        if (buffer.isNotEmpty()) {
            markupBuilder.append(Token.Text(buffer.toString()))
            buffer.clear()
        }
    }

    fun parse(input: String): List<Token> {
        return parseCore(input, UniversalMatcher)
    }

    private fun parseCore(input: String, matcher: IMatcher): List<Token> {
        markupBuilder.clear()
        buffer.clear()
        var i = 0
        var next: Int
        if (input.isEmpty()) return listOf(Token.Text(""))
        while (i < input.length) {
            val res = matcher.matchAt(input, i).let {
                val token = it.first
                if (token != null) {
                    flushBuffer()
                    markupBuilder.append(token)
                }
                next = i + it.second
                it.second
            }
            if (res > 0) {
                i = next
                continue
            }
            when {
                input.startsWith("**", i) -> {
                    val j = input.findNext("**", i + 2)
                    if (j == -1) {
                        buffer.append("**")
                        i += 1
                    } else {
                        flushBuffer()
                        markupBuilder.append(Token.Bold(MarkupParser().parse(input.slice(i + 2 until j))))
                        i = j + 1
                    }
                }

                input.startsWith("_", i) -> {
                    val j = input.findNext("_", i + 1)
                    if (j == -1) {
                        buffer.append("_")
                    } else {
                        flushBuffer()
                        markupBuilder.append(Token.Italic(MarkupParser().parse(input.slice(i + 1 until j))))
                        i = j
                    }
                }

                else -> {
                    buffer.append(input[i])
                }
            }
            i += 1
        }
        flushBuffer()

        return markupBuilder.getTokens()
    }

    private class MarkupBuilder(private val tokens: MutableList<Token>) : TokenVisitor {
        private sealed class TokenMerger<T : Token> {
            abstract fun merge(first: T, second: T): T

            data object Text : TokenMerger<Token.Text>() {
                override fun merge(first: Token.Text, second: Token.Text): Token.Text {
                    return Token.Text(buildString { appendLine(first.content); append(second.content) })
                }
            }

            data object UnorderedList : TokenMerger<Token.UnorderedList>() {
                override fun merge(
                    first: Token.UnorderedList,
                    second: Token.UnorderedList
                ): Token.UnorderedList {
                    val mergedList = mutableListOf<List<Token>>()
                    mergedList.addAll(first.content)
                    mergedList.addAll(second.content)
                    return Token.UnorderedList(mergedList)
                }
            }

            data object OrderedList : TokenMerger<Token.OrderedList>() {
                override fun merge(
                    first: Token.OrderedList,
                    second: Token.OrderedList
                ): Token.OrderedList {
                    val mergedList = mutableListOf<List<Token>>()
                    mergedList.addAll(first.content)
                    mergedList.addAll(second.content)
                    return Token.OrderedList(mergedList, first.startIndex)
                }
            }
        }

        private var buffer: Token? = null
        fun append(token: Token) {
            token.acceptVisitor(this)
        }

        fun getTokens(): List<Token> {
            flushBuffer()
            return tokens
        }

        fun clear() {
            tokens.clear()
            buffer = null
        }

        override fun visit(token: Token.Text) {
            val bufferAsText = buffer as? Token.Text
            if (bufferAsText != null) {
                buffer = TokenMerger.Text.merge(bufferAsText, token)
                return
            }
            flushBuffer()
            buffer = token
        }

        override fun visit(token: Token.Bold) {
            flushBuffer()
            tokens.add(token)
        }

        override fun visit(token: Token.Italic) {
            flushBuffer()
            tokens.add(token)
        }

        override fun visit(token: Token.Heading) {
            flushBuffer()
            tokens.add(token)
        }

        override fun visit(token: Token.UnorderedList) {
            val bufferAsUnorderedList = buffer as? Token.UnorderedList
            if (bufferAsUnorderedList != null) {
                buffer = TokenMerger.UnorderedList.merge(bufferAsUnorderedList, token)
                return
            }
            flushBuffer()
            buffer = token
        }

        override fun visit(token: Token.OrderedList) {
            val bufferAsOrderedList = buffer as? Token.OrderedList
            if (bufferAsOrderedList != null) {
                buffer = TokenMerger.OrderedList.merge(bufferAsOrderedList, token)
                return
            }
            flushBuffer()
            buffer = token
        }

        override fun visit(token: Token.Timer) {
            flushBuffer()
            tokens.add(token)
        }

        override fun visit(token: Token.Image) {
            flushBuffer()
            tokens.add(token)
        }

        private fun flushBuffer() {
            buffer?.let {
                tokens.add(it)
            }
            buffer = null
        }
    }

    private abstract class BaseLineMatcher(protected val regex: Regex) {
        open fun matchAt(line: String, index: Int): Pair<Token?, Int> {
            return regex.matchAt(line, index)?.let {
                Pair(action(it.groupValues[1]), it.groupValues[0].length)
            } ?: Pair(null, 0)
        }

        protected abstract fun action(line: String): Token
    }

    private class OrderedListMatcher : BaseLineMatcher(Pattern.Line.OrderedList) {
        private var startIndex = 1
        override fun action(line: String): Token {
            val content = MarkupParser().parseCore(line, NoMatcher)
            return Token.OrderedList(listOf(content), startIndex)
        }

        override fun matchAt(line: String, index: Int): Pair<Token?, Int> {
            return regex.matchAt(line, index)?.let { matchResult ->
                startIndex = matchResult.groupValues[1].run {
                    toIntOrNull() ?: 1
                }
                Pair(action(matchResult.groupValues[2]), matchResult.groupValues[0].length)
            } ?: Pair(null, 0)
        }
    }

    private object Heading1Matcher : BaseLineMatcher(Pattern.Line.Heading1) {
        override fun action(line: String): Token {
            return Token.Heading(1, MarkupParser().parseCore(line, NoMatcher))
        }
    }

    private object Heading2Matcher : BaseLineMatcher(Pattern.Line.Heading2) {
        override fun action(line: String): Token {
            return Token.Heading(2, MarkupParser().parseCore(line, NoMatcher))
        }
    }

    private object UnorderedListMatcher : BaseLineMatcher(Pattern.Line.RawUnorderedList) {
        override fun action(line: String): Token {
            val content = MarkupParser().parseCore(line, NoMatcher)
            return Token.UnorderedList(listOf(content))
        }
    }

    private object TimerMatcher : BaseLineMatcher(Pattern.Line.Timer) {
        override fun action(line: String): Token {
            return Token.Timer(
                Duration.parseIsoStringOrNull(line) ?: Duration.ZERO
            )
        }
    }

    private object ImageMatcher : BaseLineMatcher(Pattern.Image) {
        override fun action(line: String): Token {
            val match = Regex("""^\[(.*)]\((.+)\)$""").find(line)
            return if (match != null) {
                val imageModel = ComplexTypeConverters.toAny(match.groupValues[2])
                val placeholder = match.groupValues[1]
                Token.Image(imageModel, placeholder)
            } else {
                throw IllegalStateException()
            }
        }
    }

    private interface IMatcher {
        fun matchAt(string: String, index: Int): Pair<Token?, Int>
    }

    private object UniversalMatcher : IMatcher {
        private val matchers = listOf(
            Heading2Matcher,
            Heading1Matcher,
            UnorderedListMatcher,
            OrderedListMatcher(),
            TimerMatcher,
            ImageMatcher
        )

        override fun matchAt(string: String, index: Int): Pair<Token?, Int> {
            for (matcher in matchers) {
                val result = matcher.matchAt(string, index)
                if (result.first != null) {
                    return result
                }
            }
            return Pair(null, 0)
        }
    }

    private object NoMatcher : IMatcher {
        override fun matchAt(string: String, index: Int): Pair<Token?, Int> {
            return Pair(null, 0)
        }
    }
}


internal fun List<Token>.toTextElement(): RenderElement.Text {
    val annotatedString = this.toAnnotatedString()
    return RenderElement.Text(
        TextFieldValue(
            annotatedString = annotatedString,
            selection = TextRange(annotatedString.length)
        )
    )
}

internal fun List<Token>.toAnnotatedString(): AnnotatedString {
    return buildAnnotatedString {
        for (token in this@toAnnotatedString) {
            when (token) {
                is Token.Text -> {
                    append(token.content)
                }

                is Token.Bold -> {
                    append(getBoldText(token.content.toAnnotatedString()))
                }

                is Token.Italic -> {
                    append(getItalicText(token.content.toAnnotatedString()))
                }

                is Token.Heading -> {
                    val innerContent = token.content.toAnnotatedString()
                    val getHeader: (AnnotatedString) -> AnnotatedString = if (token.level == 1) {
                        { input -> getHeading1Text(input) }
                    } else {
                        { input -> getHeading2Text(input) }
                    }

                    append(getHeader(innerContent))
                }

                is Token.UnorderedList -> {
                    val entries = buildList(token.content.size) {
                        token.content.forEach {
                            add(it.toAnnotatedString())
                        }
                    }
                    append(getUnorderedList(entries))
                }

                is Token.OrderedList -> {
                    val entries = buildList(token.content.size) {
                        token.content.forEach {
                            add(it.toAnnotatedString())
                        }
                    }
                    append(getOrderedList(entries, token.startIndex))
                }

                is Token.Timer -> throw IllegalStateException("Can not convert ${Token.Timer::class.java.simpleName} to AnnotatedString")
                is Token.Image -> throw IllegalStateException("Can not convert ${Token.Image::class.java.simpleName} to AnnotatedString")
            }
        }
    }
}

private fun List<Token>.findNextNotTextToken(start: Int): Int {
    var i = start
    while (i < size - 1 && isTextToken(get(i + 1))) {
        i += 1
    }

    return i
}


internal fun getBoldText(text: String) = buildAnnotatedString {
    pushStyle(Style[StyleType.Bold])
    append(text)
    pop()
}

internal fun getBoldText(text: AnnotatedString) = buildAnnotatedString {
    pushStyle(Style[StyleType.Bold])
    append(text)
    pop()
}

internal fun getItalicText(text: String) = buildAnnotatedString {
    pushStyle(Style[StyleType.Italic])
    append(text)
    pop()
}

internal fun getItalicText(text: AnnotatedString) = buildAnnotatedString {
    pushStyle(Style[StyleType.Italic])
    append(text)
    pop()
}

internal fun getHeading1Text(text: String) = buildAnnotatedString {
    pushStyle(Style[StyleType.Heading1])
    append(text)
    pop()
}

internal fun getHeading1Text(text: AnnotatedString) = buildAnnotatedString {
    pushStyle(Style[StyleType.Heading1])
    append(text)
    pop()
}

internal fun getHeading2Text(text: String) = buildAnnotatedString {
    pushStyle(Style[StyleType.Heading2])
    append(text)
    pop()
}

internal fun getHeading2Text(text: AnnotatedString) = buildAnnotatedString {
    pushStyle(Style[StyleType.Heading2])
    append(text)
    pop()
}

internal fun getUnorderedList(entries: List<CharSequence>) = buildAnnotatedString {
    pushStyle(Style.listStyle)
    for (entry in entries) {
        append("\u2022 ")
        append(entry)
    }
    pop()
}

internal fun getOrderedList(entries: List<CharSequence>, startIndex: Int = 1) =
    buildAnnotatedString {
        pushStyle(Style.listStyle)
        var i = startIndex
        for (entry in entries) {
            append("$i. ")
            append(entry)
            i += 1
        }
        pop()
    }

private fun String.findNext(substring: String, start: Int): Int {
    var i = start
    while (i < length && !this.startsWith(substring, i)) {
        i += 1
    }

    if (i + 1 < length && get(i + 1) == '\n') {
        i += 1
    }

    return if (i >= length) -1 else i
}

private fun isTextToken(token: Token): Boolean {
    return token !is Token.Timer && token !is Token.Image
}

private fun isTimerToken(token: Token): Boolean {
    return token is Token.Timer
}

private fun isImageToken(token: Token): Boolean {
    return token is Token.Image
}
