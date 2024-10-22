package com.aidarn.richedit

import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import com.aidarn.richedit.data.compare
import com.aidarn.richedit.ext.applyTextAndChanges
import junit.framework.TestCase.assertEquals
import org.junit.Test

private const val TEST_VALUE = "Test"

class ApplyTextAndChangesTest {
    private val initialValue = TextFieldValue(TEST_VALUE, TextRange(TEST_VALUE.length))

    @Test
    fun putCharEnd() {
        val newString = "Test1"
        val stringDiff = compare(TEST_VALUE, newString)
        val actualValue = initialValue.applyTextAndChanges(newString, stringDiff!!)
        assertEquals(newString, actualValue.text)
        assertEquals(TextRange(newString.length), actualValue.selection)
    }

    @Test
    fun putCharStart() {
        val newString = "1Test"
        val stringDiff = compare(TEST_VALUE, newString)
        val actualValue = initialValue.applyTextAndChanges(newString, stringDiff!!)
        assertEquals(newString, actualValue.text)
        assertEquals(TextRange(1), actualValue.selection)
    }

    @Test
    fun putCharMiddle() {
        val newString = "Te1st"
        val stringDiff = compare(TEST_VALUE, newString)
        val actualValue = initialValue.applyTextAndChanges(newString, stringDiff!!)
        assertEquals(newString, actualValue.text)
        assertEquals(TextRange(3), actualValue.selection)
    }

    @Test
    fun removeCharEnd() {
        val newString = "Tes"
        val stringDiff = compare(TEST_VALUE, newString)
        val actualValue = initialValue.applyTextAndChanges(newString, stringDiff!!)
        assertEquals(newString, actualValue.text)
        assertEquals(TextRange(3), actualValue.selection)
    }

    @Test
    fun removeCharStart() {
        val newValue = initialValue.copy(selection = TextRange(1))
        val newString = "est"
        val stringDiff = compare(TEST_VALUE, newString)
        val actualValue = newValue.applyTextAndChanges(newString, stringDiff!!)
        assertEquals(newString, actualValue.text)
        assertEquals(TextRange(0), actualValue.selection)
    }

    @Test
    fun removeCharMiddle() {
        val newValue = initialValue.copy(selection = TextRange(2))
        val newString = "Tst"
        val stringDiff = compare(TEST_VALUE, newString)
        val actualValue = newValue.applyTextAndChanges(newString, stringDiff!!)
        assertEquals(newString, actualValue.text)
        assertEquals(TextRange(1), actualValue.selection)
    }

    @Test
    fun removeCharsEnd() {
        val newValue = initialValue.copy(selection = TextRange(2, TEST_VALUE.length))
        val newString = "Te"
        val stringDiff = compare(TEST_VALUE, newString)
        val actualValue = newValue.applyTextAndChanges(newString, stringDiff!!)
        assertEquals(newString, actualValue.text)
        assertEquals(TextRange(2), actualValue.selection)
    }

    @Test
    fun removeCharsStart() {
        val newValue = initialValue.copy(selection = TextRange(0, 2))
        val newString = "st"
        val stringDiff = compare(TEST_VALUE, newString)
        val actualValue = newValue.applyTextAndChanges(newString, stringDiff!!)
        assertEquals(newString, actualValue.text)
        assertEquals(TextRange(0), actualValue.selection)
    }

    @Test
    fun removeCharsMiddle() {
        val newValue = initialValue.copy(selection = TextRange(1, 3))
        val newString = "Tt"
        val stringDiff = compare(TEST_VALUE, newString)
        val actualValue = newValue.applyTextAndChanges(newString, stringDiff!!)
        assertEquals(newString, actualValue.text)
        assertEquals(TextRange(1), actualValue.selection)
    }

    @Test
    fun removeAllChars() {
        val newValue = initialValue.copy(selection = TextRange(0, TEST_VALUE.length))
        val newString = ""
        val stringDiff = compare(TEST_VALUE, newString)
        val actualValue = newValue.applyTextAndChanges(newString, stringDiff!!)
        assertEquals(newString, actualValue.text)
        assertEquals(TextRange(0), actualValue.selection)
    }

    @Test
    fun replaceCharEnd() {
        val newValue = initialValue.copy(selection = TextRange(3, TEST_VALUE.length))
        val newString = "Tes1"
        val stringDiff = compare(TEST_VALUE, newString)
        val actualValue = newValue.applyTextAndChanges(newString, stringDiff!!)
        assertEquals(newString, actualValue.text)
        assertEquals(TextRange(TEST_VALUE.length), actualValue.selection)
    }

    @Test
    fun replaceCharStart() {
        val newValue = initialValue.copy(selection = TextRange(0, 1))
        val newString = "1est"
        val stringDiff = compare(TEST_VALUE, newString)
        val actualValue = newValue.applyTextAndChanges(newString, stringDiff!!)
        assertEquals(newString, actualValue.text)
        assertEquals(TextRange(1), actualValue.selection)
    }

    @Test
    fun replaceCharMiddle() {
        val newValue = initialValue.copy(selection = TextRange(1, 2))
        val newString = "T1st"
        val stringDiff = compare(TEST_VALUE, newString)
        val actualValue = newValue.applyTextAndChanges(newString, stringDiff!!)
        assertEquals(newString, actualValue.text)
        assertEquals(TextRange(2), actualValue.selection)
    }

    @Test
    fun replaceCharsEnd() {
        val newValue = initialValue.copy(selection = TextRange(2, TEST_VALUE.length))
        val newString = "Te11"
        val stringDiff = compare(TEST_VALUE, newString)
        val actualValue = newValue.applyTextAndChanges(newString, stringDiff!!)
        assertEquals(newString, actualValue.text)
        assertEquals(TextRange(TEST_VALUE.length), actualValue.selection)
    }

    @Test
    fun replaceCharsStart() {
        val newValue = initialValue.copy(selection = TextRange(0, 2))
        val newString = "11st"
        val stringDiff = compare(TEST_VALUE, newString)
        val actualValue = newValue.applyTextAndChanges(newString, stringDiff!!)
        assertEquals(newString, actualValue.text)
        assertEquals(TextRange(2), actualValue.selection)
    }

    @Test
    fun replaceCharsMiddle() {
        val newValue = initialValue.copy(selection = TextRange(1, 3))
        val newString = "T11t"
        val stringDiff = compare(TEST_VALUE, newString)
        val actualValue = newValue.applyTextAndChanges(newString, stringDiff!!)
        assertEquals(newString, actualValue.text)
        assertEquals(TextRange(3), actualValue.selection)
    }

    @Test
    fun replaceAllChars() {
        val newValue = initialValue.copy(selection = TextRange(0, TEST_VALUE.length))
        val newString = "All"
        val stringDiff = compare(TEST_VALUE, newString)
        val actualValue = newValue.applyTextAndChanges(newString, stringDiff!!)
        assertEquals(newString, actualValue.text)
        assertEquals(TextRange(newString.length), actualValue.selection)
    }
}