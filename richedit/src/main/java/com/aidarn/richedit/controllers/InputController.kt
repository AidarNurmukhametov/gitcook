package com.aidarn.richedit.controllers

import androidx.compose.ui.text.AnnotatedString
import androidx.core.math.MathUtils.clamp
import com.aidarn.richedit.data.Markup.Companion.toMarkup
import com.aidarn.richedit.data.Pattern
import com.aidarn.richedit.data.StringDiff
import com.aidarn.richedit.data.compare
import com.aidarn.richedit.data.toAnnotatedString
import com.aidarn.richedit.ext.applyTextAndChanges
import com.aidarn.richedit.ext.applyTextAndMergeStyles
import com.aidarn.richedit.ext.mergeStyles
import kotlin.math.min


internal object InputController {
    private val unorderedListEntryRegex = Pattern.Entry.UnorderedList
    private val orderedListEntryRegex = Pattern.Entry.OrderedList

    fun processText(oldText: AnnotatedString, newText: String): AnnotatedString {
        val processedText = processTextCore(oldText, newText)

        return processedText
    }

    private fun processTextCore(
        oldText: AnnotatedString,
        newText: String
    ): AnnotatedString {
        val preprocessText = parseMarkupElements(newText)
        val changes =
            compare(oldText.text, preprocessText.text) ?: return oldText.mergeStyles(preprocessText)
        val tempResult = oldText.applyTextAndMergeStyles(preprocessText, changes)
        val processedText = processTextChange(tempResult, changes) ?: return tempResult
        return processedText
    }

    private fun parseMarkupElements(value: String): AnnotatedString {
        return value
            .replace(Pattern.Head.ProcessedUnorderedList) {
                "- "
            }
            .toMarkup()
            .toAnnotatedString()
    }

    private fun processTextChange(text: AnnotatedString, changes: StringDiff): AnnotatedString? {
        if (changes is StringDiff.Remove) return null
        if (changes is StringDiff.Add && changes.string != "\n") return null
        if (changes is StringDiff.Replace && changes.string != "\n") return null

        var i = changes.start - 1
        while (i >= 1) {
            if (text[i - 1] == '\n') {
                break
            }
            i -= 1
        }
        i = clamp(i, 0, changes.start)
        val line = text.substring(i, changes.start)
        val orderedListMatch = orderedListEntryRegex.find(line)
        if (orderedListMatch != null) {
            val isEntryEmpty = orderedListMatch.groups[2]?.value?.isEmpty()
            if (isEntryEmpty == true) {
                val endStart = min(changes.start + 2, text.length)
                val newText = buildString {
                    append(text.text.subSequence(0, i))
                    append(text.text.subSequence(endStart, text.length))
                }
                return text.applyTextAndChanges(newText, StringDiff.Remove(i, endStart))
            }
            return ListHelper.addNewOrderedListEntry(text, changes.start + 1)
        }
        val unorderedListMatch = unorderedListEntryRegex.find(line) ?: return null
        val isEntryEmpty = unorderedListMatch.groups[1]?.value?.isEmpty()
        if (isEntryEmpty == true) {
            val endStart = min(changes.start + 2, text.length)
            val newText = buildString {
                append(text.text.subSequence(0, i))
                append(text.text.subSequence(endStart, text.length))
            }
            return text.applyTextAndChanges(newText, StringDiff.Remove(i, endStart))
        }
        return ListHelper.addNewUnorderedListEntry(text, changes.start + 1)
    }
}