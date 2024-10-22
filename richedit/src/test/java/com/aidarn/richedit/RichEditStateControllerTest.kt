package com.aidarn.richedit

import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import com.aidarn.richedit.controllers.RichEditStateController
import com.aidarn.richedit.controllers.SelectionInfo
import com.aidarn.richedit.data.RenderElement
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class RichEditStateControllerTest {
    private var controller = RichEditStateController("") { }
    private val elements
        get() = controller.elements

    @Before
    fun setUp() {
        controller = RichEditStateController("") { }
    }

    @Test
    fun startOrderedListAtSelectionTest() {
        controller.onTextValueChanged(
            0,
            TextFieldValue("first\nsecond\nthird", TextRange("first\nsecond\nthird".length))
        )
        controller.onTextValueChanged(
            0,
            TextFieldValue("first\nsecond\nthird", TextRange(0, "first\nsecond".length))
        )
        controller.onOrderedListClicked(true)

        assertEquals(1, elements.size)
        val textRenderer = elements[0] as RenderElement.Text
        assertEquals("1. first\n2. second\nthird", textRenderer.content.text)
        assertEquals(TextRange(0, "1. first\n2. second".length), textRenderer.content.selection)
        val expectedSelectionInfo = SelectionInfo(isOrderedList = true)
        assertEquals(expectedSelectionInfo, controller.selectionState.value)
    }
}
