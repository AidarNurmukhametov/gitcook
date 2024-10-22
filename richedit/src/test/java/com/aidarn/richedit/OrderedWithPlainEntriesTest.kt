package com.aidarn.richedit

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.buildAnnotatedString
import com.aidarn.richedit.data.getOrderedList

class OrderedWithPlainEntriesTest : BaseUnitTest() {
    override val input: AnnotatedString = getOrderedList(listOf("first\n", "second"))

    override fun addCharAtStartTestData(): Pair<String, AnnotatedString> {
        return Pair("a1. first\n2. second", buildAnnotatedString {
            append("a1. first\n")
            append(getOrderedList(listOf("second"), 2))
        })
    }

    override fun addCharInMiddleTestData(): Pair<String, AnnotatedString> {
        return Pair("1. afirst\n2. second", getOrderedList(listOf("afirst\n", "second")))
    }

    override fun addCharAtEndTestData(): Pair<String, AnnotatedString> {
        return Pair("1. first\n2. seconda", getOrderedList(listOf("first\n", "seconda")))
    }

    override fun addStringAtStartTestData(): Pair<String, AnnotatedString> {
        return Pair("aa1. first\n2. second", buildAnnotatedString {
            append("aa1. first\n")
            append(getOrderedList(listOf("second"), 2))
        })
    }

    override fun addStringInMiddleTestData(): Pair<String, AnnotatedString> {
        return Pair("1. aafirst\n2. second", getOrderedList(listOf("aafirst\n", "second")))
    }

    override fun addStringAtEndTestData(): Pair<String, AnnotatedString> {
        return Pair("1. first\n2. secondaa", getOrderedList(listOf("first\n", "secondaa")))
    }

    override fun removeCharAtStartTestData(): Pair<String, AnnotatedString> {
        return Pair(". first\n2. second", buildAnnotatedString {
            append(". first\n")
            append(getOrderedList(listOf("second"), 2))
        })
    }

    override fun removeCharInMiddleTestData(): Pair<String, AnnotatedString> {
        return Pair("1. first2. second", getOrderedList(listOf("first2. second")))
    }

    override fun removeCharAtEndTestData(): Pair<String, AnnotatedString> {
        return Pair("1. first\n2. secon", getOrderedList(listOf("first\n", "secon")))
    }

    override fun removeStringAtStartTestData(): Pair<String, AnnotatedString> {
        return Pair("first\n2. second", buildAnnotatedString {
            append("first\n")
            append(getOrderedList(listOf("second"), 2))
        })
    }

    override fun removeStringInMiddleTestData(): Pair<String, AnnotatedString> {
        return Pair("1. firstsecond", getOrderedList(listOf("firstsecond")))
    }

    override fun removeStringAtEndTestData(): Pair<String, AnnotatedString> {
        return Pair("1. first\n2. sec", getOrderedList(listOf("first\n", "sec")))
    }

    override fun replaceCharAtStartTestData(): Pair<String, AnnotatedString> {
        return Pair("2. first\n2. second", getOrderedList(listOf("first\n", "second"), 2))
    }

    override fun replaceCharInMiddleTestData(): Pair<String, AnnotatedString> {
        return Pair("1. first\n3. second", getOrderedList(listOf("first\n", "second")))
    }

    override fun replaceCharAtEndTestData(): Pair<String, AnnotatedString> {
        return Pair("1. first\n2. secona", getOrderedList(listOf("first\n", "secona")))
    }

    override fun replaceStringAtStartTestData(): Pair<String, AnnotatedString> {
        return Pair("aa. first\n2. second", buildAnnotatedString {
            append("aa. first\n")
            append(getOrderedList(listOf("second"), 2))
        })
    }

    override fun replaceStringInMiddleTestData(): Pair<String, AnnotatedString> {
        return Pair("1. first\naa. second", buildAnnotatedString {
            append(getOrderedList(listOf("first\n")))
            append("aa. second")
        })
    }

    override fun replaceStringAtEndTestData(): Pair<String, AnnotatedString> {
        return Pair("1. first\n2. secoaa", getOrderedList(listOf("first\n", "secoaa")))
    }

    override fun addNewlineAtStartTestData(): Pair<String, AnnotatedString> {
        return Pair("\n1. first\n2. second", buildAnnotatedString {
            append("\n")
            append(getOrderedList(listOf("first\n", "second")))
        })
    }

    override fun addNewlineInMiddleTestData(): Pair<String, AnnotatedString> {
        return Pair("1. first\n\n2. second", getOrderedList(listOf("first\n", "\n", "second")))
    }

    override fun addNewlineAtEndTestData(): Pair<String, AnnotatedString> {
        return Pair("1. first\n2. second\n", getOrderedList(listOf("first\n", "second\n", "")))
    }
}
