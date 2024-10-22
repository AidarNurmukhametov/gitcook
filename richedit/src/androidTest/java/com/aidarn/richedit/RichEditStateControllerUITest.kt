package com.aidarn.richedit

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.SemanticsNodeInteraction
import androidx.compose.ui.test.assertIsNotSelected
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.performTextInputSelection
import androidx.compose.ui.text.TextRange
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Ignore
import org.junit.Test

@OptIn(ExperimentalTestApi::class)
class RichEditStateControllerUITest : BaseTest() {
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
    fun h1ToggleTest() = toggleLineStyleTestCore(HEADING1_NODE_TEXT, THE_CAKE) { "# $it" }

    @Test
    fun h2ToggleTest() = toggleLineStyleTestCore(HEADING2_NODE_TEXT, THE_SOUP) { "## $it" }

    @Test
    fun replaceH1WithH2Test() {
        composeTestRule.onNodeWithText(actualString).performTextInput(THE_SOUP)
        composeTestRule.onNodeWithText(HEADING1_NODE_TEXT).performClick()
        composeTestRule.onNodeWithText(HEADING2_NODE_TEXT).performClick()
        assertEquals("## $THE_SOUP", actualString)
    }

    @Test
    fun boldTest() = toggleSpanStyleTestCore(BOLD_NODE_TEXT, THE_SOUP) { "**$it**" }

    @Test
    fun italicTest() = toggleSpanStyleTestCore(ITALIC_NODE_TEXT, THE_CAKE) { "_${it}_" }

    @Test
    fun boldTestStart() = toggleSpanStyleWithSelectionTestCore(
        BOLD_NODE_TEXT,
        "$THE_CAKE$THE_SOUP",
        TextRange(0, THE_CAKE.length)
    ) { string, selection ->
        buildString {
            append(string.slice(0 until selection.start))
            append("**")
            append(string.slice(selection.start until selection.end))
            append("**")
            append(string.substring(selection.end))
        }
    }

    @Test
    fun boldTestEnd() = toggleSpanStyleWithSelectionTestCore(
        BOLD_NODE_TEXT,
        "$THE_CAKE$THE_SOUP",
        TextRange(THE_CAKE.length, "$THE_CAKE$THE_SOUP".length)
    ) { string, selection ->
        buildString {
            append(string.slice(0 until selection.start))
            append("**")
            append(string.slice(selection.start until selection.end))
            append("**")
            append(string.substring(selection.end))
        }
    }

    @Test
    fun boldTestMiddle() = toggleSpanStyleWithSelectionTestCore(
        BOLD_NODE_TEXT,
        "$THE_CAKE$THE_SOUP",
        TextRange(1, "$THE_CAKE$THE_SOUP".length - 1)
    ) { string, selection ->
        buildString {
            append(string.slice(0 until selection.start))
            append("**")
            append(string.slice(selection.start until selection.end))
            append("**")
            append(string.substring(selection.end))
        }
    }

    @Test
    fun italicTestStart() = toggleSpanStyleWithSelectionTestCore(
        ITALIC_NODE_TEXT,
        "$THE_SOUP$THE_CAKE",
        TextRange(0, THE_SOUP.length)
    ) { string, selection ->
        buildString {
            append(string.slice(0 until selection.start))
            append("_")
            append(string.slice(selection.start until selection.end))
            append("_")
            append(string.substring(selection.end))
        }
    }

    @Test
    fun italicTestEnd() = toggleSpanStyleWithSelectionTestCore(
        ITALIC_NODE_TEXT,
        "$THE_SOUP$THE_CAKE",
        TextRange(THE_CAKE.length, "$THE_SOUP$THE_CAKE".length)
    ) { string, selection ->
        buildString {
            append(string.slice(0 until selection.start))
            append("_")
            append(string.slice(selection.start until selection.end))
            append("_")
            append(string.substring(selection.end))
        }
    }

    @Test
    fun italicTestMiddle() = toggleSpanStyleWithSelectionTestCore(
        ITALIC_NODE_TEXT,
        "$THE_SOUP$THE_CAKE",
        TextRange(1, "$THE_SOUP$THE_CAKE".length - 1)
    ) { string, selection ->
        buildString {
            append(string.slice(0 until selection.start))
            append("_")
            append(string.slice(selection.start until selection.end))
            append("_")
            append(string.substring(selection.end))
        }
    }

    @Test
    fun boldItalicTest() {
        val unmodifiedText = "$THE_CAKE$THE_SOUP"

        composeTestRule.onNodeWithText(actualString).performTextInput("$THE_CAKE**$THE_SOUP**")
        composeTestRule.onNodeWithText(unmodifiedText)
            .performTextInputSelection(TextRange(THE_CAKE.length, unmodifiedText.length))
        composeTestRule.onNodeWithText(ITALIC_NODE_TEXT).performClick()
        assertEquals("${THE_CAKE}**_${THE_SOUP}_**", actualString)
        composeTestRule.onNodeWithText(ITALIC_NODE_TEXT).performClick()
        assertEquals("$THE_CAKE**$THE_SOUP**", actualString)
    }

    @Test
    fun italicBoldTest() {
        val unmodifiedText = "$THE_CAKE$THE_SOUP"
        composeTestRule.onNodeWithText(actualString).performTextInput("${THE_CAKE}_${THE_SOUP}_")
        composeTestRule.onNodeWithText(unmodifiedText)
            .performTextInputSelection(TextRange(THE_CAKE.length, unmodifiedText.length))
        composeTestRule.onNodeWithText(BOLD_NODE_TEXT).performClick()
        assertEquals("${THE_CAKE}_**${THE_SOUP}**_", actualString)
        composeTestRule.onNodeWithText(BOLD_NODE_TEXT).performClick()
        assertEquals("${THE_CAKE}_${THE_SOUP}_", actualString)
    }

    @Test
    fun manualBoldTest() {
        composeTestRule.onNodeWithText(actualString).performClick()
        composeTestRule.onNodeWithText(actualString).performTextInput("**bold**")
        composeTestRule.onNodeWithText("bold").performTextInput(" ")
        assertEquals("**bold **", actualString)
    }

    @Test
    @Ignore("Not supported yet")
    fun misalignedTagsTest() {
        val unmodifiedText = "$THE_CAKE$THE_SOUP"
        composeTestRule.onNodeWithText(actualString).performTextInput("${THE_CAKE}_${THE_SOUP}_")
        composeTestRule.onNodeWithText(unmodifiedText)
            .performTextInputSelection(TextRange(0, unmodifiedText.length - 1))
        composeTestRule.onNodeWithText(BOLD_NODE_TEXT).performClick()
        assertEquals("**The Cake_The Sou**p_", actualString)
        composeTestRule.onNodeWithText(BOLD_NODE_TEXT).performClick()
        assertEquals("${THE_CAKE}_${THE_SOUP}_", actualString)
    }

    @Test
    fun unorderedListStyleToggleOffTest() {
        val unorderedList = """
            - first
            - second
            - third
        """.trimIndent()
        val processedList = unorderedList.replace('-', '\u2022')
        composeTestRule.onNodeWithText(actualString).performTextInput(unorderedList)
        composeTestRule.onNodeWithText(processedList)
            .performTextInputSelection(TextRange(0, processedList.length))
        unorderedListToolbarItem().performClick()
        assertEquals("first\nsecond\nthird", actualString)
        unorderedListToolbarItem().assertIsNotSelected()
    }

    @Test
    fun unorderedListStyleToggleOffAtSelectionTest() {
        val unorderedList = """
            - first
            - second
            - third
        """.trimIndent()
        val processedList = unorderedList.replace('-', '\u2022')
        composeTestRule.onNodeWithText(actualString).performTextInput(unorderedList)
        composeTestRule.onNodeWithText(processedList)
            .performTextInputSelection(TextRange("- first".length + 1, processedList.length))
        unorderedListToolbarItem().performClick()
        assertEquals("- first\nsecond\nthird", actualString)
        unorderedListToolbarItem().assertIsNotSelected()
    }

    @Test
    fun startUnorderedListTest() {
        composeTestRule.onNodeWithText(actualString).performTextInput("test")
        unorderedListToolbarItem().performClick()
        val expected = "- test"
        assertEquals(expected, actualString)
        unorderedListToolbarItem().assertIsSelected()
    }

    @Test
    fun startUnorderedListAtSelectionTest() {
        composeTestRule.onNodeWithText(actualString).performTextInput("test\nsecond\nthird")
        composeTestRule.onNodeWithText(actualString)
            .performTextInputSelection(TextRange(0, "test\nsecond".length))
        unorderedListToolbarItem().performClick()
        val expected = "- test\n- second\nthird"
        assertEquals(expected, actualString)
        unorderedListToolbarItem().assertIsSelected()
    }

    @Test
    fun addUnorderedListEntryTest() {
        val unorderedList = """
            - first
            - second
            - third
        """.trimIndent()
        val processedList = unorderedList.replace('-', '\u2022')
        composeTestRule.onNodeWithText(actualString).performTextInput(unorderedList)
        composeTestRule.onNodeWithText(processedList).performTextInput("\n")
        val expectedString = buildString { appendLine(unorderedList); append("- ") }
        assertEquals(expectedString, actualString)
        unorderedListToolbarItem().assertIsSelected()
    }

    @Test
    fun addOnReplaceUnorderedListEntryTest() {
        val unorderedList = """
            - first
            - second
            - third1
        """.trimIndent()
        val processedList = unorderedList.replace('-', '\u2022')
        composeTestRule.onNodeWithText(actualString).performTextInput(unorderedList)
        composeTestRule.onNodeWithText(processedList)
            .performTextInputSelection(TextRange(processedList.length - 1, processedList.length))
        composeTestRule.onNodeWithText(processedList).performTextInput("\n")
        val expectedString = buildString { appendLine(unorderedList.dropLast(1)); append("- ") }
        assertEquals(expectedString, actualString)
        unorderedListToolbarItem().assertIsSelected()
    }

    @Test
    fun orderedListStyleToggleOffTest() {
        val unorderedList = """
            1. first
            2. second
            3. third
        """.trimIndent()
        composeTestRule.onNodeWithText(actualString).performTextInput(unorderedList)
        composeTestRule.onNodeWithText(unorderedList)
            .performTextInputSelection(TextRange(0, unorderedList.length))
        orderedListToolbarItem().performClick()
        assertEquals("first\nsecond\nthird", actualString)
        orderedListToolbarItem().assertIsNotSelected()
    }

    @Test
    fun orderedListStyleToggleOffAtSelectionTest() {
        val orderedList = """
            1. first
            2. second
            3. third
        """.trimIndent()
        composeTestRule.onNodeWithText(actualString).performTextInput(orderedList)
        composeTestRule.onNodeWithText(orderedList)
            .performTextInputSelection(TextRange(0, orderedList.length - "3. third".length - 1))
        orderedListToolbarItem().performClick()
        assertEquals("first\nsecond\n1. third", actualString)
        orderedListToolbarItem().assertIsNotSelected()
    }

    @Test
    fun startOrderedListTest() {
        composeTestRule.onNodeWithText(actualString).performTextInput("test")
        orderedListToolbarItem().performClick()
        val expected = "1. test"
        assertEquals(expected, actualString)
        orderedListToolbarItem().assertIsSelected()
    }

    @Test
    fun startOrderedListAtSelectionTest() {
        composeTestRule.onNodeWithText(actualString).performTextInput("test\nsecond\nthird")
        composeTestRule.onNodeWithText(actualString)
            .performTextInputSelection(TextRange(0, "test\nsecond".length))
        orderedListToolbarItem().performClick()
        val expected = "1. test\n2. second\nthird"
        assertEquals(expected, actualString)
        orderedListToolbarItem().assertIsSelected()
    }

    @Test
    fun addOrderedListEntryTest() {
        val unorderedList = """
            1. first
            2. second
            3. third
        """.trimIndent()
        composeTestRule.onNodeWithText(actualString).performTextInput(unorderedList)
        composeTestRule.onNodeWithText(actualString).performTextInput("\n")
        val expectedString = buildString { appendLine(unorderedList); append("4. ") }
        assertEquals(expectedString, actualString)
        orderedListToolbarItem().assertIsSelected()
    }

    @Test
    fun addOnReplaceOrderedListEntryTest() {
        val unorderedList = """
            1. first
            2. second
            3. third1
        """.trimIndent()
        composeTestRule.onNodeWithText(actualString).performTextInput(unorderedList)
        composeTestRule.onNodeWithText(actualString)
            .performTextInputSelection(TextRange(actualString.length - 1, actualString.length))
        composeTestRule.onNodeWithText(actualString).performTextInput("\n")
        val expectedString = buildString { appendLine(unorderedList.dropLast(1)); append("4. ") }
        assertEquals(expectedString, actualString)
        orderedListToolbarItem().assertIsSelected()
    }

    @Test
    fun addNewLineInTextElementTest() {
        val text = """
            first
            1. list
        """.trimIndent()
        composeTestRule.onNodeWithText(actualString).performTextInput(text)
        composeTestRule.onNodeWithText(actualString)
            .performTextInputSelection(TextRange("first".length))
        composeTestRule.onNodeWithText(actualString).performTextInput("\n")
        val expectedString = """
            first
            
            1. list
        """.trimIndent()
        assertEquals(expectedString, actualString)
    }

    @Test
    fun addNewLineInBoldItalicListTest() {
        val text = """
            first
            1. list
        """.trimIndent()
        composeTestRule.onNodeWithText(actualString).performTextInput(text)
        composeTestRule.onNodeWithText(BOLD_NODE_TEXT).performClick()
        composeTestRule.onNodeWithText(ITALIC_NODE_TEXT).performClick()
        composeTestRule.onNodeWithText(text).performTextInput("\n")
        val expectedString = """
            first
            1. list
            2. 
        """.trimIndent()
        assertEquals(expectedString, actualString)
    }

    @Test
    fun addH1AtEmptyStringTest() {
        composeTestRule.onNodeWithText(actualString).performTextInput("test")
        composeTestRule.onNodeWithText(HEADING1_NODE_TEXT).performClick().assertIsSelected()
    }

    @Test
    fun addNewLineAtTheEndH1Test() {
        composeTestRule.onNodeWithText(actualString).performTextInput("DA SOUP!!!")
        composeTestRule.onNodeWithText(HEADING1_NODE_TEXT).performClick().assertIsSelected()
        composeTestRule.onNodeWithText("DA SOUP!!!").performTextInput("\n")
        val expectedString = "# DA SOUP!!!\n"
        assertEquals(expectedString, actualString)
        composeTestRule.onNodeWithText(HEADING1_NODE_TEXT).assertIsNotSelected()
    }

    private fun toggleLineStyleTestCore(
        toggleText: String,
        unmodifiedText: String,
        getModifiedText: (String) -> String
    ) {
        composeTestRule.onNodeWithText(actualString).performTextInput(unmodifiedText)
        composeTestRule.onNodeWithText(toggleText).performClick()
        assertEquals(getModifiedText(unmodifiedText), actualString)
        composeTestRule.onNodeWithText(toggleText).performClick()
        assertEquals(unmodifiedText, actualString)
    }

    private fun toggleSpanStyleTestCore(
        toggleText: String,
        unmodifiedText: String,
        getModifiedText: (String) -> String
    ) {
        composeTestRule.onNodeWithText(actualString).performTextInput(unmodifiedText)
        composeTestRule.onNodeWithText(actualString)
            .performTextInputSelection(TextRange(0, unmodifiedText.length))
        composeTestRule.onNodeWithText(toggleText).performClick()
        assertEquals(getModifiedText(unmodifiedText), actualString)
        composeTestRule.onNodeWithText(toggleText).performClick()
        assertEquals(unmodifiedText, actualString)
    }

    private fun toggleSpanStyleWithSelectionTestCore(
        toggleText: String,
        unmodifiedText: String,
        selection: TextRange,
        getModifiedText: (String, TextRange) -> String
    ) {
        composeTestRule.onNodeWithText(actualString).performTextInput(unmodifiedText)
        composeTestRule.onNodeWithText(actualString).performTextInputSelection(selection)
        composeTestRule.onNodeWithText(toggleText).performClick()
        assertEquals(getModifiedText(unmodifiedText, selection), actualString)
        composeTestRule.onNodeWithText(toggleText).performClick()
        assertEquals(unmodifiedText, actualString)
    }

    private fun unorderedListToolbarItem(): SemanticsNodeInteraction {
        return composeTestRule.onNodeWithContentDescription("Toggle unordered list")
    }

    private fun orderedListToolbarItem(): SemanticsNodeInteraction {
        return composeTestRule.onNodeWithContentDescription("Toggle ordered list")
    }
}