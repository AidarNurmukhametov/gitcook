package com.aidarn.richedit

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.SemanticsNodeInteraction
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.performTextInputSelection
import androidx.compose.ui.text.TextRange
import junit.framework.TestCase.assertEquals
import org.junit.Before
import org.junit.Ignore
import org.junit.Test

@OptIn(ExperimentalTestApi::class)
class TimerInteractionTest : BaseTest() {
    private var actualString = ""

    @Before
    fun setUp() {
        actualString = ""
        composeTestRule.setContent {
            var markupString by remember {
                mutableStateOf(actualString)
            }
            RichEdit(
                markupString = markupString,
                onMarkupChanged = {
                    markupString = it
                    actualString = it
                }
            )
        }
    }

    @Test
    fun addTimerTest() {
        timerToolbarNode().performClick()
        composeTestRule.onNodeWithText("10").performTextInputSelection(TextRange(0, "10".length))
        composeTestRule.onNodeWithText("10").performTextInput("20")
        composeTestRule.onNodeWithText("Confirm").performClick()
        assertEquals("\n>!TIMER PT20M\n", actualString)
    }

    @Test
    @Ignore("Still does not work")
    fun addTimerAtTextTest() {
        composeTestRule.onNodeWithText(actualString).performTextInput("test\ntext")
        composeTestRule.onNodeWithText(actualString)
            .performTextInputSelection(TextRange("test".length))
        timerToolbarNode().performClick()
        composeTestRule.onNodeWithText("10").performTextInputSelection(TextRange(0, "10".length))
        composeTestRule.onNodeWithText("10").performTextInput("20")
        composeTestRule.onNodeWithText("Confirm").performClick()
        assertEquals("test\n>!TIMER PT20M\ntext", actualString)
    }

    @Test
    fun addTimerAtTextTest1() {
        composeTestRule.onNodeWithText(actualString).performTextInput("test\ntext")
        composeTestRule.onNodeWithText(actualString)
            .performTextInputSelection(TextRange("test\n".length))
        timerToolbarNode().performClick()
        composeTestRule.onNodeWithText("10").performTextInputSelection(TextRange(0, "10".length))
        composeTestRule.onNodeWithText("10").performTextInput("20")
        composeTestRule.onNodeWithText("Confirm").performClick()
        assertEquals("test\n>!TIMER PT20M\ntext", actualString)
    }

    @Test
    fun addTimerOnReplaceTest() {
        composeTestRule.onNodeWithText(actualString).performTextInput("test\nset 10m timer\ntext")
        composeTestRule.onNodeWithText(actualString)
            .performTextInputSelection(TextRange("test\n".length, "test\nset 10m timer\n".length))
        timerToolbarNode().performClick()
        composeTestRule.onNodeWithText("Confirm").performClick()
        assertEquals("test\n>!TIMER PT10M\ntext", actualString)
    }

    @Test
    fun removeTimerTest() {
        timerToolbarNode().performClick()
        composeTestRule.onNodeWithText("10").performTextInputSelection(TextRange(0, "10".length))
        composeTestRule.onNodeWithText("10").performTextInput("20")
        composeTestRule.onNodeWithText("Confirm").performClick()
        composeTestRule.onNodeWithContentDescription("Remove timer").performClick()
        assertEquals("", actualString)
    }

    @Test
    fun removeTimerWithTextTest() {
        composeTestRule.onNodeWithText(actualString).performTextInput("test\ntext")
        composeTestRule.onNodeWithText(actualString)
            .performTextInputSelection(TextRange("test\n".length))
        timerToolbarNode().performClick()
        composeTestRule.onNodeWithText("Confirm").performClick()
        composeTestRule.onNodeWithContentDescription("Remove timer").performClick()
        assertEquals("test\ntext", actualString)
    }

    private fun timerToolbarNode(): SemanticsNodeInteraction {
        return composeTestRule.onNodeWithContentDescription("Add timer")
    }
}