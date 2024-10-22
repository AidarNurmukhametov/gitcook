package com.aidarn.richedit

import androidx.compose.ui.text.AnnotatedString
import com.aidarn.richedit.data.getBoldText
import com.aidarn.richedit.data.getItalicText

class BoldItalicTextTest : DecoratedTextTest() {
    override val input: AnnotatedString = getBoldText(getItalicText("test"))

    override fun transformString(input: String): AnnotatedString {
        return getBoldText(getItalicText(input))
    }
}