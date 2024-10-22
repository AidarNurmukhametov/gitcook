package com.aidarn.richedit

import com.aidarn.richedit.data.StringDiff
import com.aidarn.richedit.data.compare
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNull
import org.junit.Test

class StringCompareTest {
    @Test
    fun simpleAddTest() {
        val first = "foo bar ba"
        val second = "foo bar bar"
        val res = compare(first, second)
        val expectedDiff = StringDiff.Add(10, "r")

        assertEquals(expectedDiff, res)
    }

    @Test
    fun simpleRemoveTest() {
        val first = "hello world"
        val second = "hello"
        val res = compare(first, second)
        val expectedDiff = StringDiff.Remove(5, 11)

        assertEquals(expectedDiff, res)
    }

    @Test
    fun simpleReplaceTest() {
        val first = "hello"
        val second = "hella"
        val res = compare(first, second)
        val expectedDiff = StringDiff.Replace(4, 5, "a")

        assertEquals(expectedDiff, res)
    }

    @Test
    fun addAtBeginningTest() {
        val first = "bar"
        val second = "foobar"
        val res = compare(first, second)
        val expectedDiff = StringDiff.Add(0, "foo")

        assertEquals(expectedDiff, res)
    }

    @Test
    fun removeAtBeginningTest() {
        val first = "foobar"
        val second = "bar"
        val res = compare(first, second)
        val expectedDiff = StringDiff.Remove(0, 3)

        assertEquals(expectedDiff, res)
    }

    @Test
    fun complexReplaceTest() {
        val first = "abcde"
        val second = "abXYZe"
        val res = compare(first, second)
        val expectedDiff = StringDiff.Replace(2, 4, "XYZ")

        assertEquals(expectedDiff, res)
    }

    @Test
    fun noDifferenceTest() {
        val first = "same string"
        val second = "same string"
        val res = compare(first, second)

        assertNull(res)
    }

    @Test
    fun shiftTest() {
        val first = "first\nthird"
        val second = "first\n\nthird"
        val res = compare(first, second)
        val expectedDiff = StringDiff.Add(5, "\n")

        assertEquals(expectedDiff, res)
    }

    @Test
    fun complexShiftTest() {
        val first = "- first\n- third"
        val second = "- first\n- \n- third"
        val res = compare(first, second)
        val expectedDiff = StringDiff.Add(7, "\n- ")

        assertEquals(expectedDiff, res)
    }
}
