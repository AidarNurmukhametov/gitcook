package com.aidarn.richedit

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.buildAnnotatedString
import com.aidarn.richedit.data.getBoldText
import com.aidarn.richedit.data.getItalicText
import com.aidarn.richedit.data.getUnorderedList

class UnorderedWithBoldItalicEntriesTest : BaseUnitTest() {
    override val input: AnnotatedString = getUnorderedList(
        listOf(
            getBoldItalicText("first\n"),
            getBoldItalicText("second")
        )
    )

    override fun addCharAtStartTestData(): Pair<String, AnnotatedString> {
        return Pair("a\u2022 first\n\u2022 second", buildAnnotatedString {
            append("a\u2022 ")
            append(getBoldItalicText("first\n"))
            append(getUnorderedList(listOf(getBoldItalicText("second"))))
        })
    }

    override fun addCharInMiddleTestData(): Pair<String, AnnotatedString> {
        return Pair(
            "\u2022 afirst\n\u2022 second", getUnorderedList(
                listOf(
                    getBoldItalicText("afirst\n"),
                    getBoldItalicText("second")
                )
            )
        )
    }

    override fun addCharAtEndTestData(): Pair<String, AnnotatedString> {
        return Pair(
            "\u2022 first\n\u2022 seconda", getUnorderedList(
                listOf(
                    getBoldItalicText("first\n"),
                    getBoldItalicText("seconda")
                )
            )
        )
    }

    override fun addStringAtStartTestData(): Pair<String, AnnotatedString> {
        return Pair("aa\u2022 first\n\u2022 second", buildAnnotatedString {
            append("aa\u2022 ")
            append(getBoldItalicText("first\n"))
            append(getUnorderedList(listOf(getBoldItalicText("second"))))
        })
    }

    override fun addStringInMiddleTestData(): Pair<String, AnnotatedString> {
        return Pair(
            "\u2022 aafirst\n\u2022 second", getUnorderedList(
                listOf(
                    getBoldItalicText("aafirst\n"),
                    getBoldItalicText("second")
                )
            )
        )
    }

    override fun addStringAtEndTestData(): Pair<String, AnnotatedString> {
        return Pair(
            "\u2022 first\n\u2022 secondaa", getUnorderedList(
                listOf(
                    getBoldItalicText("first\n"),
                    getBoldItalicText("secondaa")
                )
            )
        )
    }

    override fun removeCharAtStartTestData(): Pair<String, AnnotatedString> {
        return Pair(" first\n\u2022 second", buildAnnotatedString {
            append(" ")
            append(getBoldItalicText("first\n"))
            append(getUnorderedList(listOf(getBoldItalicText("second"))))
        })
    }

    override fun removeCharInMiddleTestData(): Pair<String, AnnotatedString> {
        return Pair("\u2022 first\n second", buildAnnotatedString {
            append(getUnorderedList(listOf(getBoldItalicText("first\n"))))
            append(" ")
            append(getBoldItalicText("second"))
        })
    }

    override fun removeCharAtEndTestData(): Pair<String, AnnotatedString> {
        return Pair(
            "\u2022 first\n\u2022 secon", getUnorderedList(
                listOf(
                    getBoldItalicText("first\n"),
                    getBoldItalicText("secon")
                )
            )
        )
    }

    override fun removeStringAtStartTestData(): Pair<String, AnnotatedString> {
        return Pair("first\n\u2022 second", buildAnnotatedString {
            append(getBoldItalicText("first\n"))
            append(getUnorderedList(listOf(getBoldItalicText("second"))))
        })
    }

    override fun removeStringInMiddleTestData(): Pair<String, AnnotatedString> {
        return Pair("\u2022 first\nsecond", buildAnnotatedString {
            append(getUnorderedList(listOf(getBoldItalicText("first\n"))))
            append(getBoldItalicText("second"))
        })
    }

    override fun removeStringAtEndTestData(): Pair<String, AnnotatedString> {
        return Pair(
            "\u2022 first\n\u2022 sec", getUnorderedList(
                listOf(
                    getBoldItalicText("first\n"),
                    getBoldItalicText("sec")
                )
            )
        )
    }

    override fun replaceCharAtStartTestData(): Pair<String, AnnotatedString> {
        return Pair("\u2023 first\n\u2022 second", buildAnnotatedString {
            append("\u2023 ")
            append(getBoldItalicText("first\n"))
            append(getUnorderedList(listOf(getBoldItalicText("second"))))
        })
    }

    override fun replaceCharInMiddleTestData(): Pair<String, AnnotatedString> {
        return Pair("\u2022 first\n\u2023 second", buildAnnotatedString {
            append(getUnorderedList(listOf(getBoldItalicText("first\n"))))
            append("\u2023 ")
            append(getBoldItalicText("second"))
        })
    }

    override fun replaceCharAtEndTestData(): Pair<String, AnnotatedString> {
        return Pair(
            "\u2022 first\n\u2022 secon\u2023", getUnorderedList(
                listOf(
                    getBoldItalicText("first\n"),
                    getBoldItalicText("secon\u2023")
                )
            )
        )
    }

    override fun replaceStringAtStartTestData(): Pair<String, AnnotatedString> {
        return Pair("1. first\n\u2022 second", buildAnnotatedString {
            pushStyle(Style.listStyle)
            append("1. ")
            append(getBoldItalicText("first\n"))
            append("\u2022 ")
            append(getBoldItalicText("second"))
            pop()
        })
    }

    override fun replaceStringInMiddleTestData(): Pair<String, AnnotatedString> {
        return Pair("\u2022 first\n1. second", buildAnnotatedString {
            pushStyle(Style.listStyle)
            append("\u2022 ")
            append(getBoldItalicText("first\n"))
            append("1. ")
            append(getBoldItalicText("second"))
            pop()
        })
    }

    override fun replaceStringAtEndTestData(): Pair<String, AnnotatedString> {
        return Pair(
            "\u2022 first\n\u2022 secoaa", getUnorderedList(
                listOf(
                    getBoldItalicText("first\n"),
                    getBoldItalicText("secoaa")
                )
            )
        )
    }

    override fun addNewlineAtStartTestData(): Pair<String, AnnotatedString> {
        return Pair("\n\u2022 first\n\u2022 second", buildAnnotatedString {
            append("\n")
            append(
                getUnorderedList(
                    listOf(
                        getBoldItalicText("first\n"),
                        getBoldItalicText("second")
                    )
                )
            )
        })
    }

    override fun addNewlineInMiddleTestData(): Pair<String, AnnotatedString> {
        return Pair(
            "\u2022 first\n\n\u2022 second", getUnorderedList(
                listOf(
                    getBoldItalicText("first\n"),
                    "\n",
                    getBoldItalicText("second")
                )
            )
        )
    }

    override fun addNewlineAtEndTestData(): Pair<String, AnnotatedString> {
        return Pair(
            "\u2022 first\n\u2022 second\n", getUnorderedList(
                listOf(
                    getBoldItalicText("first\n"),
                    getBoldItalicText("second\n"),
                    ""
                )
            )
        )
    }

    private fun getBoldItalicText(text: String): AnnotatedString {
        return getBoldText(getItalicText(text))
    }
}
