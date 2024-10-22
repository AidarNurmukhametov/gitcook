package com.aidarn.richedit

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.ParagraphStyle
import androidx.compose.ui.text.SpanStyle
import com.aidarn.richedit.controllers.InputController
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import org.junit.Test

abstract class BaseUnitTest {
    abstract val input: AnnotatedString

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

    private fun testCore(obtainData: () -> Pair<String, AnnotatedString>) {
        val (newText, expectedResult) = obtainData()
        val result = InputController.processText(input, newText)

        assertAnnotatedStringEquals(expectedResult, result)
    }

    @Test
    fun addCharAtStartTest() = testCore(::addCharAtStartTestData)

    abstract fun addCharAtStartTestData(): Pair<String, AnnotatedString>

    @Test
    fun addCharInMiddleTest() = testCore(::addCharInMiddleTestData)

    abstract fun addCharInMiddleTestData(): Pair<String, AnnotatedString>

    @Test
    fun addCharAtEndTest() = testCore(::addCharAtEndTestData)

    abstract fun addCharAtEndTestData(): Pair<String, AnnotatedString>

    @Test
    fun addStringAtStartTest() = testCore(::addStringAtStartTestData)

    abstract fun addStringAtStartTestData(): Pair<String, AnnotatedString>

    @Test
    fun addStringInMiddleTest() = testCore(::addStringInMiddleTestData)

    abstract fun addStringInMiddleTestData(): Pair<String, AnnotatedString>

    @Test
    fun addStringAtEndTest() = testCore(::addStringAtEndTestData)

    abstract fun addStringAtEndTestData(): Pair<String, AnnotatedString>

    @Test
    fun removeCharAtStartTest() = testCore(::removeCharAtStartTestData)

    abstract fun removeCharAtStartTestData(): Pair<String, AnnotatedString>

    @Test
    fun removeCharInMiddleTest() = testCore(::removeCharInMiddleTestData)

    abstract fun removeCharInMiddleTestData(): Pair<String, AnnotatedString>

    @Test
    fun removeCharAtEndTest() = testCore(::removeCharAtEndTestData)

    abstract fun removeCharAtEndTestData(): Pair<String, AnnotatedString>

    @Test
    fun removeStringAtStartTest() = testCore(::removeStringAtStartTestData)

    abstract fun removeStringAtStartTestData(): Pair<String, AnnotatedString>

    @Test
    fun removeStringInMiddleTest() = testCore(::removeStringInMiddleTestData)

    abstract fun removeStringInMiddleTestData(): Pair<String, AnnotatedString>

    @Test
    fun removeStringAtEndTest() = testCore(::removeStringAtEndTestData)

    abstract fun removeStringAtEndTestData(): Pair<String, AnnotatedString>

    @Test
    fun replaceCharAtStartTest() = testCore(::replaceCharAtStartTestData)

    abstract fun replaceCharAtStartTestData(): Pair<String, AnnotatedString>

    @Test
    fun replaceCharInMiddleTest() = testCore(::replaceCharInMiddleTestData)

    abstract fun replaceCharInMiddleTestData(): Pair<String, AnnotatedString>

    @Test
    fun replaceCharAtEndTest() = testCore(::replaceCharAtEndTestData)

    abstract fun replaceCharAtEndTestData(): Pair<String, AnnotatedString>

    @Test
    fun replaceStringAtStartTest() = testCore(::replaceStringAtStartTestData)

    abstract fun replaceStringAtStartTestData(): Pair<String, AnnotatedString>

    @Test
    fun replaceStringInMiddleTest() = testCore(::replaceStringInMiddleTestData)

    abstract fun replaceStringInMiddleTestData(): Pair<String, AnnotatedString>

    @Test
    fun replaceStringAtEndTest() = testCore(::replaceStringAtEndTestData)

    abstract fun replaceStringAtEndTestData(): Pair<String, AnnotatedString>

    @Test
    fun addNewlineAtStartTest() = testCore(::addNewlineAtStartTestData)

    abstract fun addNewlineAtStartTestData(): Pair<String, AnnotatedString>

    @Test
    fun addNewlineInMiddleTest() = testCore(::addNewlineInMiddleTestData)

    abstract fun addNewlineInMiddleTestData(): Pair<String, AnnotatedString>

    @Test
    fun addNewlineAtEndTest() = testCore(::addNewlineAtEndTestData)

    abstract fun addNewlineAtEndTestData(): Pair<String, AnnotatedString>

    @Test(timeout = 200)
    fun removeAllTest() {
        var localInput = input
        while (localInput.isNotEmpty()) {
            val newText = localInput.text.dropLast(1)
            localInput = InputController.processText(localInput, newText)
        }
        assertAnnotatedStringEquals(AnnotatedString(""), localInput)
    }
}