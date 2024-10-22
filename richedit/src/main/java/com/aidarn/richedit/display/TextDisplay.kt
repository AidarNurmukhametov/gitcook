package com.aidarn.richedit.display

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.aidarn.richedit.data.RenderElement


@Composable
internal fun TextDisplay(item: RenderElement.Text, modifier: Modifier = Modifier) {
    Text(text = item.content.annotatedString, modifier = modifier)
}