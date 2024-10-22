package com.aidarn.gitcook.views.edit

import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier

@Composable
fun SimpleEditor(
    input: String,
    onInputChanged: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var text by remember { mutableStateOf(input) }
    OutlinedTextField(
        value = text,
        onValueChange = {
            onInputChanged(it)
            text = it
        },
        modifier = modifier
    )
}