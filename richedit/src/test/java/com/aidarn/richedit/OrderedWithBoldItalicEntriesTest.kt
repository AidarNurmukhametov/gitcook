package com.aidarn.richedit

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.buildAnnotatedString
import com.aidarn.richedit.data.getBoldText
import com.aidarn.richedit.data.getItalicText
import com.aidarn.richedit.data.getOrderedList

class OrderedWithBoldItalicEntriesTest : BaseUnitTest() {
    override val input: AnnotatedString = getOrderedList(
        listOf(
            getBoldItalicText("first\n"),
            getBoldItalicText("second")
        )
    )

    override fun addCharAtStartTestData(): Pair<String, AnnotatedString> {
        return Pair("a1. first\n2. second", buildAnnotatedString {
            append("a1. ")
            append(getBoldItalicText("first\n"))
            append(getOrderedList(listOf(getBoldItalicText("second")), 2))
        })
    }

    override fun addCharInMiddleTestData(): Pair<String, AnnotatedString> {
        return Pair(
            "1. afirst\n2. second", getOrderedList(
                listOf(
                    getBoldItalicText("afirst\n"),
                    getBoldItalicText("second")
                )
            )
        )
    }

    override fun addCharAtEndTestData(): Pair<String, AnnotatedString> {
        return Pair(
            "1. first\n2. seconda", getOrderedList(
                listOf(
                    getBoldItalicText("first\n"),
                    getBoldItalicText("seconda")
                )
            )
        )
    }

    override fun addStringAtStartTestData(): Pair<String, AnnotatedString> {
        return Pair("aa1. first\n2. second", buildAnnotatedString {
            append("aa1. ")
            append(getBoldItalicText("first\n"))
            append(getOrderedList(listOf(getBoldItalicText("second")), 2))
        })
    }

    override fun addStringInMiddleTestData(): Pair<String, AnnotatedString> {
        return Pair(
            "1. aafirst\n2. second", getOrderedList(
                listOf(
                    getBoldItalicText("aafirst\n"),
                    getBoldItalicText("second")
                )
            )
        )
    }

    override fun addStringAtEndTestData(): Pair<String, AnnotatedString> {
        return Pair(
            "1. first\n2. secondaa", getOrderedList(
                listOf(
                    getBoldItalicText("first\n"),
                    getBoldItalicText("secondaa")
                )
            )
        )
    }

    override fun removeCharAtStartTestData(): Pair<String, AnnotatedString> {
        return Pair(". first\n2. second", buildAnnotatedString {
            append(". ")
            append(getBoldItalicText("first\n"))
            append(getOrderedList(listOf(getBoldItalicText("second")), 2))
        })
    }

    override fun removeCharInMiddleTestData(): Pair<String, AnnotatedString> {
        return Pair("1. first2. second", buildAnnotatedString {
            pushStyle(Style.listStyle)
            append("1. ")
            append(getBoldItalicText("first"))
            append("2. ")
            append(getBoldItalicText("second"))
        })
    }

    override fun removeCharAtEndTestData(): Pair<String, AnnotatedString> {
        return Pair(
            "1. first\n2. secon", getOrderedList(
                listOf(
                    getBoldItalicText("first\n"),
                    getBoldItalicText("secon")
                )
            )
        )
    }

    override fun removeStringAtStartTestData(): Pair<String, AnnotatedString> {
        return Pair("1first\n2. second", buildAnnotatedString {
            append("1")
            append(getBoldItalicText("first\n"))
            append(getOrderedList(listOf(getBoldItalicText("second")), 2))
        })
    }

    override fun removeStringInMiddleTestData(): Pair<String, AnnotatedString> {
        return Pair(
            "1. firstsecond", getOrderedList(
                listOf(
                    getBoldItalicText("firstsecond")
                )
            )
        )
    }

    override fun removeStringAtEndTestData(): Pair<String, AnnotatedString> {
        return Pair(
            "1. first\n2. sec", getOrderedList(
                listOf(
                    getBoldItalicText("first\n"),
                    getBoldItalicText("sec")
                )
            )
        )
    }

    override fun replaceCharAtStartTestData(): Pair<String, AnnotatedString> {
//        TODO: Not supported yet
//        return Pair("9. first\n2. second", getOrderedList(listOf(
//            getBoldItalicText("first\n"),
//            getBoldItalicText("second")
//        ), 9))
        return Pair("1. first\n2. second", input)
    }

    override fun replaceCharInMiddleTestData(): Pair<String, AnnotatedString> {
        return Pair(
            "1. first\n9. second", getOrderedList(
                listOf(
                    getBoldItalicText("first\n"),
                    getBoldItalicText("second")
                )
            )
        )
    }

    override fun replaceCharAtEndTestData(): Pair<String, AnnotatedString> {
        return Pair(
            "1. first\n2. secon\n", getOrderedList(
                listOf(
                    getBoldItalicText("first\n"),
                    getBoldItalicText("secon\n"),
                    ""
                )
            )
        )
    }

    override fun replaceStringAtStartTestData(): Pair<String, AnnotatedString> {
//        TODO: Not supported yet
//        return Pair("22. first\n2. second", getOrderedList(listOf(
//            getBoldItalicText("first\n"),
//            getBoldItalicText("second")
//        ), 22))
        return Pair("1. first\n2. second", input)
    }

    override fun replaceStringInMiddleTestData(): Pair<String, AnnotatedString> {
        return Pair("1. first\naa. second", buildAnnotatedString {
            append(getOrderedList(listOf(getBoldItalicText("first\n"))))
            append("aa. ")
            append(getBoldItalicText("second"))
        })
    }

    override fun replaceStringAtEndTestData(): Pair<String, AnnotatedString> {
        return Pair(
            "1. first\n2. secoaa", getOrderedList(
                listOf(
                    getBoldItalicText("first\n"),
                    getBoldItalicText("secoaa")
                )
            )
        )
    }

    override fun addNewlineAtStartTestData(): Pair<String, AnnotatedString> {
        return Pair("\n1. first\n2. second", buildAnnotatedString {
            append("\n")
            append(
                getOrderedList(
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
            "1. first\n\n2. second", getOrderedList(
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
            "1. first\n2. second\n", getOrderedList(
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
