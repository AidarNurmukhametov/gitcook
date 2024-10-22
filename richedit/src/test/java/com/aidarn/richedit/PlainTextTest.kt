package com.aidarn.richedit

import androidx.compose.ui.text.AnnotatedString

class PlainTextTest : DecoratedTextTest() {
    override fun transformString(input: String): AnnotatedString {
        return AnnotatedString(input)
    }

    override val input: AnnotatedString = AnnotatedString("test")

}