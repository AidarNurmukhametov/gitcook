package com.aidarn.richedit

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.ParagraphStyle
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import com.aidarn.richedit.controllers.InputController
import com.aidarn.richedit.data.getBoldText
import com.aidarn.richedit.data.getHeading1Text
import com.aidarn.richedit.data.getHeading2Text
import com.aidarn.richedit.data.getItalicText
import com.aidarn.richedit.data.getOrderedList
import com.aidarn.richedit.data.getUnorderedList
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import org.junit.Test

class InputControllerTest {
    @Test
    fun inputTest() {
        val oldValue = AnnotatedString("test")
        val newValue = "test1"
        val processedValue = InputController.processText(oldValue, newValue)
        val expectedString = AnnotatedString("test1")

        assertAnnotatedStringEquals(expectedString, processedValue)
    }

    @Test
    fun boldInputTest() {
        val oldValue = AnnotatedString("test")
        val newValue = "**test**"
        val processedValue = InputController.processText(oldValue, newValue)
        val expectedString = getBoldText("test")

        assertAnnotatedStringEquals(expectedString, processedValue)
    }

    @Test
    fun italicInputTest() {
        val oldValue = AnnotatedString("test")
        val newValue = "_test_"
        val processedValue = InputController.processText(oldValue, newValue)
        val expectedString = getItalicText("test")

        assertAnnotatedStringEquals(expectedString, processedValue)
    }

    @Test
    fun boldItalicInputTest() {
        val oldValue = AnnotatedString("test")
        val newValue = "**_test_**"
        val processedValue = InputController.processText(oldValue, newValue)
        val expectedString = getBoldText(getItalicText("test"))

        assertAnnotatedStringEquals(expectedString, processedValue)
    }

    @Test
    fun boldTagCompletedTest() {
        val oldValue = AnnotatedString("test **text*")
        val newValue = "test **text**"
        val processedValue = InputController.processText(oldValue, newValue)
        val expectedString = buildAnnotatedString {
            append("test ")
            pushStyle(Style[StyleType.Bold])
            append("text")
        }

        assertAnnotatedStringEquals(expectedString, processedValue)
    }

    @Test
    fun italicTagCompletedTest() {
        val oldValue = AnnotatedString("test _text")
        val newValue = "test _text_"
        val processedValue = InputController.processText(oldValue, newValue)
        val expectedString = buildAnnotatedString {
            append("test ")
            pushStyle(Style[StyleType.Italic])
            append("text")
        }

        assertAnnotatedStringEquals(expectedString, processedValue)
    }

    @Test
    fun outerTagCompletedTest() {
        val oldValue = buildAnnotatedString {
            append(AnnotatedString("test _"))
            append(getBoldText("text"))
        }
        val newValue = "test _text_"
        val processedValue = InputController.processText(oldValue, newValue)
        val expectedString = buildAnnotatedString {
            append(AnnotatedString("test "))
            append(getItalicText(getBoldText("text")))
        }

        assertAnnotatedStringEquals(expectedString, processedValue)
    }

    @Test
    fun heading1Test() {
        val oldValue = AnnotatedString("#test")
        val newValue = "# test"
        val processedValue = InputController.processText(oldValue, newValue)
        val expectedString = getHeading1Text("test")

        assertAnnotatedStringEquals(expectedString, processedValue)
    }

    @Test
    fun heading2Test() {
        val oldValue = AnnotatedString("##test")
        val newValue = "## test"
        val processedValue = InputController.processText(oldValue, newValue)
        val expectedString = getHeading2Text("test")

        assertAnnotatedStringEquals(expectedString, processedValue)
    }

    @Test
    fun boldHeading1Test() {
        val oldValue = buildAnnotatedString {
            append(AnnotatedString("#"))
            append(getBoldText("test"))
        }
        val newValue = "# test"
        val processedValue = InputController.processText(oldValue, newValue)
        val expectedString = getHeading1Text(getBoldText("test"))

        assertAnnotatedStringEquals(expectedString, processedValue)
    }

    @Test
    fun italicHeading2Test() {
        val oldValue = buildAnnotatedString {
            append(AnnotatedString("##"))
            append(getItalicText("test"))
        }
        val newValue = "## test"
        val processedValue = InputController.processText(oldValue, newValue)
        val expectedString = getHeading2Text(getItalicText("test"))

        assertAnnotatedStringEquals(expectedString, processedValue)
    }

    @Test
    fun appendUnorderedListEntryTest() {
        val oldValue = getUnorderedList(listOf(AnnotatedString("test")))
        val newValue = "\u2022 test\n"
        val processedValue = InputController.processText(oldValue, newValue)
        val expectedString = buildAnnotatedString {
            pushStyle(Style.listStyle)
            append(AnnotatedString("\u2022 "))
            append(AnnotatedString("test\n"))
            append(AnnotatedString("\u2022 "))
            append(AnnotatedString(""))
        }

        assertAnnotatedStringEquals(expectedString, processedValue)
    }

    @Test
    fun insertUnorderedListEntryTest() {
        val oldValue =
            getUnorderedList(listOf(AnnotatedString("first\n"), AnnotatedString("third")))
        val newValue = "\u2022 first\n\n\u2022 third"
        val processedValue = InputController.processText(oldValue, newValue)
        val expectedString = getUnorderedList(
            listOf(
                AnnotatedString("first\n"),
                AnnotatedString("\n"),
                AnnotatedString("third")
            )
        )

        assertAnnotatedStringEquals(expectedString, processedValue)
    }

    @Test
    fun appendOrderedListEntryTest() {
        val oldValue = buildAnnotatedString {
            append(AnnotatedString("test\n"))
            append(getOrderedList(listOf(AnnotatedString("first")), 2))
        }
        val newValue = "test\n2. first\n"
        val processedValue = InputController.processText(oldValue, newValue)
        val expectedString = buildAnnotatedString {
            append(AnnotatedString("test\n"))
            append(getOrderedList(listOf(AnnotatedString("first\n"), AnnotatedString("")), 2))
        }

        assertAnnotatedStringEquals(expectedString, processedValue)
    }

    @Test
    fun insertOrderedListEntryTest() {
        val oldValue = getOrderedList(listOf(AnnotatedString("first\n"), AnnotatedString("third")))
        val newValue = "1. first\n\n2. third"
        val processedValue = InputController.processText(oldValue, newValue)
        val expectedString = getOrderedList(
            listOf(
                AnnotatedString("first\n"),
                AnnotatedString("\n"),
                AnnotatedString("third")
            )
        )

        assertAnnotatedStringEquals(expectedString, processedValue)
    }

    @Test
    fun removeOrderedListEntryTest() {
        val oldValue = getOrderedList(listOf(AnnotatedString("first\n"), AnnotatedString("")))
        val newValue = "1. first\n2."
        val processedValue = InputController.processText(oldValue, newValue)
        val expectedString = buildAnnotatedString {
            append(getOrderedList(listOf("first\n")))
            append("2.")
        }

        assertAnnotatedStringEquals(expectedString, processedValue)
    }

    @Test
    fun removeUnorderedListEntryTest() {
        val oldValue = getUnorderedList(listOf(AnnotatedString("first\n"), AnnotatedString("")))
        val newValue = "\u2022 first\n\u2022"
        val processedValue = InputController.processText(oldValue, newValue)
        val expectedString = buildAnnotatedString {
            append(getUnorderedList(listOf("first\n")))
            append("\u2022")
        }

        assertAnnotatedStringEquals(expectedString, processedValue)
    }

    @Test
    fun removeOnNewlineOrderedListEntryTest() {
        val oldValue = getOrderedList(listOf(AnnotatedString("first\n"), AnnotatedString("")))
        val newValue = "1. first\n2. \n"
        val processedValue = InputController.processText(oldValue, newValue)
        val expectedString = buildAnnotatedString {
            append(getOrderedList(listOf("first\n")))
        }

        assertAnnotatedStringEquals(expectedString, processedValue)
    }


    @Test
    fun removeOnNewlineOrderedListEntryTest1() {
        val oldValue = buildAnnotatedString {
            append(getOrderedList(listOf(AnnotatedString("first\n"), AnnotatedString("\n"))))
            append(getBoldText("text"))
        }
        val newValue = "1. first\n2. \n\ntext"
        val processedValue = InputController.processText(oldValue, newValue)
        val expectedString = buildAnnotatedString {
            append(getOrderedList(listOf("first\n")))
            append(getBoldText("text"))
        }

        assertAnnotatedStringEquals(expectedString, processedValue)
    }

    @Test
    fun removeOnNewlineUnorderedListEntryTest() {
        val oldValue = getUnorderedList(listOf(AnnotatedString("first\n"), AnnotatedString("")))
        val newValue = "\u2022 first\n\u2022 \n"
        val processedValue = InputController.processText(oldValue, newValue)
        val expectedString = buildAnnotatedString {
            append(getUnorderedList(listOf("first\n")))
        }

        assertAnnotatedStringEquals(expectedString, processedValue)
    }


    @Test
    fun removeOnNewlineUnorderedListEntryTest1() {
        val oldValue = buildAnnotatedString {
            append(getUnorderedList(listOf(AnnotatedString("first\n"), AnnotatedString("\n"))))
            append(getBoldText("text"))
        }
        val newValue = "\u2022 first\n\u2022 \n\ntext"
        val processedValue = InputController.processText(oldValue, newValue)
        val expectedString = buildAnnotatedString {
            append(getUnorderedList(listOf("first\n")))
            append(getBoldText("text"))
        }

        assertAnnotatedStringEquals(expectedString, processedValue)
    }

    @Test
    fun removeUnorderedListTest() {
        val oldValue = buildAnnotatedString {
            append(getBoldText("text"))
            append('\n')
            append(getUnorderedList(listOf(AnnotatedString("first\n"))))
        }
        val newValue = "text\u2022 first\n"
        val processedValue = InputController.processText(oldValue, newValue)
        val expectedString = buildAnnotatedString {
            append(getBoldText("text"))
            append("\u2022 first\n")
        }

        assertAnnotatedStringEquals(expectedString, processedValue)
    }


    @Test
    fun removeOrderedListTest() {
        val oldValue = buildAnnotatedString {
            append(getBoldText("text"))
            append('\n')
            append(getOrderedList(listOf(AnnotatedString("first\n"))))
        }
        val newValue = "text1. first\n"
        val processedValue = InputController.processText(oldValue, newValue)
        val expectedString = buildAnnotatedString {
            append(getBoldText("text"))
            append("1. first\n")
        }

        assertAnnotatedStringEquals(expectedString, processedValue)
    }

    @Test
    fun removeAfterOrderedListTest() {
        val oldValue = getOrderedList(listOf(AnnotatedString("first\n")))
        val newValue = "1. first"
        val processedValue = InputController.processText(oldValue, newValue)
        val expectedString = getOrderedList(listOf(AnnotatedString("first")))

        assertAnnotatedStringEquals(expectedString, processedValue)
    }

    @Test
    fun removeAfterUnorderedListTest() {
        val oldValue = getUnorderedList(listOf(AnnotatedString("first\n")))
        val newValue = "\u2022 first"
        val processedValue = InputController.processText(oldValue, newValue)
        val expectedString = getUnorderedList(listOf(AnnotatedString("first")))

        assertAnnotatedStringEquals(expectedString, processedValue)
    }

    @Test
    fun addOrderedListBoldItalicEntryTest() {
        val oldValue = getOrderedList(listOf(getBoldText(getItalicText("first"))))
        val newValue = "1. first\n"
        val processedValue = InputController.processText(oldValue, newValue)
        val expectedString = getOrderedList(listOf(getBoldText(getItalicText("first\n")), ""))

        assertAnnotatedStringEquals(expectedString, processedValue)
    }

    private fun assertAnnotatedStringEquals(expected: AnnotatedString, actual: AnnotatedString) {
        assertEquals(expected.text, actual.text)
        assertEquals(expected.spanStyles.size, actual.spanStyles.size)
        val spanDifference = mutableListOf<AnnotatedString.Range<SpanStyle>>()
        spanDifference.addAll(expected.spanStyles)
        for (style in actual.spanStyles) {
            assertTrue(spanDifference.remove(style))
        }
        assertTrue(spanDifference.isEmpty())
        assertEquals(expected.paragraphStyles.size, actual.paragraphStyles.size)
        val paragraphDifference = mutableListOf<AnnotatedString.Range<ParagraphStyle>>()
        paragraphDifference.addAll(expected.paragraphStyles)
        for (style in actual.paragraphStyles) {
            assertTrue(paragraphDifference.remove(style))
        }
        assertTrue(paragraphDifference.isEmpty())
    }
}