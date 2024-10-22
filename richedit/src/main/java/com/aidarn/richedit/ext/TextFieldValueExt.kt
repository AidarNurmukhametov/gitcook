package com.aidarn.richedit.ext

import androidx.compose.ui.text.input.TextFieldValue
import com.aidarn.richedit.controllers.SelectionController
import com.aidarn.richedit.data.StringDiff


internal fun TextFieldValue.applyTextAndChanges(
    newText: String,
    changes: StringDiff,
    keepSelection: Boolean = false
): TextFieldValue {
    val newSelection = SelectionController.processSelection(
        selection, changes,
        keepSelection
    )
    return copy(
        annotatedString = annotatedString.applyTextAndChanges(newText, changes),
        selection = newSelection
    )
}
