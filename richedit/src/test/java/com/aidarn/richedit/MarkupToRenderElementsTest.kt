package com.aidarn.richedit

import com.aidarn.richedit.data.Markup.Companion.toMarkup
import com.aidarn.richedit.data.RenderElement
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import org.junit.Test
import kotlin.time.DurationUnit
import kotlin.time.toDuration

class MarkupToRenderElementsTest {
    @Test
    fun textMarkupToRenderElementsTest() {
        val markup = "first".toMarkup()
        val elements = markup.toRenderElements()
        assertEquals(1, elements.size)
        assertTrue(elements[0] is RenderElement.Text)
        val textElement = elements[0] as RenderElement.Text
        assertEquals("first", textElement.content.text)
    }

    @Test
    fun imageMarkupToRenderElementsTest() {
        val markup = "\n![image placeholder](http://api.example.org/data/)\n".toMarkup()
        val elements = markup.toRenderElements()
        assertEquals(3, elements.size)
        assertTrue(elements[0] is RenderElement.Text)
        val firstTextElement = elements[0] as RenderElement.Text
        assertEquals("", firstTextElement.content.text)
        assertTrue(elements[1] is RenderElement.Image)
        val imageElement = elements[1] as RenderElement.Image
        assertEquals("image placeholder", imageElement.placeholder)
        assertEquals("http://api.example.org/data/", imageElement.content)
        assertTrue(elements[2] is RenderElement.Text)
        val secondTextElement = elements[0] as RenderElement.Text
        assertEquals("", secondTextElement.content.text)
    }

    @Test
    fun timerMarkupToRenderElementsTest() {
        val markup = "\n>!TIMER PT1M30S\n".toMarkup()
        val elements = markup.toRenderElements()
        assertEquals(3, elements.size)
        assertEquals(3, elements.size)
        assertTrue(elements[0] is RenderElement.Text)
        val firstTextElement = elements[0] as RenderElement.Text
        assertEquals("", firstTextElement.content.text)
        assertTrue(elements[1] is RenderElement.Timer)
        val timerElement = elements[1] as RenderElement.Timer
        assertEquals(1.5.toDuration(DurationUnit.MINUTES), timerElement.content)
        assertTrue(elements[2] is RenderElement.Text)
        val secondTextElement = elements[0] as RenderElement.Text
        assertEquals("", secondTextElement.content.text)
    }

    @Test
    fun textImageMarkupToRenderElementsTest() {
        val markup = "first\n![image placeholder](http://api.example.org/data/)\n".toMarkup()
        val elements = markup.toRenderElements()
        assertEquals(3, elements.size)
        assertTrue(elements[0] is RenderElement.Text)
        val firstTextElement = elements[0] as RenderElement.Text
        assertEquals("first", firstTextElement.content.text)
        assertTrue(elements[1] is RenderElement.Image)
        val imageElement = elements[1] as RenderElement.Image
        assertEquals("image placeholder", imageElement.placeholder)
        assertEquals("http://api.example.org/data/", imageElement.content)
        assertTrue(elements[2] is RenderElement.Text)
        val secondTextElement = elements[2] as RenderElement.Text
        assertEquals("", secondTextElement.content.text)
    }

    @Test
    fun textTimerMarkupToRenderElementsTest() {
        val markup = "first\n>!TIMER PT1M30S\n".toMarkup()
        val elements = markup.toRenderElements()
        assertEquals(3, elements.size)
        assertTrue(elements[0] is RenderElement.Text)
        val firstTextElement = elements[0] as RenderElement.Text
        assertEquals("first", firstTextElement.content.text)
        assertTrue(elements[1] is RenderElement.Timer)
        val timerElement = elements[1] as RenderElement.Timer
        assertEquals(1.5.toDuration(DurationUnit.MINUTES), timerElement.content)
        assertTrue(elements[2] is RenderElement.Text)
        val secondTextElement = elements[2] as RenderElement.Text
        assertEquals("", secondTextElement.content.text)
    }

    @Test
    fun imageTimerMarkupToRenderElementsTest() {
        val markup =
            "\n![image placeholder](http://api.example.org/data/)\n>!TIMER PT1M30S\n".toMarkup()
        val elements = markup.toRenderElements()
        assertEquals(5, elements.size)
        assertTrue(elements[0] is RenderElement.Text)
        val firstTextElement = elements[0] as RenderElement.Text
        assertEquals("", firstTextElement.content.text)
        assertTrue(elements[1] is RenderElement.Image)
        val imageElement = elements[1] as RenderElement.Image
        assertEquals("image placeholder", imageElement.placeholder)
        assertEquals("http://api.example.org/data/", imageElement.content)
        assertTrue(elements[2] is RenderElement.Text)
        val secondTextElement = elements[2] as RenderElement.Text
        assertEquals("", secondTextElement.content.text)
        assertTrue(elements[3] is RenderElement.Timer)
        val timerElement = elements[3] as RenderElement.Timer
        assertEquals(1.5.toDuration(DurationUnit.MINUTES), timerElement.content)
        assertTrue(elements[4] is RenderElement.Text)
        val thirdTextElement = elements[4] as RenderElement.Text
        assertEquals("", thirdTextElement.content.text)
    }

    @Test
    fun imageTextMarkupToRenderElementsTest() {
        val markup = "\n![image placeholder](http://api.example.org/data/)\nfirst".toMarkup()
        val elements = markup.toRenderElements()
        assertEquals(3, elements.size)
        assertTrue(elements[0] is RenderElement.Text)
        val firstTextElement = elements[0] as RenderElement.Text
        assertEquals("", firstTextElement.content.text)
        assertTrue(elements[1] is RenderElement.Image)
        val imageElement = elements[1] as RenderElement.Image
        assertEquals("image placeholder", imageElement.placeholder)
        assertEquals("http://api.example.org/data/", imageElement.content)
        assertTrue(elements[2] is RenderElement.Text)
        val secondTextElement = elements[2] as RenderElement.Text
        assertEquals("first", secondTextElement.content.text)
    }

    @Test
    fun timerTextMarkupToRenderElementsTest() {
        val markup = "\n>!TIMER PT1M30S\nfirst".toMarkup()
        val elements = markup.toRenderElements()
        assertEquals(3, elements.size)
        assertTrue(elements[0] is RenderElement.Text)
        val firstTextElement = elements[0] as RenderElement.Text
        assertEquals("", firstTextElement.content.text)
        assertTrue(elements[1] is RenderElement.Timer)
        val timerElement = elements[1] as RenderElement.Timer
        assertEquals(1.5.toDuration(DurationUnit.MINUTES), timerElement.content)
        assertTrue(elements[2] is RenderElement.Text)
        val secondTextElement = elements[2] as RenderElement.Text
        assertEquals("first", secondTextElement.content.text)
    }

    @Test
    fun textImageTimerMarkupToRenderElementsTest() {
        val markup =
            "first\n![image placeholder](http://api.example.org/data/)\n>!TIMER PT1M30S\n".toMarkup()
        val elements = markup.toRenderElements()
        assertEquals(5, elements.size)
        assertTrue(elements[0] is RenderElement.Text)
        val firstTextElement = elements[0] as RenderElement.Text
        assertEquals("first", firstTextElement.content.text)
        assertTrue(elements[1] is RenderElement.Image)
        val imageElement = elements[1] as RenderElement.Image
        assertEquals("image placeholder", imageElement.placeholder)
        assertEquals("http://api.example.org/data/", imageElement.content)
        assertTrue(elements[2] is RenderElement.Text)
        val secondTextElement = elements[2] as RenderElement.Text
        assertEquals("", secondTextElement.content.text)
        assertTrue(elements[3] is RenderElement.Timer)
        val timerElement = elements[3] as RenderElement.Timer
        assertEquals(1.5.toDuration(DurationUnit.MINUTES), timerElement.content)
        assertTrue(elements[4] is RenderElement.Text)
        val thirdTextElement = elements[4] as RenderElement.Text
        assertEquals("", thirdTextElement.content.text)
    }

    @Test
    fun textTimerImageMarkupToRenderElementsTest() {
        val markup =
            "first\n>!TIMER PT1M30S\n![image placeholder](http://api.example.org/data/)\n".toMarkup()
        val elements = markup.toRenderElements()
        assertEquals(5, elements.size)
        assertTrue(elements[0] is RenderElement.Text)
        val firstTextElement = elements[0] as RenderElement.Text
        assertEquals("first", firstTextElement.content.text)
        assertTrue(elements[1] is RenderElement.Timer)
        val timerElement = elements[1] as RenderElement.Timer
        assertEquals(1.5.toDuration(DurationUnit.MINUTES), timerElement.content)
        assertTrue(elements[2] is RenderElement.Text)
        val secondTextElement = elements[2] as RenderElement.Text
        assertEquals("", secondTextElement.content.text)
        assertTrue(elements[3] is RenderElement.Image)
        val imageElement = elements[3] as RenderElement.Image
        assertEquals("image placeholder", imageElement.placeholder)
        assertEquals("http://api.example.org/data/", imageElement.content)
        assertTrue(elements[4] is RenderElement.Text)
        val thirdTextElement = elements[4] as RenderElement.Text
        assertEquals("", thirdTextElement.content.text)
    }

    @Test
    fun imageTextTimerMarkupToRenderElementsTest() {
        val markup =
            "\n![image placeholder](http://api.example.org/data/)\nfirst\n>!TIMER PT1M30S\n".toMarkup()
        val elements = markup.toRenderElements()
        assertEquals(5, elements.size)
        assertTrue(elements[0] is RenderElement.Text)
        val firstTextElement = elements[0] as RenderElement.Text
        assertEquals("", firstTextElement.content.text)
        assertTrue(elements[1] is RenderElement.Image)
        val imageElement = elements[1] as RenderElement.Image
        assertEquals("image placeholder", imageElement.placeholder)
        assertEquals("http://api.example.org/data/", imageElement.content)
        assertTrue(elements[2] is RenderElement.Text)
        val secondTextElement = elements[2] as RenderElement.Text
        assertEquals("first", secondTextElement.content.text)
        assertTrue(elements[3] is RenderElement.Timer)
        val timerElement = elements[3] as RenderElement.Timer
        assertEquals(1.5.toDuration(DurationUnit.MINUTES), timerElement.content)
        assertTrue(elements[4] is RenderElement.Text)
        val thirdTextElement = elements[4] as RenderElement.Text
        assertEquals("", thirdTextElement.content.text)
    }

    @Test
    fun imageTimerTextMarkupToRenderElementsTest() {
        val markup =
            "\n![image placeholder](http://api.example.org/data/)\n>!TIMER PT1M30S\nfirst".toMarkup()
        val elements = markup.toRenderElements()
        assertEquals(5, elements.size)
        assertTrue(elements[0] is RenderElement.Text)
        val firstTextElement = elements[0] as RenderElement.Text
        assertEquals("", firstTextElement.content.text)
        assertTrue(elements[1] is RenderElement.Image)
        val imageElement = elements[1] as RenderElement.Image
        assertEquals("image placeholder", imageElement.placeholder)
        assertEquals("http://api.example.org/data/", imageElement.content)
        assertTrue(elements[2] is RenderElement.Text)
        val secondTextElement = elements[2] as RenderElement.Text
        assertEquals("", secondTextElement.content.text)
        assertTrue(elements[3] is RenderElement.Timer)
        val timerElement = elements[3] as RenderElement.Timer
        assertEquals(1.5.toDuration(DurationUnit.MINUTES), timerElement.content)
        assertTrue(elements[4] is RenderElement.Text)
        val thirdTextElement = elements[4] as RenderElement.Text
        assertEquals("first", thirdTextElement.content.text)
    }

    @Test
    fun timerTextImageMarkupToRenderElementsTest() {
        val markup =
            "\n>!TIMER PT1M30S\nfirst\n![image placeholder](http://api.example.org/data/)\n".toMarkup()
        val elements = markup.toRenderElements()
        assertEquals(5, elements.size)
        assertTrue(elements[0] is RenderElement.Text)
        val firstTextElement = elements[0] as RenderElement.Text
        assertEquals("", firstTextElement.content.text)
        assertTrue(elements[1] is RenderElement.Timer)
        val timerElement = elements[1] as RenderElement.Timer
        assertEquals(1.5.toDuration(DurationUnit.MINUTES), timerElement.content)
        assertTrue(elements[2] is RenderElement.Text)
        val secondTextElement = elements[2] as RenderElement.Text
        assertEquals("first", secondTextElement.content.text)
        assertTrue(elements[3] is RenderElement.Image)
        val imageElement = elements[3] as RenderElement.Image
        assertEquals("image placeholder", imageElement.placeholder)
        assertEquals("http://api.example.org/data/", imageElement.content)
        assertTrue(elements[4] is RenderElement.Text)
        val thirdTextElement = elements[4] as RenderElement.Text
        assertEquals("", thirdTextElement.content.text)
    }

    @Test
    fun timerImageTextMarkupToRenderElementsTest() {
        val markup =
            "\n>!TIMER PT1M30S\n![image placeholder](http://api.example.org/data/)\nfirst".toMarkup()
        val elements = markup.toRenderElements()
        assertEquals(5, elements.size)
        assertTrue(elements[0] is RenderElement.Text)
        val firstTextElement = elements[0] as RenderElement.Text
        assertEquals("", firstTextElement.content.text)
        assertTrue(elements[1] is RenderElement.Timer)
        val timerElement = elements[1] as RenderElement.Timer
        assertEquals(1.5.toDuration(DurationUnit.MINUTES), timerElement.content)
        assertTrue(elements[2] is RenderElement.Text)
        val secondTextElement = elements[2] as RenderElement.Text
        assertEquals("", secondTextElement.content.text)
        assertTrue(elements[3] is RenderElement.Image)
        val imageElement = elements[3] as RenderElement.Image
        assertEquals("image placeholder", imageElement.placeholder)
        assertEquals("http://api.example.org/data/", imageElement.content)
        assertTrue(elements[4] is RenderElement.Text)
        val thirdTextElement = elements[4] as RenderElement.Text
        assertEquals("first", thirdTextElement.content.text)
    }
}