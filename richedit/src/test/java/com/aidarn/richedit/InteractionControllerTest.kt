package com.aidarn.richedit

import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import com.aidarn.richedit.controllers.InteractionController
import com.aidarn.richedit.data.getOrderedList
import org.junit.Assert.assertEquals
import org.junit.Test

class InteractionControllerTest {
    @Test
    fun orderedListStyleToggleOffAtSelectionTest() {
        val oldText = getOrderedList(listOf("first\n", "second\n", "third"))
        val oldValue = TextFieldValue(oldText, TextRange(0, "1. first\n".length + 1))
        val actualElement = InteractionController.removeOrderedList(oldValue)

        assertEquals("first\nsecond\n1. third", actualElement.text)
        assertEquals(1, actualElement.annotatedString.paragraphStyles.size)
        val paragraphStyle = actualElement.annotatedString.paragraphStyles[0]
        assertEquals("first\nsecond\n".length, paragraphStyle.start)
        assertEquals("first\nsecond\n1. third".length, paragraphStyle.end)
        assertEquals(TextRange(0, "first\nsecond\n".length), actualElement.selection)
    }

    @Test
    fun addOrderedListAtTheEndTest() {
        val oldText = "first\nsecond"
        val oldValue = TextFieldValue(oldText, TextRange(oldText.length))
        val actualElement = InteractionController.startOrderedList(oldValue)

        val expectedString = "first\n1. second"
        assertEquals(expectedString, actualElement.text)
        assertEquals(1, actualElement.annotatedString.paragraphStyles.size)
        val paragraphStyle = actualElement.annotatedString.paragraphStyles[0]
        assertEquals("first\n".length, paragraphStyle.start)
        assertEquals(expectedString.length, paragraphStyle.end)
        assertEquals(TextRange(expectedString.length), actualElement.selection)
    }

    @Test
    fun startOrderedListAfterNewlineTest() {
        val oldText = "first\n"
        val oldValue = TextFieldValue(oldText, TextRange(oldText.length))
        val actualElement = InteractionController.startOrderedList(oldValue)
        val expectedString = "first\n1. "
        assertEquals(expectedString, actualElement.text)
        assertEquals(1, actualElement.annotatedString.paragraphStyles.size)
        val paragraphStyle = actualElement.annotatedString.paragraphStyles[0]
        assertEquals("first\n".length, paragraphStyle.start)
        assertEquals(expectedString.length, paragraphStyle.end)
        assertEquals(TextRange(expectedString.length), actualElement.selection)
    }
}