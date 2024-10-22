package com.aidarn.richedit

import androidx.compose.ui.text.ParagraphStyle
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextIndent
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType


internal enum class StyleType {
    Plain, Bold, Italic, Heading1, Heading2
}

internal data object Style {
    private val map: Map<StyleType, SpanStyle> = mapOf(
        Pair(StyleType.Bold, SpanStyle(fontWeight = FontWeight.Bold)),
        Pair(StyleType.Italic, SpanStyle(fontStyle = FontStyle.Italic)),
        Pair(StyleType.Heading1, SpanStyle(fontSize = Constants.LARGE_TITLE_SIZE)),
        Pair(
            StyleType.Heading2,
            SpanStyle(fontSize = Constants.MEDIUM_TITLE_SIZE, fontWeight = FontWeight.Medium)
        )
    )
    val listStyle =
        ParagraphStyle(textIndent = TextIndent(firstLine = TextUnit(4f, TextUnitType.Sp)))

    operator fun get(key: StyleType): SpanStyle {
        return checkNotNull(map[key])
    }

    fun firstMatchingKey(item: SpanStyle): StyleType {
        return map.entries.first { it.value == item }.key
    }
}