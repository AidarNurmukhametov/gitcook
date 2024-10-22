package com.aidarn.richedit

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.ParagraphStyle
import androidx.compose.ui.text.buildAnnotatedString
import com.aidarn.richedit.data.compare
import com.aidarn.richedit.ext.applyTextAndChanges
import com.aidarn.richedit.ext.toMarkupString
import org.junit.Assert.assertEquals
import org.junit.Test

class AnnotatedStringWithTextTest {
    private fun changeStringTestCore(
        string: String,
        styleTypes: List<StyleType>,
        replaceWith: String,
        expectedResult: String
    ) {
        val annotatedString = buildAnnotatedString {
            for (styleType in styleTypes) {
                pushStyle(Style[styleType])
            }
            append(string)
        }
        val newString = annotatedString.withText(replaceWith)
        val markup = newString.toMarkupString()

        assertEquals(expectedResult, markup)
    }

    @Test
    fun addBoldAtBeginningTest() =
        changeStringTestCore("test", listOf(StyleType.Bold), "1test", "**1test**")

    @Test
    fun removeBoldAtBeginningTest() =
        changeStringTestCore("test", listOf(StyleType.Bold), "est", "**est**")

    @Test
    fun replaceBoldAtBeginningTest() =
        changeStringTestCore("test", listOf(StyleType.Bold), "1est", "**1est**")

    @Test
    fun addItalicAtMiddleTest() =
        changeStringTestCore("test2", listOf(StyleType.Italic), "te1st2", "_te1st2_")

    @Test
    fun removeItalicAtMiddleTest() =
        changeStringTestCore("test2", listOf(StyleType.Italic), "tet2", "_tet2_")

    @Test
    fun replaceItalicAtMiddleTest() =
        changeStringTestCore("test2", listOf(StyleType.Italic), "te3t2", "_te3t2_")

    @Test
    fun addHeading1AtEndTest() =
        changeStringTestCore("heading", listOf(StyleType.Heading1), "heading1", "# heading1")

    @Test
    fun removeHeading1AtEndTest() =
        changeStringTestCore("heading1", listOf(StyleType.Heading1), "heading", "# heading")

    @Test
    fun replaceHeading1AtEndTest() =
        changeStringTestCore("heading1", listOf(StyleType.Heading1), "heading2", "# heading2")

    @Test
    fun addHeading2AtBeginningTest() = changeStringTestCore(
        "subheading",
        listOf(StyleType.Heading2),
        "1subheading",
        "## 1subheading"
    )

    @Test
    fun removeHeading2AtBeginningTest() =
        changeStringTestCore("subheading", listOf(StyleType.Heading2), "ubheading", "## ubheading")

    @Test
    fun replaceHeading2AtBeginningTest() = changeStringTestCore(
        "subheading",
        listOf(StyleType.Heading2),
        "1ubheading",
        "## 1ubheading"
    )

    @Test
    fun addBoldItalicAtBeginningTest() = changeStringTestCore(
        "test",
        listOf(StyleType.Italic, StyleType.Bold),
        "1test",
        "_**1test**_"
    )

    @Test
    fun removeBoldItalicAtBeginningTest() =
        changeStringTestCore("test", listOf(StyleType.Italic, StyleType.Bold), "est", "_**est**_")

    @Test
    fun replaceBoldItalicAtBeginningTest() =
        changeStringTestCore("test", listOf(StyleType.Italic, StyleType.Bold), "1est", "_**1est**_")

    @Test
    fun addBoldItalicAtMiddleTest() = changeStringTestCore(
        "test",
        listOf(StyleType.Italic, StyleType.Bold),
        "te1st",
        "_**te1st**_"
    )

    @Test
    fun removeBoldItalicAtMiddleTest() =
        changeStringTestCore("test", listOf(StyleType.Italic, StyleType.Bold), "tst", "_**tst**_")

    @Test
    fun replaceBoldItalicAtMiddleTest() =
        changeStringTestCore("test", listOf(StyleType.Italic, StyleType.Bold), "te1t", "_**te1t**_")

    @Test
    fun addHeading1BoldAtEndTest() = changeStringTestCore(
        "heading",
        listOf(StyleType.Heading1, StyleType.Bold),
        "heading1",
        "# **heading1**"
    )

    @Test
    fun removeHeading1BoldAtEndTest() = changeStringTestCore(
        "heading1",
        listOf(StyleType.Heading1, StyleType.Bold),
        "heading",
        "# **heading**"
    )

    @Test
    fun replaceHeading1BoldAtEndTest() = changeStringTestCore(
        "heading1",
        listOf(StyleType.Heading1, StyleType.Bold),
        "heading2",
        "# **heading2**"
    )

    @Test
    fun addHeading2ItalicAtBeginningTest() = changeStringTestCore(
        "subheading",
        listOf(StyleType.Heading2, StyleType.Italic),
        "1subheading",
        "## _1subheading_"
    )

    @Test
    fun removeHeading2ItalicAtBeginningTest() = changeStringTestCore(
        "subheading",
        listOf(StyleType.Heading2, StyleType.Italic),
        "ubheading",
        "## _ubheading_"
    )

    @Test
    fun replaceHeading2ItalicAtBeginningTest() = changeStringTestCore(
        "subheading",
        listOf(StyleType.Heading2, StyleType.Italic),
        "1ubheading",
        "## _1ubheading_"
    )

    @Test
    fun addItalicBoldHeading1AtMiddleTest() = changeStringTestCore(
        "example",
        listOf(StyleType.Heading1, StyleType.Italic, StyleType.Bold),
        "ex1ample",
        "# _**ex1ample**_"
    )

    @Test
    fun removeItalicBoldHeading1AtMiddleTest() = changeStringTestCore(
        "example",
        listOf(StyleType.Heading1, StyleType.Italic, StyleType.Bold),
        "exmple",
        "# _**exmple**_"
    )

    @Test
    fun replaceItalicBoldHeading1AtMiddleTest() = changeStringTestCore(
        "example",
        listOf(StyleType.Heading1, StyleType.Italic, StyleType.Bold),
        "ex1mple",
        "# _**ex1mple**_"
    )
}

private fun AnnotatedString.withText(replaceWith: String): AnnotatedString {
    val changes = compare(text, replaceWith) ?: return this
    return applyTextAndChanges(replaceWith, changes)
}

class AnnotatedMultilineStringWithTextTest {
    private fun changeTextTestCore(
        string: String,
        styles: List<ParagraphStyle>,
        replaceWith: String,
        expectedResult: String
    ) {
        val annotatedString = buildAnnotatedString {
            for (style in styles) {
                pushStyle(style)
            }
            append(string)
        }
        val newString = annotatedString.withText(replaceWith)
        val markup = newString.toMarkupString()

        assertEquals(expectedResult, markup)
    }

    @Test
    fun addToListAtBeggingTest() =
        changeTextTestCore("1. test", listOf(Style.listStyle), "1. 1test", "1. 1test")

    @Test
    fun addToListAtMiddleTest() =
        changeTextTestCore("1. test", listOf(Style.listStyle), "1. te1st", "1. te1st")

    @Test
    fun addToListAtEndTest() =
        changeTextTestCore("1. test", listOf(Style.listStyle), "1. test1", "1. test1")

    @Test
    fun addToListAtBeggingMultilineTest() = changeTextTestCore(
        "1. test\n2. test2",
        listOf(Style.listStyle),
        "1. 1test\n2. test2",
        "1. 1test\n2. test2"
    )

    @Test
    fun addToListAtMiddleMultilineTest() = changeTextTestCore(
        "1. test\n2. test2",
        listOf(Style.listStyle),
        "1. te1st\n2. test2",
        "1. te1st\n2. test2"
    )

    @Test
    fun addToListAtEndMultilineTest() = changeTextTestCore(
        "1. test\n2. test2",
        listOf(Style.listStyle),
        "1. test1\n2. test2",
        "1. test1\n2. test2"
    )

    @Test
    fun addToListAtBeggingSecondLineTest() = changeTextTestCore(
        "1. test\n2. test2",
        listOf(Style.listStyle),
        "1. test\n2. 1test2",
        "1. test\n2. 1test2"
    )

    @Test
    fun addToListAtMiddleSecondLineTest() = changeTextTestCore(
        "1. test\n2. test2",
        listOf(Style.listStyle),
        "1. test\n2. te1st2",
        "1. test\n2. te1st2"
    )

    @Test
    fun addToListAtEndSecondLineTest() = changeTextTestCore(
        "1. test\n2. test2",
        listOf(Style.listStyle),
        "1. test\n2. test21",
        "1. test\n2. test21"
    )

    @Test
    fun boldListEntryTest() {
        val annotatedString = buildAnnotatedString {
            pushStyle(Style.listStyle)
            append("1. ")
            pushStyle(Style[StyleType.Bold])
            append("test")
        }
        val newString = annotatedString.withText("1. 1test")
        val markup = newString.toMarkupString()

        assertEquals("1. **1test**", markup)
    }

    @Test
    fun complexListEntryTest() {
        val annotatedString = buildAnnotatedString {
            pushStyle(Style.listStyle)
            append("1. ")
            pushStyle(Style[StyleType.Bold])
            append("test")
            pushStyle(Style[StyleType.Italic])
            append("1")
            pop()
            append(".")
        }
        val newString = annotatedString.withText("1. test21.")
        val markup = newString.toMarkupString()

        assertEquals("1. **test_21_.**", markup)
    }
}