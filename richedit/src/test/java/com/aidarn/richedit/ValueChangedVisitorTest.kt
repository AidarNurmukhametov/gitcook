package com.aidarn.richedit

import androidx.compose.ui.text.input.TextFieldValue
import com.aidarn.richedit.data.RenderElement
import com.aidarn.richedit.data.ValueChangedVisitor
import junit.framework.TestCase.assertEquals
import org.junit.After
import org.junit.Test
import kotlin.time.DurationUnit
import kotlin.time.toDuration

class ValueChangedVisitorTest {
    private var valueChangedVisitor = ValueChangedVisitor()

    @After
    fun tearDown() {
        valueChangedVisitor.reset()
    }

    @Test
    fun textElementVisitTest() {
        val textElement = RenderElement.Text(TextFieldValue("first"))
        textElement.acceptVisitor(valueChangedVisitor)
        val expectedString = "first"
        assertEquals(expectedString, valueChangedVisitor.getMarkupString())
    }

    @Test
    fun imageElementVisitTest() {
        val imageUri = "http://api.example.org/data/"
        val imageElement = RenderElement.Image(imageUri, "image placeholder")
        imageElement.acceptVisitor(valueChangedVisitor)
        val expectedString = "\n![image placeholder](http://api.example.org/data/)\n"
        assertEquals(expectedString, valueChangedVisitor.getMarkupString())
    }

    @Test
    fun timerElementVisitTest() {
        val duration = 1.5.toDuration(DurationUnit.MINUTES)
        val timerElement = RenderElement.Timer(duration)
        timerElement.acceptVisitor(valueChangedVisitor)
        val expectedString = "\n>!TIMER PT1M30S\n"
        assertEquals(expectedString, valueChangedVisitor.getMarkupString())
    }

    @Test
    fun textImageElementsVisitTest() {
        val textElement = RenderElement.Text(TextFieldValue("first"))
        val imageUri = "http://api.example.org/data/"
        val imageElement = RenderElement.Image(imageUri, "image placeholder")
        textElement.acceptVisitor(valueChangedVisitor)
        imageElement.acceptVisitor(valueChangedVisitor)
        val expectedString = "first\n![image placeholder](http://api.example.org/data/)\n"
        assertEquals(expectedString, valueChangedVisitor.getMarkupString())
    }

    @Test
    fun textTimerElementsVisitTest() {
        val textElement = RenderElement.Text(TextFieldValue("first"))
        val duration = 1.5.toDuration(DurationUnit.MINUTES)
        val timerElement = RenderElement.Timer(duration)
        textElement.acceptVisitor(valueChangedVisitor)
        timerElement.acceptVisitor(valueChangedVisitor)
        val expectedString = "first\n>!TIMER PT1M30S\n"
        assertEquals(expectedString, valueChangedVisitor.getMarkupString())
    }

    @Test
    fun imageTextElementsVisitTest() {
        val imageUri = "http://api.example.org/data/"
        val imageElement = RenderElement.Image(imageUri, "image placeholder")
        val textElement = RenderElement.Text(TextFieldValue("first"))
        imageElement.acceptVisitor(valueChangedVisitor)
        textElement.acceptVisitor(valueChangedVisitor)
        val expectedString = "\n![image placeholder](http://api.example.org/data/)\nfirst"
        assertEquals(expectedString, valueChangedVisitor.getMarkupString())
    }

    @Test
    fun imageTimerElementsVisitTest() {
        val imageUri = "http://api.example.org/data/"
        val imageElement = RenderElement.Image(imageUri, "image placeholder")
        val duration = 1.5.toDuration(DurationUnit.MINUTES)
        val timerElement = RenderElement.Timer(duration)
        imageElement.acceptVisitor(valueChangedVisitor)
        timerElement.acceptVisitor(valueChangedVisitor)
        val expectedString =
            "\n![image placeholder](http://api.example.org/data/)\n>!TIMER PT1M30S\n"
        assertEquals(expectedString, valueChangedVisitor.getMarkupString())
    }

    @Test
    fun timerTextElementsVisitTest() {
        val duration = 1.5.toDuration(DurationUnit.MINUTES)
        val timerElement = RenderElement.Timer(duration)
        val textElement = RenderElement.Text(TextFieldValue("first"))
        timerElement.acceptVisitor(valueChangedVisitor)
        textElement.acceptVisitor(valueChangedVisitor)
        val expectedString = "\n>!TIMER PT1M30S\nfirst"
        assertEquals(expectedString, valueChangedVisitor.getMarkupString())
    }

    @Test
    fun timerImageElementsVisitTest() {
        val duration = 1.5.toDuration(DurationUnit.MINUTES)
        val timerElement = RenderElement.Timer(duration)
        val imageUri = "http://api.example.org/data/"
        val imageElement = RenderElement.Image(imageUri, "image placeholder")
        timerElement.acceptVisitor(valueChangedVisitor)
        imageElement.acceptVisitor(valueChangedVisitor)
        val expectedString =
            "\n>!TIMER PT1M30S\n![image placeholder](http://api.example.org/data/)\n"
        assertEquals(expectedString, valueChangedVisitor.getMarkupString())
    }

    @Test
    fun textImageTimerElementsVisitTest() {
        val textElement = RenderElement.Text(TextFieldValue("first"))
        val imageUri = "http://api.example.org/data/"
        val imageElement = RenderElement.Image(imageUri, "image placeholder")
        val duration = 1.5.toDuration(DurationUnit.MINUTES)
        val timerElement = RenderElement.Timer(duration)
        textElement.acceptVisitor(valueChangedVisitor)
        imageElement.acceptVisitor(valueChangedVisitor)
        timerElement.acceptVisitor(valueChangedVisitor)
        val expectedString =
            "first\n![image placeholder](http://api.example.org/data/)\n>!TIMER PT1M30S\n"
        assertEquals(expectedString, valueChangedVisitor.getMarkupString())
    }

    @Test
    fun textTimerImageElementsVisitTest() {
        val textElement = RenderElement.Text(TextFieldValue("first"))
        val duration = 1.5.toDuration(DurationUnit.MINUTES)
        val timerElement = RenderElement.Timer(duration)
        val imageUri = "http://api.example.org/data/"
        val imageElement = RenderElement.Image(imageUri, "image placeholder")
        textElement.acceptVisitor(valueChangedVisitor)
        timerElement.acceptVisitor(valueChangedVisitor)
        imageElement.acceptVisitor(valueChangedVisitor)
        val expectedString =
            "first\n>!TIMER PT1M30S\n![image placeholder](http://api.example.org/data/)\n"
        assertEquals(expectedString, valueChangedVisitor.getMarkupString())
    }

    @Test
    fun imageTextTimerElementsVisitTest() {
        val imageUri = "http://api.example.org/data/"
        val imageElement = RenderElement.Image(imageUri, "image placeholder")
        val textElement = RenderElement.Text(TextFieldValue("first"))
        val duration = 1.5.toDuration(DurationUnit.MINUTES)
        val timerElement = RenderElement.Timer(duration)
        imageElement.acceptVisitor(valueChangedVisitor)
        textElement.acceptVisitor(valueChangedVisitor)
        timerElement.acceptVisitor(valueChangedVisitor)
        val expectedString =
            "\n![image placeholder](http://api.example.org/data/)\nfirst\n>!TIMER PT1M30S\n"
        assertEquals(expectedString, valueChangedVisitor.getMarkupString())
    }

    @Test
    fun imageTimerTextElementsVisitTest() {
        val imageUri = "http://api.example.org/data/"
        val imageElement = RenderElement.Image(imageUri, "image placeholder")
        val duration = 1.5.toDuration(DurationUnit.MINUTES)
        val timerElement = RenderElement.Timer(duration)
        val textElement = RenderElement.Text(TextFieldValue("first"))
        imageElement.acceptVisitor(valueChangedVisitor)
        timerElement.acceptVisitor(valueChangedVisitor)
        textElement.acceptVisitor(valueChangedVisitor)
        val expectedString =
            "\n![image placeholder](http://api.example.org/data/)\n>!TIMER PT1M30S\nfirst"
        assertEquals(expectedString, valueChangedVisitor.getMarkupString())
    }

    @Test
    fun timerTextImageElementsVisitTest() {
        val duration = 1.5.toDuration(DurationUnit.MINUTES)
        val timerElement = RenderElement.Timer(duration)
        val textElement = RenderElement.Text(TextFieldValue("first"))
        val imageUri = "http://api.example.org/data/"
        val imageElement = RenderElement.Image(imageUri, "image placeholder")
        timerElement.acceptVisitor(valueChangedVisitor)
        textElement.acceptVisitor(valueChangedVisitor)
        imageElement.acceptVisitor(valueChangedVisitor)
        val expectedString =
            "\n>!TIMER PT1M30S\nfirst\n![image placeholder](http://api.example.org/data/)\n"
        assertEquals(expectedString, valueChangedVisitor.getMarkupString())
    }

    @Test
    fun timerImageTextElementsVisitTest() {
        val duration = 1.5.toDuration(DurationUnit.MINUTES)
        val timerElement = RenderElement.Timer(duration)
        val imageUri = "http://api.example.org/data/"
        val imageElement = RenderElement.Image(imageUri, "image placeholder")
        val textElement = RenderElement.Text(TextFieldValue("first"))
        timerElement.acceptVisitor(valueChangedVisitor)
        imageElement.acceptVisitor(valueChangedVisitor)
        textElement.acceptVisitor(valueChangedVisitor)
        val expectedString =
            "\n>!TIMER PT1M30S\n![image placeholder](http://api.example.org/data/)\nfirst"
        assertEquals(expectedString, valueChangedVisitor.getMarkupString())
    }
}