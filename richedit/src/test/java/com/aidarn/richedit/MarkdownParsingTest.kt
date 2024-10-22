package com.aidarn.richedit

import com.aidarn.richedit.data.Markup.Companion.toMarkup
import com.aidarn.richedit.data.Token
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import org.junit.Test
import kotlin.time.DurationUnit
import kotlin.time.toDuration


class MarkdownParsingTest {
    @Test
    fun boldParseTest() {
        val string = "**test**"
        val tokens = string.toMarkup()

        assertEquals(1, tokens.size)
        assertTrue(tokens[0] is Token.Bold)
        val token = tokens[0] as Token.Bold
        assertEquals(1, token.content.size)
        assertTrue(token.content[0] is Token.Text)
        val textToken = token.content[0] as Token.Text
        assertEquals("test", textToken.content)
    }

    @Test
    fun italicParseTest() {
        val string = "_test_"
        val tokens = string.toMarkup()

        assertEquals(1, tokens.size)
        assertTrue(tokens[0] is Token.Italic)
        val token = tokens[0] as Token.Italic
        assertEquals(1, token.content.size)
        assertTrue(token.content[0] is Token.Text)
        val textToken = token.content[0] as Token.Text
        assertEquals("test", textToken.content)
    }

    @Test
    fun heading1ParseTest() {
        val string = "# test"
        val tokens = string.toMarkup()

        assertEquals(1, tokens.size)
        assertTrue(tokens[0] is Token.Heading)
        val token = tokens[0] as Token.Heading
        assertEquals(1, token.content.size)
        assertEquals(1, token.level)
        assertTrue(token.content[0] is Token.Text)
        val textToken = token.content[0] as Token.Text
        assertEquals("test", textToken.content)
    }

    @Test
    fun heading2ParseTest() {
        val string = "## test"
        val tokens = string.toMarkup()

        assertEquals(1, tokens.size)
        assertTrue(tokens[0] is Token.Heading)
        val token = tokens[0] as Token.Heading
        assertEquals(1, token.content.size)
        assertEquals(2, token.level)
        assertTrue(token.content[0] is Token.Text)
        val textToken = token.content[0] as Token.Text
        assertEquals("test", textToken.content)
    }

    @Test
    fun unorderedListTest() {
        val string = """
            - First
            - Second
        """.trimIndent()
        val tokens = string.toMarkup()

        assertEquals(1, tokens.size)
        assertTrue(tokens[0] is Token.UnorderedList)
        val token = tokens[0] as Token.UnorderedList
        assertEquals(2, token.content.size)
        val first = token.content[0]
        val second = token.content[1]
        assertEquals(1, first.size)
        assertEquals(1, second.size)
        assertTrue(first[0] is Token.Text)
        assertTrue(second[0] is Token.Text)
    }

    @Test
    fun orderedListTest() {
        val string = """
            1. First
            2. Second
        """.trimIndent()
        val tokens = string.toMarkup()

        assertEquals(1, tokens.size)
        assertTrue(tokens[0] is Token.OrderedList)
        val token = tokens[0] as Token.OrderedList
        assertEquals(2, token.content.size)
        val first = token.content[0]
        val second = token.content[1]
        assertEquals(1, first.size)
        assertEquals(1, second.size)
        assertTrue(first[0] is Token.Text)
        assertTrue(second[0] is Token.Text)
    }

    @Test
    fun timerTest() {
        val string = ">!TIMER PT1H30M"
        val tokens = string.toMarkup()

        assertEquals(1, tokens.size)
        assertTrue(tokens[0] is Token.Timer)
        val token = tokens[0] as Token.Timer
        assertEquals(90.toDuration(DurationUnit.MINUTES), token.duration)
    }

    @Test
    fun imageTest() {
        val imageId = 123
        val string = "![test]($imageId)"
        val tokens = string.toMarkup()

        assertEquals(1, tokens.size)
        assertTrue(tokens[0] is Token.Image)
        val token = tokens[0] as Token.Image
        assertEquals("test", token.placeholderText)
        assertEquals(imageId, token.imageModel)
    }

    @Test
    fun imageTest1() {
        val imageId = "/data/data/images/1"
        val string = "![test]($imageId)"
        val tokens = string.toMarkup()

        assertEquals(1, tokens.size)
        assertTrue(tokens[0] is Token.Image)
        val token = tokens[0] as Token.Image
        assertEquals("test", token.placeholderText)
        assertEquals(imageId, token.imageModel)
    }

    @Test
    fun boldAndItalicParseTest() {
        val string = "**_test_**"
        val tokens = string.toMarkup()

        assertEquals(1, tokens.size)
        assertTrue(tokens[0] is Token.Bold)
        val boldToken = tokens[0] as Token.Bold
        assertEquals(1, boldToken.content.size)
        assertTrue(boldToken.content[0] is Token.Italic)
        val italicToken = boldToken.content[0] as Token.Italic
        assertEquals(1, italicToken.content.size)
        val textToken = italicToken.content[0] as Token.Text
        assertEquals("test", textToken.content)
    }

    @Test
    fun boldAndItalicParseTest1() {
        val string = "_**test**_"
        val tokens = string.toMarkup()

        assertEquals(1, tokens.size)
        assertTrue(tokens[0] is Token.Italic)
        val boldToken = tokens[0] as Token.Italic
        assertEquals(1, boldToken.content.size)
        assertTrue(boldToken.content[0] is Token.Bold)
        val italicToken = boldToken.content[0] as Token.Bold
        assertEquals(1, italicToken.content.size)
        val textToken = italicToken.content[0] as Token.Text
        assertEquals("test", textToken.content)
    }

    @Test
    fun multilineTest() {
        val string = """
            ## Head
            
            1. First
            2. Second
        """.trimIndent()
        val tokens = string.toMarkup()

        assertEquals(3, tokens.size)
        assertTrue(tokens[0] is Token.Heading)
        assertTrue(tokens[1] is Token.Text)
        assertTrue(tokens[2] is Token.OrderedList)
        val textToken = tokens[1] as Token.Text
        assertEquals("\n", textToken.content)
    }

    @Test
    fun multilineTest1() {
        val string = """
            ## Head
            
            Steps:
            1. First
            2. Second
        """.trimIndent()
        val tokens = string.toMarkup()

        assertEquals(3, tokens.size)
        assertTrue(tokens[0] is Token.Heading)
        assertTrue(tokens[1] is Token.Text)
        assertTrue(tokens[2] is Token.OrderedList)
        val textToken = tokens[1] as Token.Text
        assertEquals("\nSteps:\n", textToken.content)
    }

    @Test
    fun nestedUnorderedList() {
        val string = "- f - s"
        val tokens = string.toMarkup()

        assertEquals(1, tokens.size)
        assertTrue(tokens[0] is Token.UnorderedList)
        val token = tokens[0] as Token.UnorderedList
        assertEquals(1, token.content.size)
        val first = token.content[0]
        assertEquals(1, first.size)
        assertTrue(first[0] is Token.Text)
        val textToken = first[0] as Token.Text
        assertEquals("f - s", textToken.content)
    }
}