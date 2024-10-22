package com.aidarn.richedit

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import com.aidarn.richedit.data.Markup.Companion.toMarkup
import com.aidarn.richedit.data.RenderElement
import com.aidarn.richedit.display.ImageDisplay
import com.aidarn.richedit.display.TextDisplay
import com.aidarn.richedit.display.TimerDisplay

@Composable
fun String.AsComposable() {
    val elements = toMarkup().toRenderElements()
    Column {
        elements.forEach {
            when (it) {
                is RenderElement.Text -> {
                    TextDisplay(it)
                }

                is RenderElement.Image -> {
                    ImageDisplay(it)
                }

                is RenderElement.Timer -> {
                    TimerDisplay(it)
                }
            }
        }
    }
}