package com.aidarn.richedit

import androidx.compose.ui.text.AnnotatedString
import com.aidarn.richedit.data.getBoldText

class BoldTextTest : DecoratedTextTest() {
    override val input: AnnotatedString = getBoldText("test")

    override fun transformString(input: String): AnnotatedString {
        return getBoldText(input)
    }
}
