package com.aidarn.richedit.controllers

import androidx.collection.IntList
import androidx.collection.mutableIntListOf
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.buildAnnotatedString
import com.aidarn.richedit.data.Pattern
import com.aidarn.richedit.data.getOrderedList
import com.aidarn.richedit.data.getUnorderedList
import com.aidarn.richedit.ext.withoutParagraphStyle
import kotlin.math.max

internal object ListHelper {
    private val orderedListStartRegex = Pattern.Head.OrderedList
    private val orderedListEntryRegex = Pattern.Entry.OrderedList
    private val unorderedListEntryRegex = Pattern.Entry.UnorderedList
    private val lineRegex = Regex("""\n""")

    fun addNewOrderedListEntry(annotatedString: AnnotatedString, index: Int): AnnotatedString {
        val (selection, startIndex) = selectOrderedListAtIndex(annotatedString.text, index)
        val entries = mutableListOf<AnnotatedString>()
        var match = orderedListEntryRegex.find(annotatedString, selection.start)
        var isEntryAdded = false
        while (match != null) {
            if (match.range.last + 1 > selection.end)
                break
            if (!isEntryAdded && match.range.first > index) {
                entries.add(AnnotatedString("\n"))
                isEntryAdded = true
            }

            match.groups[2]?.let {
                entries.add(
                    annotatedString.subSequenceWithoutParagraphStyles(
                        it.range.first,
                        it.range.last + 1
                    )
                )
            }
            match = match.next()
        }
        if (!isEntryAdded) {
            entries.add(AnnotatedString(""))
        }
        return buildAnnotatedString {
            if (0 != selection.start) {
                append(annotatedString.subSequence(0, selection.start))
            }
            append(getOrderedList(entries, startIndex))
            if (annotatedString.length != selection.end) {
                append(annotatedString.subSequence(selection.end, annotatedString.length))
            }
        }
    }

    fun addNewUnorderedListEntry(annotatedString: AnnotatedString, index: Int): AnnotatedString {
        val selection = selectUnorderedListAtIndex(annotatedString.text, index)
        val entries = mutableListOf<AnnotatedString>()
        var match = unorderedListEntryRegex.find(annotatedString, selection.start)
        var isEntryAdded = false
        while (match != null) {
            if (match.range.last + 1 > selection.end)
                break
            if (!isEntryAdded && match.range.first > index) {
                entries.add(AnnotatedString("\n"))
                isEntryAdded = true
            }

            match.groups[1]?.let {
                entries.add(
                    annotatedString.subSequenceWithoutParagraphStyles(
                        it.range.first,
                        it.range.last + 1
                    )
                )
            }
            match = match.next()
        }
        if (!isEntryAdded) {
            entries.add(AnnotatedString(""))
        }
        return buildAnnotatedString {
            if (0 != selection.start) {
                append(annotatedString.subSequence(0, selection.start))
            }
            append(getUnorderedList(entries))
            if (annotatedString.length != selection.end) {
                append(annotatedString.subSequence(selection.end, annotatedString.length))
            }
        }
    }

    fun markSelectionAsOrderedList(
        annotatedString: AnnotatedString,
        textRange: TextRange,
    ): AnnotatedString {
        if (textRange.length == 0 && textRange.start == annotatedString.length) {
            if (annotatedString.isEmpty() || annotatedString.last() == '\n')
                return addNewOrderedListEntry(annotatedString, textRange.start)
        }
        val selection = patchSelection(annotatedString.text, textRange, lineRegex)
        val startIndex = startIndexForOrderedList(annotatedString.text, selection.start + 1)
        val inputIndices = calcInputIndices(selection, annotatedString.text)
        val entries = mutableListOf<AnnotatedString>()
        inputIndices.forEachReversedIndexed { index, element ->
            if (index != 0) {
                entries.add(
                    annotatedString.subSequenceWithoutParagraphStyles(
                        element,
                        inputIndices[index - 1]
                    )
                )
            }
        }
        entries.add(
            annotatedString.subSequenceWithoutParagraphStyles(
                inputIndices.first(),
                selection.end
            )
        )
        return buildAnnotatedString {
            if (0 != selection.start) {
                append(annotatedString.subSequence(0, selection.start + 1))
            }
            append(getOrderedList(entries, startIndex))
            append(
                updateIndicesIfNeeded(
                    annotatedString.subSequence(selection.end, annotatedString.length),
                    startIndex + inputIndices.size + 1
                )
            )
        }
    }

    fun markSelectionAsUnorderedList(
        annotatedString: AnnotatedString, textRange: TextRange
    ): AnnotatedString {
        val selection = patchSelection(annotatedString.text, textRange, lineRegex)
        val inputIndices = calcInputIndices(selection, annotatedString.text)
        val entries = mutableListOf<AnnotatedString>()
        inputIndices.forEachReversedIndexed { index, element ->
            if (index != 0) {
                entries.add(
                    annotatedString.subSequenceWithoutParagraphStyles(
                        element,
                        inputIndices[index - 1]
                    )
                )
            }
        }
        entries.add(
            annotatedString.subSequenceWithoutParagraphStyles(
                inputIndices.first(),
                selection.end
            )
        )
        val newText = buildAnnotatedString {
            if (0 != selection.start) {
                append(annotatedString.subSequence(0, selection.start + 1))
            }
            append(getUnorderedList(entries))
            if (selection.end != annotatedString.length) {
                append(
                    annotatedString.subSequence(
                        selection.end,
                        annotatedString.length
                    )
                )
            }
        }
        return newText
    }

    fun removeOrderedList(annotatedString: AnnotatedString, selection: TextRange): AnnotatedString {
        val patchedSelection =
            patchSelection(annotatedString.text, selection, orderedListStartRegex)
        return buildAnnotatedString {
            if (patchedSelection.start != 0) {
                append(annotatedString.subSequence(0, patchedSelection.start))
            }
            var match = orderedListEntryRegex.find(
                annotatedString.subSequence(
                    patchedSelection.start,
                    patchedSelection.end
                )
            )
            while (match?.groups?.get(2) != null) {
                val text = match.groups[2]?.let {
                    annotatedString.subSequenceWithoutParagraphStyles(
                        it.range.first,
                        it.range.last + 1
                    )
                } ?: throw IllegalStateException()
                append(text)
                match = match.next()
            }
            append(
                updateIndicesIfNeeded(
                    annotatedString.subSequence(
                        patchedSelection.end,
                        annotatedString.length
                    ), 1
                )
            )
        }
    }

    fun removeUnorderedList(
        annotatedString: AnnotatedString,
        selection: TextRange
    ): Pair<AnnotatedString, TextRange> {
        var newSelection: TextRange = TextRange.Zero
        val newString = buildAnnotatedString {
            append(annotatedString.subSequence(0, selection.start))
            var match = unorderedListEntryRegex.find(
                annotatedString.subSequence(
                    selection.start,
                    selection.end
                )
            )
            var reducedStringLength = 0
            while (match != null) {
                append(match.groups[1]?.let {
                    reducedStringLength += (it.range.last - it.range.first + 1)
                    annotatedString.subSequenceWithoutParagraphStyles(
                        selection.start + it.range.first,
                        selection.start + it.range.last + 1
                    )
                })
                match = match.next()
            }
            append(annotatedString.substring(selection.end))
            newSelection = TextRange(
                selection.start, selection.start + reducedStringLength
            )
        }
        return Pair(newString, newSelection)
    }

    private fun selectOrderedListAtIndex(text: String, index: Int): Pair<TextRange, Int> {
        var i = index - 1
        var selectionStart = index
        var selectionEnd = index
        var startIndex = 1
        while (i >= 0) {
            if (i == 0 || (text[i - 1] == '\n')) {
                if (orderedListEntryRegex.matchesAt(text, i)) {
                    val match = orderedListEntryRegex.matchAt(text, i)
                    selectionStart = i
                    startIndex = match?.groups?.get(1)?.value?.toIntOrNull() ?: 1
                } else {
                    break
                }
            }
            i -= 1
        }
        i = index
        while (i < text.length) {
            if ((text[i] == '\n') && i + 1 < text.length) {
                val match = orderedListEntryRegex.matchAt(text, i + 1)
                if (match != null) {
                    selectionEnd = match.range.last + 1
                } else {
                    break
                }
            }
            i += 1
        }

        return Pair(TextRange(selectionStart, selectionEnd), startIndex)
    }

    private fun selectUnorderedListAtIndex(text: String, index: Int): TextRange {
        var i = index - 1
        var selectionStart = index
        var selectionEnd = index
        while (i >= 0) {
            if (i == 0 || (text[i - 1] == '\n')) {
                if (unorderedListEntryRegex.matchesAt(text, i)) {
                    selectionStart = i
                } else {
                    break
                }
            }
            i -= 1
        }
        i = index
        while (i < text.length) {
            if ((text[i] == '\n') && i + 1 < text.length) {
                val match = unorderedListEntryRegex.matchAt(text, i + 1)
                if (match != null) {
                    selectionEnd = match.range.last + 1
                } else {
                    break
                }
            }
            i += 1
        }

        return TextRange(selectionStart, selectionEnd)
    }


    private fun calcInputIndices(
        textRange: TextRange, text: String
    ): IntList {
        val inputIndices = mutableIntListOf()
        var i = max(textRange.start, textRange.end - 1)
        while (i >= 1) {
            if (text[i - 1] == '\n') {
                inputIndices.add(i)
                if (i < textRange.start) break
            }
            i -= 1
        }
        if (textRange.start == 0) {
            inputIndices.add(0)
        }
        return inputIndices
    }

    private fun startIndexForOrderedList(text: String, startIndex: Int): Int {
        var i = startIndex
        while (i >= 1) {
            if (text[i - 1] == '\n') {
                i -= 1
                break
            }
            i -= 1
        }
        if (i == 0) {
            return 1
        }
        while (i >= 1) {
            if (text[i - 1] == '\n') {
                break
            }
            i -= 1
        }
        val prevOrCurrentLine = text.substring(i, startIndex)
        val match = orderedListStartRegex.find(prevOrCurrentLine) ?: return 1
        return match.groupValues[1].toIntOrNull()?.let {
            it + 1
        } ?: 1
    }

    private fun updateIndicesIfNeeded(text: AnnotatedString, startIndex: Int): AnnotatedString {
        if (text.isEmpty()) return text
        var newLineCharIndex = -1
        var i = 0
        for (char in text) {
            if (char == '\n') {
                newLineCharIndex = i
                break
            }
            i += 1
        }
        if (newLineCharIndex == text.length || !text[newLineCharIndex + 1].isDigit()) return text
        i = 0
        while (i < text.length) {
            if (text[i] == ' ') break
            i += 1
        }
        val firstWord = text.slice(0..i)
        orderedListStartRegex.find(firstWord) ?: return text
        var match = orderedListEntryRegex.find(text)
        val entries = mutableListOf<AnnotatedString>()
        var lastIndex = 0
        while (match != null) {
            if (lastIndex != match.range.first)
                break
            match.groups[2]?.let {
                val subSequence =
                    text.subSequenceWithoutParagraphStyles(it.range.first, it.range.last + 1)
                entries.add(subSequence)
            }
            lastIndex = match.range.last + 1
            match = match.next()
        }
        return buildAnnotatedString {
            append(getOrderedList(entries, startIndex))
            append(text.subSequence(lastIndex, text.length))
        }
    }

    private fun patchSelection(text: String, selection: TextRange, regex: Regex): TextRange {
        var i = selection.start
        while (i > 0) {
            if (regex.matchesAt(text, i)) {
                break
            }
            i -= 1
        }
        val newStart = i
        i = max(selection.start, selection.end)
        while (i < text.length) {
            if (regex.matchesAt(text, i)) {
                i += 1
                break
            }
            if (text[i] == '\n') {
                i += 1
                break
            }
            i += 1
        }
        val newEnd = i
        return TextRange(newStart, newEnd)
    }
}

private fun AnnotatedString.subSequenceWithoutParagraphStyles(
    startIndex: Int,
    endIndex: Int
): AnnotatedString {
    val subSequence = subSequence(startIndex, endIndex)
    var temp = subSequence
    for (styleRange in subSequence.paragraphStyles) {
        temp = temp.withoutParagraphStyle(styleRange)
    }
    return temp
}