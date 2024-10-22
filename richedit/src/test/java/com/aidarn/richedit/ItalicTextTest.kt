package com.aidarn.richedit

import androidx.compose.ui.text.AnnotatedString
import com.aidarn.richedit.data.getItalicText

class ItalicTextTest : DecoratedTextTest() {
    override val input: AnnotatedString = getItalicText("test")

    override fun transformString(input: String): AnnotatedString {
        return getItalicText(input)
    }
}
