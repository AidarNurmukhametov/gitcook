package com.aidarn.richedit.data

import androidx.compose.runtime.Immutable
import androidx.compose.ui.text.input.TextFieldValue
import com.aidarn.richedit.ext.applyTextAndChanges
import kotlin.time.Duration

sealed interface RenderElement {
    fun acceptVisitor(visitor: IRenderElementVisitor)

    @Immutable
    data class Text(val content: TextFieldValue) : RenderElement {
        override fun acceptVisitor(visitor: IRenderElementVisitor) {
            visitor.visit(this)
        }

        fun withText(newText: String, keepSelection: Boolean = false): Text {
            val changes = compare(content.text, newText) ?: return this
            val newContent = content.applyTextAndChanges(newText, changes, keepSelection)

            return Text(newContent)
        }
    }

    @Immutable
    data class Timer(val content: Duration) : RenderElement {
        override fun acceptVisitor(visitor: IRenderElementVisitor) {
            visitor.visit(this)
        }
    }

    @Immutable
    data class Image(val content: Any, val placeholder: String) : RenderElement {
        override fun acceptVisitor(visitor: IRenderElementVisitor) {
            visitor.visit(this)
        }
    }
}

interface IRenderElementVisitor {
    fun visit(textElement: RenderElement.Text)
    fun visit(timerElement: RenderElement.Timer)
    fun visit(imageElement: RenderElement.Image)
}
