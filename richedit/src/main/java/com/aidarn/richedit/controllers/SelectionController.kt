package com.aidarn.richedit.controllers

import androidx.compose.ui.text.TextRange
import com.aidarn.richedit.data.StringDiff
import com.aidarn.richedit.data.compare

internal object SelectionController {
    fun processSelection(
        oldSelection: TextRange,
        oldText: String,
        newText: String,
        keepSelection: Boolean
    ): TextRange? {
        val totalDiff = compare(oldText, newText)

        return totalDiff?.let { processSelection(oldSelection, it, keepSelection) }
    }

    fun processSelection(
        oldSelection: TextRange,
        changes: StringDiff,
        keepSelection: Boolean
    ): TextRange {
        return if (!keepSelection) {
            when (changes) {
                is StringDiff.Add -> {
                    changeSelection(oldSelection, changes)
                }

                is StringDiff.Remove -> {
                    changeSelection(oldSelection, changes)
                }

                is StringDiff.Replace -> {
                    changeSelection(oldSelection, changes)
                }
            }
        } else {
            when (changes) {
                is StringDiff.Add -> {
                    keepSelection(oldSelection, changes)
                }

                is StringDiff.Remove -> {
                    keepSelection(oldSelection, changes)
                }

                is StringDiff.Replace -> {
                    keepSelection(oldSelection, changes)
                }
            }
        }
    }
}

private fun keepSelection(selection: TextRange, changes: StringDiff.Add) =
    if (selection.end < changes.start) {
        selection
    } else {
        TextRange(selection.start + changes.string.length, selection.end + changes.string.length)
    }

private fun keepSelection(selection: TextRange, changes: StringDiff.Remove) =
    if (selection.end <= changes.start) {
        selection
    } else if (selection.start <= changes.start && selection.end >= changes.end) {
        val newEnd = selection.end - (changes.end - changes.start)
        TextRange(selection.start, newEnd)
    } else if (selection.start >= changes.end) {
        val newStart = selection.start - (changes.end - changes.start)
        val newEnd = selection.end - (changes.end - changes.start)
        TextRange(newStart, newEnd)
    } else selection

private fun keepSelection(selection: TextRange, changes: StringDiff.Replace) =
    if (selection.end < changes.start) {
        selection
    } else if (selection.start <= changes.start && selection.end >= changes.end) {
        val newEnd = selection.end + changes.string.length - (changes.end - changes.start)
        TextRange(selection.start, newEnd)
    } else if (selection.start <= changes.start) {
        TextRange(changes.start, changes.start + changes.string.length - 1)
    } else if (selection.start >= changes.end) {
        val shift = changes.string.length - (changes.end - changes.start)
        val newStart = selection.start + shift
        val newEnd = selection.end + shift
        TextRange(newStart, newEnd)
    } else selection

private fun changeSelection(selection: TextRange, changes: StringDiff.Replace) =
    if (selection.end < changes.start) {
        selection
    } else if (selection.start <= changes.start && selection.end >= changes.end) {
        val newEnd = selection.end + changes.string.length - (changes.end - changes.start)
        TextRange(newEnd, newEnd)
    } else if (selection.start >= changes.end) {
        val shift = changes.string.length - (changes.end - changes.start)
        val newStart = selection.start + shift
        val newEnd = selection.end + shift
        TextRange(newStart, newEnd)
    } else selection

private fun changeSelection(selection: TextRange, changes: StringDiff.Remove) =
    if (selection.end <= changes.start) {
        selection
    } else if (selection.start <= changes.start && selection.end >= changes.end) {
        val newEnd = selection.end - (changes.end - changes.start)
        TextRange(selection.start, newEnd)
    } else if (selection.start >= changes.end) {
        val newStart = selection.start - (changes.end - changes.start)
        val newEnd = selection.end - (changes.end - changes.start)
        TextRange(newStart, newEnd)
    } else selection

private fun changeSelection(selection: TextRange, changes: StringDiff.Add) =
    if (selection.end < changes.start) {
        selection
    } else {
        TextRange(changes.start + changes.string.length, changes.start + changes.string.length)
    }