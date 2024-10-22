package com.aidarn.richedit

import androidx.compose.ui.text.TextRange
import com.aidarn.richedit.controllers.SelectionController
import junit.framework.TestCase.assertEquals
import org.junit.Test

class SelectionControllerTest {
    @Test
    fun replaceTest() {
        val oldText = "1"
        val newText = "first\n"
        val oldSelection = TextRange(0, oldText.length)
        val actualSelection =
            SelectionController.processSelection(oldSelection, oldText, newText, true)
        val expected = TextRange(0, newText.length)
        assertEquals(expected, actualSelection)
    }

    @Test
    fun replaceTest1() {
        val oldText = "1. first\n2. second\n3. third"
        val newText = "first\nsecond\n1. third"
        val oldSelection = TextRange(0, oldText.length - "3. third".length)
        val actualSelection =
            SelectionController.processSelection(oldSelection, oldText, newText, true)
        val expected = TextRange(0, newText.length - "1. third".length)
        assertEquals(expected, actualSelection)
    }
}