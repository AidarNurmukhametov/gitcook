package com.aidarn.richedit

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsNotSelected
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInputSelection
import androidx.compose.ui.text.TextRange
import org.junit.Before
import org.junit.Test

class SelectionInfoTest : BaseTest() {
    private var actualString = ""

    @Before
    fun initialize() {
        actualString = "test"
        composeTestRule.setContent {
            var instructions by remember {
                mutableStateOf(actualString)
            }
            RichEdit(
                markupString = instructions,
                onMarkupChanged = { instructions = it; actualString = it })
        }
    }

    @Test
    fun isH1SelectedTest() {
        composeTestRule.onNodeWithText(actualString).performClick()
        composeTestRule.onNodeWithText(HEADING2_NODE_TEXT).performClick()
        composeTestRule.onNodeWithText(HEADING1_NODE_TEXT).performClick()
        composeTestRule.onNodeWithText(HEADING1_NODE_TEXT).assertIsSelected()
        composeTestRule.onNodeWithText(HEADING2_NODE_TEXT).assertIsNotSelected()
    }

    @Test
    fun h1ToggleTest() {
        composeTestRule.onNodeWithText(actualString).performClick()
        composeTestRule.onNodeWithText(HEADING1_NODE_TEXT).performClick()
        composeTestRule.onNodeWithText(HEADING1_NODE_TEXT).performClick()
        composeTestRule.onNodeWithText(HEADING1_NODE_TEXT).assertIsNotSelected()
        composeTestRule.onNodeWithText(HEADING2_NODE_TEXT).assertIsNotSelected()
    }

    @Test
    fun isH2SelectedTest() {
        composeTestRule.onNodeWithText(actualString).performClick()
        composeTestRule.onNodeWithText(HEADING1_NODE_TEXT).performClick()
        composeTestRule.onNodeWithText(HEADING2_NODE_TEXT).performClick()
        composeTestRule.onNodeWithText(HEADING1_NODE_TEXT).assertIsNotSelected()
        composeTestRule.onNodeWithText(HEADING2_NODE_TEXT).assertIsSelected()
    }

    @Test
    fun h2ToggleTest() {
        composeTestRule.onNodeWithText(actualString).performClick()
        composeTestRule.onNodeWithText(HEADING2_NODE_TEXT).performClick()
        composeTestRule.onNodeWithText(HEADING2_NODE_TEXT).performClick()
        composeTestRule.onNodeWithText(HEADING1_NODE_TEXT).assertIsNotSelected()
        composeTestRule.onNodeWithText(HEADING2_NODE_TEXT).assertIsNotSelected()
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun isBoldSelectedTest() {
        composeTestRule.onNodeWithText(actualString).performClick()
        composeTestRule.onNodeWithText(actualString)
            .performTextInputSelection(TextRange(0, actualString.length))
        composeTestRule.onNodeWithText(BOLD_NODE_TEXT).performClick()
        composeTestRule.onNodeWithText(BOLD_NODE_TEXT).assertIsSelected()
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun isItalicSelectedTest() {
        composeTestRule.onNodeWithText(actualString).performClick()
        composeTestRule.onNodeWithText(actualString)
            .performTextInputSelection(TextRange(0, actualString.length))
        composeTestRule.onNodeWithText(ITALIC_NODE_TEXT).performClick()
        composeTestRule.onNodeWithText(ITALIC_NODE_TEXT).assertIsSelected()
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun isBoldAndItalicSelectedTest() {
        composeTestRule.onNodeWithText(actualString).performClick()
        composeTestRule.onNodeWithText(actualString)
            .performTextInputSelection(TextRange(0, actualString.length))
        composeTestRule.onNodeWithText(ITALIC_NODE_TEXT).performClick()
        composeTestRule.onNodeWithText(BOLD_NODE_TEXT).performClick()
        composeTestRule.onNodeWithText(ITALIC_NODE_TEXT).assertIsSelected()
        composeTestRule.onNodeWithText(BOLD_NODE_TEXT).assertIsSelected()
    }
}