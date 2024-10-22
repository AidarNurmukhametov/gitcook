package com.aidarn.richedit.controllers

import androidx.compose.ui.text.input.TextFieldValue
import com.aidarn.richedit.data.compare

object InteractionController {
    fun removeOrderedList(textFieldValue: TextFieldValue): TextFieldValue {
        val newString =
            ListHelper.removeOrderedList(textFieldValue.annotatedString, textFieldValue.selection)
        val changes = compare(textFieldValue.text, newString.text) ?: return textFieldValue
        val newSelection =
            SelectionController.processSelection(textFieldValue.selection, changes, true)
        return textFieldValue.copy(annotatedString = newString, selection = newSelection)
    }

    fun startOrderedList(
        textFieldValue: TextFieldValue
    ): TextFieldValue {
        val newText =
            ListHelper.markSelectionAsOrderedList(
                textFieldValue.annotatedString,
                textFieldValue.selection
            )
        val newSelection = SelectionController.processSelection(
            textFieldValue.selection,
            textFieldValue.text,
            newText.text,
            true
        ) ?: textFieldValue.selection
        return textFieldValue.copy(annotatedString = newText, selection = newSelection)
    }

    fun startUnorderedList(textFieldValue: TextFieldValue): TextFieldValue {
        val newText = ListHelper.markSelectionAsUnorderedList(
            textFieldValue.annotatedString,
            textFieldValue.selection
        )
        val newSelection = SelectionController.processSelection(
            textFieldValue.selection,
            textFieldValue.text,
            newText.text,
            true
        ) ?: textFieldValue.selection
        return textFieldValue.copy(annotatedString = newText, selection = newSelection)
    }

    fun removeUnorderedList(textFieldValue: TextFieldValue): TextFieldValue {
        val (newString, newSelection) = ListHelper.removeUnorderedList(
            textFieldValue.annotatedString,
            textFieldValue.selection
        )
        return textFieldValue.copy(annotatedString = newString, selection = newSelection)
    }
}