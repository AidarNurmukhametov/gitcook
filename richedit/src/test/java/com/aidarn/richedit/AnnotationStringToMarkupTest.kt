package com.aidarn.richedit

import androidx.compose.ui.text.buildAnnotatedString
import com.aidarn.richedit.data.getBoldText
import com.aidarn.richedit.data.getItalicText
import com.aidarn.richedit.ext.toMarkupString
import org.junit.Assert.assertEquals
import org.junit.Test


class AnnotationStringToMarkupTest {
    @Test
    fun boldStringTest() {
        val string = buildAnnotatedString {
            pushStyle(Style[StyleType.Bold])
            append("test")
            pop()
        }
        val markup = string.toMarkupString()

        assertEquals("**test**", markup)
    }

    @Test
    fun italicStringTest() {
        val string = buildAnnotatedString {
            pushStyle(Style[StyleType.Italic])
            append("test")
            pop()
        }
        val markup = string.toMarkupString()

        assertEquals("_test_", markup)
    }

    @Test
    fun heading1StringTest() {
        val string = buildAnnotatedString {
            pushStyle(Style[StyleType.Heading1])
            append("test")
            pop()
        }
        val markup = string.toMarkupString()

        assertEquals("# test", markup)
    }

    @Test
    fun heading2StringTest() {
        val string = buildAnnotatedString {
            pushStyle(Style[StyleType.Heading2])
            append("test")
            pop()
        }
        val markup = string.toMarkupString()

        assertEquals("## test", markup)
    }

    @Test
    fun boldAndItalicTest() {
        val string = buildAnnotatedString {
            pushStyle(Style[StyleType.Italic])
            pushStyle(Style[StyleType.Bold])
            append("test")
            pop()
        }
        val markup = string.toMarkupString()

        assertEquals("_**test**_", markup)
    }

    @Test
    fun unorderedListTest() {
        val string = buildAnnotatedString {
            pushStyle(Style.listStyle)
            append("\u2022 ")
            append("first")
            appendLine()
            append("\u2022 ")
            append("second")
            appendLine()
            pop()
        }
        val markup = string.toMarkupString()
        val expectedString = """
            - first
            - second
            
        """.trimIndent()

        assertEquals(expectedString, markup)
    }

    @Test
    fun orderedListTest() {
        val string = buildAnnotatedString {
            pushStyle(Style.listStyle)
            appendLine("1. first")
            appendLine("2. second")
            pop()
        }
        val markup = string.toMarkupString()
        val expectedString = """
            1. first
            2. second
            
        """.trimIndent()

        assertEquals(expectedString, markup)
    }

    @Test
    fun boldHeadingTest() {
        val string = buildAnnotatedString {
            pushStyle(Style[StyleType.Heading1])
            pushStyle(Style[StyleType.Bold])
            append("test")
            pop()
            pop()
        }
        val markup = string.toMarkupString()

        assertEquals("# **test**", markup)
    }

    @Test
    fun italicOrderedListTest() {
        val string = buildAnnotatedString {
            pushStyle(Style.listStyle)
            append("1. ")
            pushStyle(Style[StyleType.Italic])
            append("first")
            pop()
            appendLine()
            append("2. ")
            pushStyle(Style[StyleType.Bold])
            append("second")
            pop()
            appendLine()
        }
        val markup = string.toMarkupString()
        val expectedString = """
            1. _first_
            2. **second**
            
        """.trimIndent()

        assertEquals(expectedString, markup)
    }

    @Test
    fun italicWithNewLineTest() {
        val string = getItalicText("test\n")
        val markup = string.toMarkupString()
        val expectedString = "_test_\n"

        assertEquals(expectedString, markup)
    }

    @Test
    fun boldWithNewLineTest() {
        val string = getBoldText("test\n")
        val markup = string.toMarkupString()
        val expectedString = "**test**\n"

        assertEquals(expectedString, markup)
    }

    @Test
    fun boldItalicWithNewLineTest() {
        val string = getItalicText(getBoldText("test\n"))
        val markup = string.toMarkupString()
        val expectedString = "_**test**_\n"

        assertEquals(expectedString, markup)
    }
}