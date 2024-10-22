package com.aidarn.richedit.controllers

import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import com.aidarn.richedit.Style
import com.aidarn.richedit.StyleType
import com.aidarn.richedit.data.Markup.Companion.toMarkup
import com.aidarn.richedit.data.Pattern
import com.aidarn.richedit.data.RenderElement
import com.aidarn.richedit.data.ValueChangedVisitor
import com.aidarn.richedit.ext.overlaps
import com.aidarn.richedit.ext.replaceSpanStylesWithStyle
import com.aidarn.richedit.ext.toggleSpanStyleAtSelection
import com.aidarn.richedit.ext.withStyle
import com.aidarn.richedit.ext.withoutSpanStyle
import kotlin.math.max
import kotlin.math.min
import kotlin.time.Duration


class RichEditStateController(markupString: String, private val onChanged: (String) -> Unit) {
    private var currentlySelectedText: TextRange = TextRange(0)
    private var currentlySelectedIndex: Int = -1
    private val valueChangedVisitor = ValueChangedVisitor()
    private val unorderedListEntryRegex = Pattern.Head.ProcessedUnorderedList
    private val orderedListEntryRegex = Pattern.Head.OrderedList
    private var editingTimer: RenderElement.Timer? = null
    private var editingImage: RenderElement.Image? = null
    var elementsState = mutableStateOf<List<RenderElement>>(emptyList())
        private set
    var selectionState = mutableStateOf(SelectionInfo())
        private set
    var imagePickerDialogState = mutableStateOf(false)
        private set
    var timerDialogState = mutableStateOf(false)
        private set

    internal val elements by elementsState

    init {
        val markup = markupString.toMarkup()
        elementsState.value = markup.toRenderElements()
    }

    fun onTextValueChanged(index: Int, newValue: TextFieldValue) {
        currentlySelectedIndex = index
        val element = elementAtIndex<RenderElement.Text>(index) ?: return

        val processedString =
            InputController.processText(element.content.annotatedString, newValue.text)
        currentlySelectedText = SelectionController.processSelection(
            currentlySelectedText, element.content.text, processedString.text, false
        ) ?: newValue.selection

        val processedValue =
            newValue.copy(annotatedString = processedString, selection = currentlySelectedText)
        onTextValueChangedCore(index, RenderElement.Text(processedValue))
    }

    fun onHeading2Clicked() {
        onHeadingClicked(StyleType.Heading2)
    }

    fun onHeading1Clicked() {
        onHeadingClicked(StyleType.Heading1)
    }

    fun onItalicClicked() {
        onTextDecorationClickedClicked(StyleType.Italic)
    }

    fun onBoldClicked() {
        onTextDecorationClickedClicked(StyleType.Bold)
    }

    fun onImageAddClicked() {
        imagePickerDialogState.value = true
    }

    fun onTimerAddClicked() {
        timerDialogState.value = true
    }

    fun onImagePicked(uri: Uri, description: String) {
        val imageElement = RenderElement.Image(uri, description)
        if (editingImage == null) {
            val textElement = elementAtIndex<RenderElement.Text>(currentlySelectedIndex)
            addNewElementAtElement(textElement, imageElement)
        } else {
            replaceElementWith(editingImage, imageElement)
            editingImage = null
        }

        imagePickerDialogState.value = false
    }

    fun onImagePickerDismissed() {
        imagePickerDialogState.value = false
    }

    fun onUnorderedListClicked(state: Boolean) {
        val currentElement =
            elementAtIndex<RenderElement.Text>(currentlySelectedIndex) ?: return

        val newTextFieldValue = if (state) {
            InteractionController.startUnorderedList(currentElement.content)
        } else {
            InteractionController.removeUnorderedList(currentElement.content)
        }
        onTextValueChangedCore(currentlySelectedIndex, RenderElement.Text(newTextFieldValue))
    }

    fun onOrderedListClicked(state: Boolean) {
        val currentElement =
            elementAtIndex<RenderElement.Text>(currentlySelectedIndex) ?: return

        val newTextFieldValue = if (state) {
            InteractionController.startOrderedList(currentElement.content)
        } else {
            InteractionController.removeOrderedList(currentElement.content)
        }
        onTextValueChangedCore(currentlySelectedIndex, RenderElement.Text(newTextFieldValue))
    }

    fun onTimerDialogDismissed() {
        timerDialogState.value = false
    }

    fun onTimerPicked(duration: Duration) {
        val timerElement = RenderElement.Timer(duration)
        if (editingTimer == null) {
            val textElement = elementAtIndex<RenderElement.Text>(currentlySelectedIndex)
            addNewElementAtElement(textElement, timerElement)
        } else {
            replaceElementWith(editingTimer, timerElement)
            editingTimer = null
        }

        timerDialogState.value = false
    }

    fun onTimerEditClicked(index: Int) {
        val timerElement = elementAtIndex<RenderElement.Timer>(index) ?: return
        editingTimer = timerElement
        timerDialogState.value = true
    }

    fun onItemRemoveClicked(index: Int) {
        removeElement(index)
    }

    fun onImageEditClicked(index: Int) {
        val imageElement = elementAtIndex<RenderElement.Image>(index) ?: return
        editingImage = imageElement
        imagePickerDialogState.value = true
    }

    private fun onTextDecorationClickedClicked(styleType: StyleType) {
        val renderElement =
            elementAtIndex<RenderElement.Text>(currentlySelectedIndex) ?: return
        val spanStyle = Style[styleType]
        val annotatedString = renderElement.content.annotatedString.toggleSpanStyleAtSelection(
            spanStyle, renderElement.content.selection
        )
        val newTextElement =
            RenderElement.Text(renderElement.content.copy(annotatedString = annotatedString))
        onTextValueChangedCore(currentlySelectedIndex, newTextElement)
    }

    private fun calcNewSelectionState(text: AnnotatedString, textRange: TextRange): SelectionInfo {
        var isH1Selected = false
        var isH2Selected = false
        var isItalic = false
        var isBold = false
        var isUnorderedList = false
        var isOrderedList = false
        for (spanStyle in text.spanStyles) {
            if (overlapIsNewlineOrEmpty(text.text, spanStyle, textRange))
                continue
            val spanType = Style.firstMatchingKey(spanStyle.item)
            when (spanType) {
                StyleType.Heading1 -> isH1Selected = true
                StyleType.Heading2 -> isH2Selected = true
                StyleType.Bold -> isBold = true
                StyleType.Italic -> isItalic = true
                StyleType.Plain -> Unit
            }
        }
        for (paragraphStyle in text.paragraphStyles) {
            if (overlapIsNewlineOrEmpty(text.text, paragraphStyle, textRange))
                continue
            val subText = if (textRange.length > 0) {
                val start = text.text.findEnclosing("\n", textRange.start)
                val end = text.text.findEnclosing("\n", textRange.end)
                text.text.substring(start.first + 1, end.second)
            } else {
                var i = max(0, textRange.start - 1)
                while (i >= 1) {
                    if (text.text[i - 1] == '\n') {
                        break
                    }
                    i -= 1
                }
                text.substring(i, textRange.start)
            }
            if (unorderedListEntryRegex.find(subText) != null) {
                isUnorderedList = true
            }
            if (orderedListEntryRegex.find(subText) != null) {
                isOrderedList = true
            }
        }

        return SelectionInfo(
            isH1Selected = isH1Selected,
            isH2Selected = isH2Selected,
            isBold = isBold,
            isItalic = isItalic,
            isOrderedList = isOrderedList,
            isUnorderedList = isUnorderedList
        )
    }

    private fun <T> overlapIsNewlineOrEmpty(
        text: String,
        range: AnnotatedString.Range<T>,
        textRange: TextRange
    ): Boolean {
        if (!range.overlaps(textRange)) return true
        if (textRange.length == 0) return textRange.start - 1 in text.indices && text[textRange.start - 1] == '\n'
        val start = max(0, textRange.start - 1)
        val end = min(text.length, textRange.end)
        val spanRange = range.start until range.end
        val selectionRange = start until end
        return spanRange.intersect(selectionRange).all {
            text[it] == '\n'
        }
    }

    private fun onTextValueChangedCore(index: Int, newValue: RenderElement.Text) {
        elements.forEachIndexed { i, renderElement ->
            if (i != index) {
                renderElement.acceptVisitor(valueChangedVisitor)
            } else {
                newValue.acceptVisitor(valueChangedVisitor)
            }
        }
        val newElements = buildList {
            addAll(elements.slice(0..<index))
            add(newValue)
            if (index + 1 < elements.size) {
                addAll(elements.slice(index + 1..<elements.size))
            }
        }
        updateElements(newElements)
        updateSelection(newValue)
        onMarkupChanged(valueChangedVisitor.getMarkupString())
        valueChangedVisitor.reset()
    }

    private fun updateSelection(element: RenderElement.Text) {
        currentlySelectedText = element.content.selection
        selectionState.value =
            calcNewSelectionState(element.content.annotatedString, currentlySelectedText)
    }

    private fun updateElements(newElements: List<RenderElement>) {
        elementsState.value = newElements
    }

    private fun onHeadingClicked(headingStyleType: StyleType) {
        val renderElement =
            elementAtIndex<RenderElement.Text>(currentlySelectedIndex) ?: return

        val style = renderElement.content.annotatedString.spanStyles.firstOrNull { spanStyle ->
            spanStyle.overlaps(currentlySelectedText) && (spanStyle.item == Style[StyleType.Heading1] || spanStyle.item == Style[StyleType.Heading2])
        }
        val annotatedString = if (style != null) {
            if (style.item == Style[headingStyleType]) {
                renderElement.content.annotatedString.withoutSpanStyle(style)
            } else {
                renderElement.content.annotatedString.replaceSpanStylesWithStyle(
                    listOf(style), Style[headingStyleType], style.start until style.end
                )
            }
        } else {
            val (start, end) = renderElement.content.text.findEnclosing(
                "\n", currentlySelectedText.start
            )
            renderElement.content.annotatedString.withStyle(Style[headingStyleType], start + 1, end)
        }
        val newTextElement =
            RenderElement.Text(renderElement.content.copy(annotatedString = annotatedString))
        onTextValueChangedCore(currentlySelectedIndex, newTextElement)
    }

    private fun onMarkupChanged(markupString: String) {
        onChanged(markupString)
    }

    private fun addNewElementAtElement(
        textElement: RenderElement.Text?, newElement: RenderElement
    ) {
        val newElements = mutableListOf<RenderElement>()
        val processElement = { element: RenderElement ->
            element.acceptVisitor(valueChangedVisitor)
            newElements.add(element)
        }
        if (textElement == null || currentlySelectedText == TextRange(textElement.content.text.length)) {
            elements.subList(0, min(elements.size, currentlySelectedIndex + 1)).forEach {
                processElement(it)
            }
            processElement(newElement)
            if (currentlySelectedIndex + 1 < elements.size) {
                val nextElement =
                    elementAtIndex<RenderElement.Text>(currentlySelectedIndex + 1)
                if (nextElement == null) {
                    processElement(RenderElement.Text(TextFieldValue()))
                }
                elements.subList(currentlySelectedIndex + 1, elements.size).forEach {
                    processElement(it)
                }
            } else {
                processElement(RenderElement.Text(TextFieldValue()))
            }
        } else {
            for (i in elements.indices) {
                if (i != currentlySelectedIndex) {
                    processElement(elements[i])
                    continue
                }
                val beforeTextElement = textElement.withText(
                    textElement.content.text.substring(
                        0, currentlySelectedText.start
                    )
                )
                val afterTextElement =
                    textElement.withText(textElement.content.text.substring(currentlySelectedText.end))
                processElement(beforeTextElement)
                processElement(newElement)
                processElement(afterTextElement)
            }
        }
        onMarkupChanged(valueChangedVisitor.getMarkupString())
        valueChangedVisitor.reset()
        elementsState.value = newElements
    }

    private fun replaceElementWith(element: RenderElement?, newElement: RenderElement) {
        val newElements = mutableListOf<RenderElement>()
        val processElement = { renderElement: RenderElement ->
            renderElement.acceptVisitor(valueChangedVisitor)
            newElements.add(renderElement)
        }
        for (i in elements.indices) {
            if (elements[i] != element) {
                processElement(elements[i])
            } else {
                processElement(newElement)
            }
        }
        onMarkupChanged(valueChangedVisitor.getMarkupString())
        valueChangedVisitor.reset()
        elementsState.value = newElements
    }

    private fun removeElement(index: Int) {
        val newElements = mutableListOf<RenderElement>()
        val processElement = { renderElement: RenderElement ->
            renderElement.acceptVisitor(valueChangedVisitor)
            newElements.add(renderElement)
        }
        val elementBefore = elementAtIndex<RenderElement.Text>(index - 1)
        val elementAfter = elementAtIndex<RenderElement.Text>(index + 1)
        if (elementAfter != null && elementBefore != null) {
            val newElement = mergeTextElements(elementBefore, elementAfter)
            for (i in elements.indices) {
                if (i < index - 1 || i > index + 1) {
                    processElement(elements[i])
                } else if (i == index) {
                    processElement(newElement)
                }
            }
        } else {
            for (i in elements.indices) {
                if (i != index) {
                    processElement(elements[i])
                }
            }
        }
        onMarkupChanged(valueChangedVisitor.getMarkupString())
        valueChangedVisitor.reset()
        elementsState.value = newElements
    }

    private fun mergeTextElements(
        first: RenderElement.Text,
        second: RenderElement.Text
    ): RenderElement.Text {
        val newAnnotatedString = first.content.annotatedString + second.content.annotatedString
        val newValue = first.content.copy(annotatedString = newAnnotatedString)
        return RenderElement.Text(newValue)
    }

    private inline fun <reified T : RenderElement> elementAtIndex(index: Int): T? {
        if (index !in elements.indices) return null
        val renderElement = elements[index]
        return if (renderElement is T) renderElement
        else null
    }
}

private fun String.findEnclosing(s: String, start: Int): Pair<Int, Int> {
    var i = start
    while (i > 0) {
        if (startsWith(s, i)) break
        i -= 1
    }
    val first = when {
        i > 0 -> i
        i == 0 && startsWith(s) -> 0
        else -> -1
    }
    i = start
    while (i < length) {
        if (startsWith(s, i)) break
        i += 1
    }
    return Pair(first, i)
}
