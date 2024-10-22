package com.aidarn.gitcook.views.edit

import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction


@Composable
internal fun NewItemEntryField(
    placeholder: String, modifier: Modifier = Modifier, onSubmit: (String) -> Unit
) {
    var value by remember { mutableStateOf("") }
    OutlinedTextField(
        value = value,
        onValueChange = {
            value = it
        },
        placeholder = {
            Text(text = placeholder)
        },
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
        keyboardActions = KeyboardActions(onDone = {
            if (value.isNotBlank()) {
                onSubmit(value)
                value = ""
            }
        }),
        singleLine = true,
        modifier = modifier
    )
}
