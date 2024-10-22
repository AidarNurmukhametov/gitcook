package com.aidarn.richedit

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.buildAnnotatedString
import com.aidarn.richedit.data.getUnorderedList

class UnorderedWithPlainEntriesTest : BaseUnitTest() {
    override val input: AnnotatedString = getUnorderedList(listOf("first\n", "second"))

    override fun addCharAtStartTestData(): Pair<String, AnnotatedString> {
        return Pair("a\u2022 first\n\u2022 second", buildAnnotatedString {
            append("a\u2022 first\n")
            append(getUnorderedList(listOf("second")))
        })
    }

    override fun addCharInMiddleTestData(): Pair<String, AnnotatedString> {
        return Pair("\u2022 afirst\n\u2022 second", getUnorderedList(listOf("afirst\n", "second")))
    }

    override fun addCharAtEndTestData(): Pair<String, AnnotatedString> {
        return Pair("\u2022 first\n\u2022 seconda", getUnorderedList(listOf("first\n", "seconda")))
    }

    override fun addStringAtStartTestData(): Pair<String, AnnotatedString> {
        return Pair("aa\u2022 first\n\u2022 second", buildAnnotatedString {
            append("aa\u2022 first\n")
            append(getUnorderedList(listOf("second")))
        })
    }

    override fun addStringInMiddleTestData(): Pair<String, AnnotatedString> {
        return Pair(
            "\u2022 aafirst\n\u2022 second",
            getUnorderedList(listOf("aafirst\n", "second"))
        )
    }

    override fun addStringAtEndTestData(): Pair<String, AnnotatedString> {
        return Pair(
            "\u2022 first\n\u2022 secondaa",
            getUnorderedList(listOf("first\n", "secondaa"))
        )
    }

    override fun removeCharAtStartTestData(): Pair<String, AnnotatedString> {
        return Pair(" first\n\u2022 second", buildAnnotatedString {
            append(" first\n")
            append(getUnorderedList(listOf("second")))
        })
    }

    override fun removeCharInMiddleTestData(): Pair<String, AnnotatedString> {
        return Pair("\u2022 first\u2022 second", getUnorderedList(listOf("first\u2022 second")))
    }

    override fun removeCharAtEndTestData(): Pair<String, AnnotatedString> {
        return Pair("\u2022 first\n\u2022 secon", getUnorderedList(listOf("first\n", "secon")))
    }

    override fun removeStringAtStartTestData(): Pair<String, AnnotatedString> {
        return Pair("first\n\u2022 second", buildAnnotatedString {
            append("first\n")
            append(getUnorderedList(listOf("second")))
        })
    }

    override fun removeStringInMiddleTestData(): Pair<String, AnnotatedString> {
        return Pair("\u2022 firstsecond", getUnorderedList(listOf("firstsecond")))
    }

    override fun removeStringAtEndTestData(): Pair<String, AnnotatedString> {
        return Pair("\u2022 first\n\u2022 sec", getUnorderedList(listOf("first\n", "sec")))
    }

    override fun replaceCharAtStartTestData(): Pair<String, AnnotatedString> {
        return Pair("\u2023 first\n\u2022 second", buildAnnotatedString {
            append("\u2023 first\n")
            append(getUnorderedList(listOf("second")))
        })
    }

    override fun replaceCharInMiddleTestData(): Pair<String, AnnotatedString> {
        return Pair("\u2022 first\n\u2023 second", buildAnnotatedString {
            append(getUnorderedList(listOf("first\n")))
            append("\u2023 second")
        })
    }

    override fun replaceCharAtEndTestData(): Pair<String, AnnotatedString> {
        return Pair("\u2022 first\n\u2022 seconb", getUnorderedList(listOf("first\n", "seconb")))
    }

    override fun replaceStringAtStartTestData(): Pair<String, AnnotatedString> {
        return Pair("1. first\n\u2022 second", buildAnnotatedString {
            pushStyle(Style.listStyle)
            append("1. first\n")
            append("\u2022 second")
            pop()
        })
    }

    override fun replaceStringInMiddleTestData(): Pair<String, AnnotatedString> {
        return Pair("\u2022 first\n1. second", buildAnnotatedString {
            pushStyle(Style.listStyle)
            append("\u2022 first\n")
            append("1. second")
            pop()
        })
    }

    override fun replaceStringAtEndTestData(): Pair<String, AnnotatedString> {
        return Pair("\u2022 first\n\u2022 secoaa", getUnorderedList(listOf("first\n", "secoaa")))
    }

    override fun addNewlineAtStartTestData(): Pair<String, AnnotatedString> {
        return Pair("\n\u2022 first\n\u2022 second", buildAnnotatedString {
            append("\n")
            append(getUnorderedList(listOf("first\n", "second")))
        })
    }

    override fun addNewlineInMiddleTestData(): Pair<String, AnnotatedString> {
        return Pair(
            "\u2022 first\n\n\u2022 second",
            getUnorderedList(listOf("first\n", "\n", "second"))
        )
    }

    override fun addNewlineAtEndTestData(): Pair<String, AnnotatedString> {
        return Pair(
            "\u2022 first\n\u2022 second\n",
            getUnorderedList(listOf("first\n", "second\n", ""))
        )
    }
}
