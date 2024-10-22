package com.aidarn.richedit.data

import com.aidarn.gitcook.data.ComplexTypeConverters
import com.aidarn.richedit.ext.toMarkupString


class ValueChangedVisitor : IRenderElementVisitor {
    private val stringBuilder = StringBuilder()
    override fun visit(textElement: RenderElement.Text) {
        stringBuilder.append(textElement.content.annotatedString.toMarkupString())
    }

    override fun visit(timerElement: RenderElement.Timer) {
        if (stringBuilder.lastOrNull() != '\n')
            stringBuilder.appendLine()
        stringBuilder.appendLine(">!TIMER ${timerElement.content.toIsoString()}")
    }

    override fun visit(imageElement: RenderElement.Image) {
        if (stringBuilder.lastOrNull() != '\n')
            stringBuilder.appendLine()
        stringBuilder.appendLine(
            "![${imageElement.placeholder}](${
                ComplexTypeConverters.fromAny(
                    imageElement.content
                )
            })"
        )
    }

    fun getMarkupString(): String {
        return stringBuilder.toString()
    }

    fun reset() {
        stringBuilder.clear()
    }
}
