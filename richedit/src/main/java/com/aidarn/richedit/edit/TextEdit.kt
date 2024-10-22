package com.aidarn.richedit.edit

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.takeOrElse
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.TransformedText


@OptIn(ExperimentalFoundationApi::class)
@Composable
internal fun TextEdit(
    value: TextFieldValue,
    bringIntoViewRequester: BringIntoViewRequester,
    onTextLayout: (TextLayoutResult) -> Unit,
    onValueChanged: (TextFieldValue) -> Unit,
    modifier: Modifier = Modifier
) {
    var textFieldValue by remember { mutableStateOf(value) }
    if (textFieldValue.text != value.text) {
        textFieldValue = textFieldValue.copy(annotatedString = value.annotatedString)
    }
    if (textFieldValue.selection != value.selection) {
        textFieldValue = textFieldValue.copy(selection = value.selection)
    }
    val interactionSource = remember { MutableInteractionSource() }
    val textStyle = LocalTextStyle.current
    val colors = OutlinedTextFieldDefaults.colors()
    val textColor = textStyle.color.takeOrElse {
        val focused = interactionSource.collectIsFocusedAsState().value
        colors.run {
            if (focused)
                this.focusedTextColor
            else
                this.unfocusedTextColor
        }
    }
    val mergedTextStyle = textStyle.merge(TextStyle(color = textColor))
    BasicTextField(
        value = textFieldValue,
        modifier = modifier
            .fillMaxWidth()
            .bringIntoViewRequester(bringIntoViewRequester),
        onValueChange = {
            onValueChanged(it)
            textFieldValue = it
        },
        textStyle = mergedTextStyle,
        cursorBrush = SolidColor(colors.cursorColor),
        visualTransformation = { _ ->
            TransformedText(value.annotatedString, offsetMapping = OffsetMapping.Identity)
        },
        onTextLayout = onTextLayout,
        interactionSource = interactionSource,
    )
}
