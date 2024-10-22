package com.aidarn.richedit

import androidx.compose.ui.text.AnnotatedString
import com.aidarn.richedit.data.getHeading1Text
import com.aidarn.richedit.data.getHeading2Text

class Heading1PlainTest : DecoratedTextTest() {
    override val input: AnnotatedString = getHeading1Text("test")

    override fun transformString(input: String): AnnotatedString {
        return getHeading1Text(input)
    }
}

class Heading2PlainTest : DecoratedTextTest() {
    override val input: AnnotatedString = getHeading2Text("test")

    override fun transformString(input: String): AnnotatedString {
        return getHeading2Text(input)
    }
}
