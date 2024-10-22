package com.aidarn.richedit.ext

import androidx.compose.runtime.Immutable
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.ParagraphStyle
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.buildAnnotatedString
import com.aidarn.richedit.Style
import com.aidarn.richedit.StyleType
import com.aidarn.richedit.data.Pattern
import com.aidarn.richedit.data.StringDiff
import kotlin.math.max
import kotlin.math.min


internal fun AnnotatedString.toMarkupString(): String {
    val stringBuilder = StringBuilder()
    var i = 0
    var styles = spanStyles.flatMap { spanStyle ->
        val first = spanStyle.start
        val styleType = Style.firstMatchingKey(spanStyle.item)
        if (styleType == StyleType.Bold || styleType == StyleType.Italic) {
            val lines = text.slice(spanStyle.start..<spanStyle.end).lines()
            val res = mutableListOf<Pair<Int, StyleType>>()
            var totalLength = 0
            for (line in lines) {
                if (line.isEmpty()) continue
                res.add((first + totalLength) to styleType)
                res.add((first + totalLength + line.length) to styleType)
                totalLength += (line.length + 1)
            }
            res
        } else {
            listOf(Pair(first, styleType))
        }

    }.sortedBy { it.first }
    var openTags = ""
    while (i < length) {
        if (styles.isEmpty()) {
            stringBuilder.append(substring(i))
            break
        }
        if (i < styles.first().first) {
            stringBuilder.append(text.slice(i until styles.first().first))
            i = styles.first().first
            continue
        } else {
            val index = styles.first().first
            var pairList = styles.takeWhile { it.first == index }
            if (openTags != "") {
                pairList = pairList.reversed()
            }
            for (pair in pairList) {
                val tag = when (pair.second) {
                    StyleType.Bold -> {
                        openTags =
                            if (openTags.endsWith("**")) openTags.dropLast(2) else "$openTags**"
                        "**"
                    }

                    StyleType.Italic -> {
                        openTags =
                            if (openTags.endsWith("_")) openTags.dropLast(2) else "${openTags}_"
                        "_"
                    }

                    StyleType.Heading1 -> "# "
                    StyleType.Heading2 -> "## "
                    else -> ""
                }
                stringBuilder.append(tag)
            }
            styles = styles.drop(pairList.size)
        }
    }
    for (style in styles.reversed()) {
        val tag = when (style.second) {
            StyleType.Bold -> "**"
            StyleType.Italic -> "_"
            StyleType.Heading1 -> "# "
            StyleType.Heading2 -> "## "
            else -> ""
        }
        stringBuilder.append(tag)
    }

    return stringBuilder.toString().replace('\u2022', '-')
}

internal fun AnnotatedString.applyTextAndMergeStyles(
    other: AnnotatedString, changes: StringDiff
): AnnotatedString {
    val tempString = applyTextAndChanges(other.text, changes)
    return tempString.mergeStyles(other)
}

internal fun AnnotatedString.mergeStyles(other: AnnotatedString): AnnotatedString {
    require(text == other.text)
    val spans = mergeStyles(spanStyles, other.spanStyles) { first, second ->
        val index = when {
            first.start == second.end -> first.start
            first.end == second.start -> first.end
            else -> throw IllegalStateException("Ranges should be adjacent")
        }
        index - 1 in text.indices && text[index - 1] == '\n'
    }
    val paragraphs = mergeStyles(paragraphStyles, other.paragraphStyles)
    return buildAnnotatedString {
        append(text)
        for (span in spans) addStyle(span.item, span.start, span.end)
        for (paragraph in paragraphs) addStyle(paragraph.item, paragraph.start, paragraph.end)
    }
}

internal fun <T> AnnotatedString.Range<T>.contains(index: Int): Boolean {
    return index in start..<end
}

internal fun <T> AnnotatedString.Range<T>.contains(range: IntRange): Boolean {
    return contains(range.first) && contains(range.last)
}

internal fun <T> AnnotatedString.Range<T>.overlaps(range: TextRange): Boolean {
    if (range.length == 0 && range.start != 0) return contains(range.start - 1)
    return contains(range.start) || contains(max(range.start, range.end - 1))
}

internal fun AnnotatedString.toggleSpanStyleAtSelection(
    item: SpanStyle, selection: TextRange
): AnnotatedString {
    val selectedRange = selection.start..<selection.end

    val stylesToModify = findOverlappingSpanStyles(item, selectedRange)

    return if (stylesToModify.headOverlap == null && stylesToModify.tailOverlap == null) {
        if (stylesToModify.fullyOverlapped.size == 1) {
            cutRangeFromSpanStyle(
                stylesToModify.fullyOverlapped[0], selectedRange
            )
        } else {
            replaceSpanStylesWithStyle(
                stylesToModify.fullyOverlapped, item, selectedRange
            )
        }
    } else {
        if (stylesToModify.headOverlap != null && stylesToModify.tailOverlap != null) {
            mergeSpanEndsAndRemoveStyles(
                stylesToModify.headOverlap,
                stylesToModify.tailOverlap,
                stylesToModify.fullyOverlapped
            )
        } else {
            expandSpanStyleAndRemoveStyles(
                stylesToModify.headOverlap ?: stylesToModify.tailOverlap!!,
                selectedRange,
                stylesToModify.fullyOverlapped
            )
        }
    }
}

internal fun AnnotatedString.applyTextAndChanges(
    newText: String, changes: StringDiff
): AnnotatedString {
    return buildAnnotatedString {
        append(newText)
        val newSpanStyles = mutableListOf<AnnotatedString.Range<SpanStyle>>()
        val newParagraphStyles = mutableListOf<AnnotatedString.Range<ParagraphStyle>>()

        for (style in spanStyles) {
            val styleToAdd = when (changes) {
                is StringDiff.Add -> {
                    if (changes.string.contains('\n')) {
                        processStyle(style, newText, changes)
                    } else {
                        listOf(changeStyle(style, changes) { changes.string.startsWith("\n") })
                    }
                }

                is StringDiff.Remove -> {
                    changeStyle(style, changes)?.let { listOf(it) } ?: emptyList()
                }

                is StringDiff.Replace -> {
                    if (changes.string.contains('\n')) {
                        processStyle(style, newText, changes)
                    } else {
                        changeStyle(style, changes) { changes.string.startsWith("\n") }?.let {
                            listOf(it)
                        } ?: emptyList()
                    }
                }
            }
            styleToAdd.forEach {
                newSpanStyles.add(it)
            }
        }

        for (style in paragraphStyles) {
            val styleToAdd = when (changes) {
                is StringDiff.Add -> {
                    changeStyle(style, changes, shouldConsume = {
                        style.start != changes.start || !it.string.contains('\n')
                    })
                }

                is StringDiff.Remove -> {
                    changeStyle(style, changes)
                }

                is StringDiff.Replace -> {
                    changeStyle(style, changes)
                }
            }?.let {
                if (!isCrucialTextChanged(text, newText, changes))
                    it
                else {
                    calcNewStartEnd(newText, changes, it)?.let { (newStart, newEnd) ->
                        AnnotatedString.Range(style.item, newStart, newEnd)
                    }
                }
            }
            styleToAdd?.let {
                newParagraphStyles.add(it)
            }
        }

        for (style in newSpanStyles) {
            if (style.start == style.end) continue
            addStyle(style.item, style.start, style.end)
        }

        for (style in newParagraphStyles) {
            if (style.start == style.end) continue
            addStyle(style.item, style.start, style.end)
        }
    }
}

private fun calcNewStartEnd(
    newText: String,
    changes: StringDiff,
    style: AnnotatedString.Range<ParagraphStyle>
): Pair<Int, Int>? {
    var excludeStart = min(newText.indices.last, changes.start)
    while (excludeStart >= 0) {
        if (newText[excludeStart] == '\n') break
        excludeStart -= 1
    }
    if (excludeStart != 0 || newText[excludeStart] == '\n') {
        excludeStart = min(excludeStart + 1, newText.length)
    }
    var excludeEnd = min(newText.indices.last, changes.end)
    while (excludeEnd < newText.length) {
        if (newText[excludeEnd] == '\n') break
        excludeEnd += 1
    }
    excludeEnd = min(excludeEnd + 1, newText.length)
    if ((excludeStart..excludeEnd).contains(style.start) && (excludeStart..excludeEnd).contains(
            style.end
        )
    ) {
        return null
    }
    val newStart = if (style.start == excludeStart) excludeEnd else style.start
    val newEnd = if (style.end == excludeEnd) excludeStart else style.end
    return Pair(newStart, newEnd)
}

private fun isCrucialTextChanged(oldText: String, newText: String, changes: StringDiff): Boolean {
    var res = false
    val orderedListHeadPattern = Pattern.Head.OrderedList
    var orderedListMatch = orderedListHeadPattern.find(oldText)
    while (orderedListMatch != null) {
        val matchRange = orderedListMatch.groups[1]?.let {
            it.range.first - 1..it.range.last + 2
        } ?: continue
        if (matchRange.contains(changes.start) || matchRange.contains(changes.end - 1)) {
            val checkIndex: Int = calcIndex(orderedListMatch.range.first, changes)
            res = !orderedListHeadPattern.matchesAt(newText, checkIndex)
            break
        }
        orderedListMatch = orderedListMatch.next()
    }
    if (!res) {
        val unorderedListHeadPattern = Pattern.Head.ProcessedUnorderedList
        var match = unorderedListHeadPattern.find(oldText)
        while (match != null) {
            val matchRange = match.range.first - 1..match.range.last + 1
            if (matchRange.contains(changes.start) || matchRange.contains(changes.end - 1)) {
                val checkIndex: Int = calcIndex(match.range.first, changes)
                res = !unorderedListHeadPattern.matchesAt(newText, checkIndex)
                break
            }
            match = match.next()
        }
    }
    return res
}

private fun calcIndex(oldIndex: Int, changes: StringDiff): Int {
    if (oldIndex < changes.start) return oldIndex
    return when (changes) {
        is StringDiff.Add -> {
            oldIndex + changes.string.length
        }

        is StringDiff.Remove -> {
            if (oldIndex == changes.start) return oldIndex
            oldIndex - (changes.end - changes.start)
        }

        is StringDiff.Replace -> {
            oldIndex + changes.string.length - changes.end + changes.start
        }
    }
}

private fun processStyle(
    style: AnnotatedString.Range<SpanStyle>, newText: String, changes: StringDiff.Add
): List<AnnotatedString.Range<SpanStyle>> {
    val changedStyle = changeStyle(style, changes)
    if (!changedStyle.contains(changes.start) && !changedStyle.contains(changes.end)) {
        return listOf(changedStyle)
    }
    val newStyles = mutableListOf<AnnotatedString.Range<SpanStyle>>()
    var i = 0
    var newStart = changedStyle.start
    while (i < changes.string.length) {
        if (changes.string[i] == '\n') {
            val end = when {
                changes.start + i + 1 == newText.length -> newText.length
                changes.start + i + 1 < newText.length && newText[changes.start + i + 1] == '\n' -> changes.start + i + 1
                else -> changes.start + i
            }
            newStyles.add(changedStyle.copy(start = newStart, end = end))
            newStart = changes.start + i + 1
        }
        i += 1
    }

    newStyles.add(changedStyle.copy(start = newStart, end = changedStyle.end))

    return newStyles
}

private fun processStyle(
    style: AnnotatedString.Range<SpanStyle>, newText: String, changes: StringDiff.Replace
): List<AnnotatedString.Range<SpanStyle>> {
    val changedStyle = changeStyle(style, changes) ?: return emptyList()
    if (!changedStyle.contains(changes.start) && !changedStyle.contains(changes.end)) {
        return listOf(changedStyle)
    }
    val newStyles = mutableListOf<AnnotatedString.Range<SpanStyle>>()
    var i = 0
    var newStart = changedStyle.start
    while (i < changes.string.length) {
        if (changes.string[i] == '\n') {
            val end = when {
                changes.start + i + 1 == newText.length -> newText.length
                changes.start + i + 1 < newText.length && newText[changes.start + i + 1] == '\n' -> changes.start + i + 1
                else -> changes.start + i
            }
            newStyles.add(changedStyle.copy(start = newStart, end = end))
            newStart = changes.start + i + 1
        }
        i += 1
    }

    newStyles.add(changedStyle.copy(start = newStart, end = changedStyle.end))

    return newStyles
}

private inline fun <reified T> changeStyle(
    style: AnnotatedString.Range<T>,
    changes: StringDiff.Replace,
    skipReason: (StringDiff.Replace) -> Boolean = { false }
) = if (style.end <= changes.start || skipReason(changes)) {
    style
} else if (style.start <= changes.start && style.end >= changes.end) {
    val newEnd = style.end + changes.string.length - (changes.end - changes.start)
    style.copy(end = newEnd)
} else if (style.start >= changes.end) {
    val shift = changes.string.length - (changes.end - changes.start)
    val newStart = style.start + shift
    val newEnd = style.end + shift
    style.copy(start = newStart, end = newEnd)
} else null

private inline fun <reified T> changeStyle(
    style: AnnotatedString.Range<T>,
    changes: StringDiff.Remove
) = if (style.end <= changes.start) {
    style
} else if (style.start <= changes.start && style.end < changes.end) {
    val newEnd = min(style.end, changes.start)
    style.copy(end = newEnd)
} else if (style.start <= changes.start) {
    val newEnd = style.end - (changes.end - changes.start)
    style.copy(end = newEnd)
} else if (style.start >= changes.end) {
    val newStart = style.start - (changes.end - changes.start)
    val newEnd = style.end - (changes.end - changes.start)
    style.copy(start = newStart, end = newEnd)
} else null

private inline fun <reified T> changeStyle(
    style: AnnotatedString.Range<T>,
    changes: StringDiff.Add,
    shouldConsume: (StringDiff.Add) -> Boolean = { true },
    skipReason: (StringDiff.Add) -> Boolean = { false }
) = if (style.end < changes.start || skipReason(changes)) {
    style
} else {
    val newStart =
        if (style.start <= changes.start && shouldConsume(changes)) style.start else style.start + changes.string.length
    val newEnd = style.end + changes.string.length
    style.copy(start = newStart, end = newEnd)
}

private fun AnnotatedString.findOverlappingSpanStyles(
    spanStyle: SpanStyle, selectedRange: IntRange
) = findStyles(spanStyles, spanStyle, selectedRange)

private fun AnnotatedString.expandSpanStyleAndRemoveStyles(
    range: AnnotatedString.Range<SpanStyle>,
    selectedRange: IntRange,
    stylesToRemove: List<AnnotatedString.Range<SpanStyle>>
): AnnotatedString = buildAnnotatedString {
    append(text)
    paragraphStyles.forEach {
        addStyle(it.item, it.start, it.end)
    }
    spanStyles.forEach {
        if (it != range && !stylesToRemove.contains(it)) {
            addStyle(it.item, it.start, it.end)
        }
    }
    val newStart = min(selectedRange.first, range.start)
    val newEnd = max(selectedRange.last, range.end)
    addStyle(range.item, newStart, newEnd)
}

private fun AnnotatedString.mergeSpanEndsAndRemoveStyles(
    headOverlap: AnnotatedString.Range<SpanStyle>,
    tailOverlap: AnnotatedString.Range<SpanStyle>,
    stylesToRemove: List<AnnotatedString.Range<SpanStyle>>
): AnnotatedString = buildAnnotatedString {
    append(text)
    paragraphStyles.forEach {
        addStyle(it.item, it.start, it.end)
    }
    spanStyles.forEach {
        when {
            it == headOverlap -> Unit
            it == tailOverlap -> Unit
            stylesToRemove.contains(it) -> Unit
            else -> addStyle(it.item, it.start, it.end)
        }
    }
    addStyle(headOverlap.item, headOverlap.start, tailOverlap.end)
}

internal fun AnnotatedString.replaceSpanStylesWithStyle(
    stylesToRemove: List<AnnotatedString.Range<SpanStyle>>,
    spanStyle: SpanStyle,
    selectedRange: IntRange
): AnnotatedString = buildAnnotatedString {
    append(text)
    paragraphStyles.forEach {
        addStyle(it.item, it.start, it.end)
    }
    spanStyles.forEach {
        if (!stylesToRemove.contains(it)) {
            addStyle(it.item, it.start, it.end)
        }
    }
    addStyle(spanStyle, selectedRange.first, selectedRange.last + 1)
}

private fun AnnotatedString.cutRangeFromSpanStyle(
    spanStyle: AnnotatedString.Range<SpanStyle>, selectedRange: IntRange
): AnnotatedString = buildAnnotatedString {
    append(text)
    paragraphStyles.forEach {
        addStyle(it.item, it.start, it.end)
    }
    spanStyles.forEach {
        if (it != spanStyle) {
            addStyle(it.item, it.start, it.end)
        } else {
            if (spanStyle.start < selectedRange.first) {
                addStyle(spanStyle.item, spanStyle.start, selectedRange.first)
            }
            if (spanStyle.end > selectedRange.last + 1) {
                addStyle(spanStyle.item, selectedRange.last + 1, spanStyle.end)
            }
        }
    }
}

internal fun AnnotatedString.withoutSpanStyle(style: AnnotatedString.Range<SpanStyle>) =
    buildAnnotatedString {
        append(text)
        paragraphStyles.forEach {
            addStyle(it.item, it.start, it.end)
        }
        spanStyles.forEach {
            if (it != style) {
                addStyle(it.item, it.start, it.end)
            }
        }
    }

internal fun AnnotatedString.withoutParagraphStyle(style: AnnotatedString.Range<ParagraphStyle>) =
    buildAnnotatedString {
        append(text)
        spanStyles.forEach {
            addStyle(it.item, it.start, it.end)
        }
        paragraphStyles.forEach {
            if (it != style) {
                addStyle(it.item, it.start, it.end)
            }
        }
    }

internal fun AnnotatedString.withStyle(style: SpanStyle, start: Int, end: Int) =
    buildAnnotatedString {
        append(text)
        paragraphStyles.forEach {
            addStyle(it.item, it.start, it.end)
        }
        spanStyles.forEach {
            addStyle(it.item, it.start, it.end)
        }
        addStyle(style, start, end)
    }

private fun <T> findStyles(
    source: Iterable<AnnotatedString.Range<T>>, searchItem: T, selectedRange: IntRange
): StylesToModify<T> {
    val fullOverlap = mutableListOf<AnnotatedString.Range<T>>()
    var headOverlap: AnnotatedString.Range<T>? = null
    var tailOverlap: AnnotatedString.Range<T>? = null
    for (item in source) {
        val overlapType = if (item.item != searchItem) OverlapType.None
        else {
            when {
                item.contains(selectedRange) -> OverlapType.Full
                item.contains(selectedRange.first) -> OverlapType.Head
                item.contains(selectedRange.last) -> OverlapType.Tail
                else -> OverlapType.None
            }
        }
        when (overlapType) {
            OverlapType.Full -> fullOverlap.add(item)
            OverlapType.Head -> headOverlap = item
            OverlapType.Tail -> tailOverlap = item
            OverlapType.None -> Unit
        }
    }
    return StylesToModify(fullOverlap, headOverlap, tailOverlap)
}

private inline fun <reified T> mergeStyles(
    first: List<AnnotatedString.Range<T>>,
    second: List<AnnotatedString.Range<T>>,
    veto: (AnnotatedString.Range<T>, AnnotatedString.Range<T>) -> Boolean = { _, _ -> false }
): Iterable<AnnotatedString.Range<T>> {
    val res = mutableSetOf<AnnotatedString.Range<T>>()
    val source = sequence {
        yieldAll(first)
        yieldAll(second)
    }
    for (range in source) {
        if (source.any { it != range && it.contains(range.start..<range.end) && it.item == range.item }) continue
        if (res.any { it != range && it.contains(range.start..<range.end) && it.item == range.item }) continue
        source.firstOrNull {
            (it.start == range.end || it.end == range.start) && it.item == range.item && !veto(
                it,
                range
            )
        }?.let {
            res.add(it.copy(start = min(it.start, range.start), end = max(it.end, range.end)))
        } ?: res.add(range)
    }

    return res
}

@Immutable
private class StylesToModify<T>(
    val fullyOverlapped: List<AnnotatedString.Range<T>>,
    val headOverlap: AnnotatedString.Range<T>?,
    val tailOverlap: AnnotatedString.Range<T>?
)

private enum class OverlapType {
    Full, Head, Tail, None
}
