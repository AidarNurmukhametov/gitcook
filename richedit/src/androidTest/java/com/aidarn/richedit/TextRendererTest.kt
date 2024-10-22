package com.aidarn.richedit

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.performTextInputSelection
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import com.aidarn.richedit.controllers.InputController
import com.aidarn.richedit.controllers.SelectionController
import com.aidarn.richedit.edit.TextEdit
import junit.framework.TestCase.assertEquals
import org.junit.Before
import org.junit.Test


@OptIn(ExperimentalTestApi::class)
class TextRendererTest : BaseTest() {
    private var actualString = ""
    private var actualSelection = TextRange(0)

    @OptIn(ExperimentalFoundationApi::class)
    @Before
    fun setUp() {
        actualString = "test"
        actualSelection = TextRange(actualString.length)
        composeTestRule.setContent {
            var textFieldValue by remember {
                mutableStateOf(
                    TextFieldValue(
                        AnnotatedString(actualString),
                        selection = actualSelection
                    )
                )
            }
            val bringIntoViewRequester = remember { BringIntoViewRequester() }
            TextEdit(value = textFieldValue, bringIntoViewRequester, { }, onValueChanged = {
                val newString =
                    InputController.processText(textFieldValue.annotatedString, it.text)
                actualString = newString.text
                actualSelection = SelectionController.processSelection(
                    textFieldValue.selection,
                    textFieldValue.text,
                    newString.text,
                    false
                ) ?: it.selection

                val processedValue =
                    it.copy(annotatedString = newString, selection = actualSelection)
                textFieldValue = processedValue
            })
        }
    }

    @Test
    fun selectionTest() {
        val expectedSelection = TextRange(0, actualString.length)
        composeTestRule.onNodeWithText(actualString).performTextInputSelection(expectedSelection)
        assertEquals(expectedSelection, actualSelection)
    }

    @Test
    fun manualBoldInputTest() {
        composeTestRule.onNodeWithText(actualString)
            .performTextInputSelection(TextRange(actualString.length))
        for (char in "**bold**") {
            composeTestRule.onNodeWithText(actualString).performTextInput(char.toString())
        }
        composeTestRule.onNodeWithText("testbold").assertExists()
        composeTestRule.onNodeWithText("testbold").performTextInput(" ")
        composeTestRule.onNodeWithText("testbold ").assertExists()
    }

    @Test
    fun manualItalicInputTest() {
        composeTestRule.onNodeWithText(actualString)
            .performTextInputSelection(TextRange(actualString.length))
        for (char in "_italic_") {
            composeTestRule.onNodeWithText(actualString).performTextInput(char.toString())
        }
        composeTestRule.onNodeWithText("testitalic").assertExists()
        composeTestRule.onNodeWithText("testitalic").performTextInput(" ")
        composeTestRule.onNodeWithText("testitalic ").assertExists()
    }

    @Test
    fun manualHeading1InputTest() {
        composeTestRule.onNodeWithText(actualString).performTextInputSelection(TextRange(0))
        for (char in "# ") {
            composeTestRule.onNodeWithText(actualString).performTextInput(char.toString())
        }
        composeTestRule.onNodeWithText("test").assertExists()
        composeTestRule.onNodeWithText("test").performTextInputSelection(TextRange("test".length))
        composeTestRule.onNodeWithText("test").performTextInput(" ")
        composeTestRule.onNodeWithText("test ").assertExists()
    }

    @Test
    fun manualHeading2InputTest() {
        composeTestRule.onNodeWithText(actualString).performTextInputSelection(TextRange(0))
        for (char in "## ") {
            composeTestRule.onNodeWithText(actualString).performTextInput(char.toString())
        }
        composeTestRule.onNodeWithText("test").assertExists()
        composeTestRule.onNodeWithText("test").performTextInputSelection(TextRange("test".length))
        composeTestRule.onNodeWithText("test").performTextInput(" ")
        composeTestRule.onNodeWithText("test ").assertExists()
    }

    @Test
    fun manualUnorderedInputTest() {
        composeTestRule.onNodeWithText(actualString)
            .performTextInputSelection(TextRange(actualString.length))
        val list = """
            - first
            second
            third
        """.trimIndent()
        composeTestRule.onNodeWithText(actualString).performTextInput("\n")
        for (char in list) {
            composeTestRule.onNodeWithText(actualString).assertExists()
            composeTestRule.onNodeWithText(actualString).performTextInput(char.toString())
        }
        composeTestRule.onNodeWithText(actualString).assertExists()
        composeTestRule.onNodeWithText(actualString).performTextInput(" ")
        val processedList = "\u2022 first\n\u2022 second\n\u2022 third"
        val expectedString = buildString {
            append("test")
            appendLine()
            append(processedList)
            append(" ")
        }
        assertEquals(expectedString, actualString)
        composeTestRule.onNodeWithText(actualString).assertExists()
    }

    @Test
    fun manualOrderedInputTest() {
        composeTestRule.onNodeWithText(actualString)
            .performTextInputSelection(TextRange(actualString.length))
        val list = """
            1. first
            second
            third
        """.trimIndent()
        composeTestRule.onNodeWithText(actualString).performTextInput("\n")
        for (char in list) {
            composeTestRule.onNodeWithText(actualString).assertExists()
            composeTestRule.onNodeWithText(actualString).performTextInput(char.toString())
        }
        composeTestRule.onNodeWithText(actualString).assertExists()
        composeTestRule.onNodeWithText(actualString).performTextInput(" ")
        val expectedString = buildString {
            append("test")
            appendLine()
            appendLine("1. first")
            appendLine("2. second")
            append("3. third ")
        }
        assertEquals(expectedString, actualString)
        composeTestRule.onNodeWithText(actualString).assertExists()
    }

    @Test
    fun addNewLineInTextElementTest() {
        composeTestRule.onNodeWithText(actualString).performTextInput("\n")
        val expectedString = "test\n"
        assertEquals(expectedString, actualString)
    }

    @Test
    fun addNewLineInHeaderElementTest() {
        composeTestRule.onNodeWithText(actualString).performTextInputSelection(TextRange(0))
        composeTestRule.onNodeWithText(actualString).performTextInput("# ")
        composeTestRule.onNodeWithText(actualString)
            .performTextInputSelection(TextRange(actualString.length))
        composeTestRule.onNodeWithText(actualString).performTextInput("\n")
        val expectedString = "test\n"

        assertEquals(expectedString, actualString)
    }
}
