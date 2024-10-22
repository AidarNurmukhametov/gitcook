package com.aidarn.richedit

import androidx.compose.ui.text.AnnotatedString
import com.aidarn.richedit.data.getBoldText
import com.aidarn.richedit.data.getHeading1Text
import com.aidarn.richedit.data.getHeading2Text
import com.aidarn.richedit.data.getItalicText

class Heading1BoldItalicTest : DecoratedTextTest() {
    override val input: AnnotatedString = getHeading1Text(getBoldText(getItalicText("test")))

    override fun transformString(input: String): AnnotatedString {
        return getHeading1Text(getBoldText(getItalicText(input)))
    }
}

class Heading2BoldItalicTest : DecoratedTextTest() {
    override val input: AnnotatedString = getHeading2Text(getBoldText(getItalicText("test")))

    override fun transformString(input: String): AnnotatedString {
        return getHeading2Text(getBoldText(getItalicText(input)))
    }
}
