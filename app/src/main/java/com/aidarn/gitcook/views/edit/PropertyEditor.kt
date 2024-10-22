package com.aidarn.gitcook.views.edit

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType


@Composable
inline fun <reified T> PropertyEditor(
    value: T?,
    default: T?,
    crossinline parse: (String) -> T?,
    valueDesc: String,
    crossinline onValueChanged: (T?) -> Unit,
    modifier: Modifier = Modifier,
    keyboardOptions: KeyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
    noinline suffix: @Composable () -> Unit = { }
) {
    OutlinedTextField(
        value = value?.toString() ?: "",
        label = { Text(text = valueDesc) },
        suffix = suffix,
        onValueChange = {
            val newValue = try {
                parse(it)
            } catch (_: Exception) {
                default
            }
            onValueChanged(newValue)
        },
        keyboardOptions = keyboardOptions,
        modifier = modifier
    )
}

@Composable
inline fun IntPropertyEditor(
    value: Int?,
    valueDesc: String,
    crossinline onValueChanged: (Int?) -> Unit,
    modifier: Modifier,
    noinline suffix: @Composable () -> Unit = { }
) {
    PropertyEditor(
        value = value,
        default = null,
        parse = { it.toIntOrNull() },
        valueDesc = valueDesc,
        onValueChanged = onValueChanged,
        modifier = modifier,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Number,
            imeAction = ImeAction.Next
        ),
        suffix = suffix
    )
}