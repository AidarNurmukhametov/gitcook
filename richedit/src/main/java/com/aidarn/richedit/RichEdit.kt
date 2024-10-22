package com.aidarn.richedit

import android.content.Intent
import android.view.View
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.aidarn.richedit.controllers.RichEditStateController
import com.aidarn.richedit.data.RenderElement
import com.aidarn.richedit.dialog.TimerDialog
import com.aidarn.richedit.edit.ImageEdit
import com.aidarn.richedit.edit.TextEdit
import com.aidarn.richedit.edit.TimerEdit
import com.aidarn.richedit.toolbar.RichEditToolbar


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun RichEdit(
    markupString: String, onMarkupChanged: (String) -> Unit, modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val stateController by remember {
        mutableStateOf(RichEditStateController(markupString, onMarkupChanged))
    }
    val photoDescription = stringResource(R.string.cooking_stage_photo)
    val activityResultLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            val flag = Intent.FLAG_GRANT_READ_URI_PERMISSION
            val res =
                uri?.let { context.contentResolver.takePersistableUriPermission(uri, flag); uri }
            if (res != null) {
                stateController.onImagePicked(res, photoDescription)
            } else {
                stateController.onImagePickerDismissed()
            }
        }
    val bringIntoViewRequester = remember { BringIntoViewRequester() }
    var textFieldValue by remember { mutableStateOf(TextFieldValue()) }
    var layoutResult by remember { mutableStateOf<TextLayoutResult?>(null) }

    val selection = textFieldValue.selection
    val elements by stateController.elementsState
    val selectionInfo by stateController.selectionState
    val isTimerDialogShown by stateController.timerDialogState
    val isImagePickerDialogShown by stateController.imagePickerDialogState
    var isKeyboardVisible by remember { mutableStateOf(false) }

    val view = LocalView.current
    DisposableEffect(view) {
        val listener = { _: View, insets: WindowInsetsCompat ->
            val isVisible = insets.isVisible(WindowInsetsCompat.Type.ime())
            isKeyboardVisible = isVisible
            insets
        }
        ViewCompat.setOnApplyWindowInsetsListener(view, listener)
        onDispose {
            ViewCompat.setOnApplyWindowInsetsListener(view, null)
        }
    }
    LaunchedEffect(isImagePickerDialogShown) {
        if (isImagePickerDialogShown) {
            activityResultLauncher.launch(
                PickVisualMediaRequest(
                    ActivityResultContracts.PickVisualMedia.ImageOnly
                )
            )
        }
    }
    LaunchedEffect(selection) {
        if (isKeyboardVisible) {
            layoutResult?.let { layoutResult ->
                val (top, bottom) = layoutResult.cursorCoordinates(selection)
                bringIntoViewRequester.bringIntoView(Rect(0f, top, 0f, bottom))
            }
        }
    }
    if (isTimerDialogShown) {
        TimerDialog(onDismissRequest = stateController::onTimerDialogDismissed) {
            stateController.onTimerPicked(it)
        }
    }
    Column(modifier = modifier) {
        RichEditToolbar(
            selectionInfo,
            onHeading1Clicked = stateController::onHeading1Clicked,
            onHeading2Clicked = stateController::onHeading2Clicked,
            onBoldClicked = stateController::onBoldClicked,
            onItalicClicked = stateController::onItalicClicked,
            onUnorderedListClicked = stateController::onUnorderedListClicked,
            onOrderedListClicked = stateController::onOrderedListClicked,
            onImageAddClicked = stateController::onImageAddClicked,
            onTimerAddClicked = stateController::onTimerAddClicked,
        )
        RichEditContent(
            elements = elements,
            bringIntoViewRequester = bringIntoViewRequester,
            onTextLayout = { layoutResult = it },
            onValueChanged = { index, value ->
                textFieldValue = value; stateController.onTextValueChanged(index, value)
            },
            onImageEditClicked = { stateController.onImageEditClicked(it) },
            onTimerEditClicked = { stateController.onTimerEditClicked(it) },
            onItemRemoveClicked = { stateController.onItemRemoveClicked(it) },
            modifier = Modifier.padding(vertical = 4.dp)
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun RichEditContent(
    elements: List<RenderElement>,
    bringIntoViewRequester: BringIntoViewRequester,
    onTextLayout: (TextLayoutResult) -> Unit,
    onValueChanged: (Int, TextFieldValue) -> Unit,
    onImageEditClicked: (Int) -> Unit,
    onTimerEditClicked: (Int) -> Unit,
    onItemRemoveClicked: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        elements.forEachIndexed { index, renderElement ->
            when (renderElement) {
                is RenderElement.Text -> {
                    TextEdit(value = renderElement.content,
                        bringIntoViewRequester = bringIntoViewRequester,
                        onTextLayout = onTextLayout,
                        onValueChanged = { onValueChanged(index, it) }
                    )
                }

                is RenderElement.Image -> {
                    ImageEdit(element = renderElement,
                        onEditClicked = { onImageEditClicked(index) },
                        onRemoveClicked = { onItemRemoveClicked(index) }
                    )
                }

                is RenderElement.Timer -> {
                    TimerEdit(duration = renderElement.content,
                        onEditClicked = { onTimerEditClicked(index) },
                        onRemoveClicked = { onItemRemoveClicked(index) }
                    )
                }
            }
        }
    }
}

fun TextLayoutResult.cursorCoordinates(selection: TextRange): Pair<Float, Float> {
    val currentLine = try {
        getLineForOffset(selection.end)
    } catch (ex: IllegalArgumentException) {
        getLineForOffset(selection.end - 1)
    }
    val lineTop = getLineTop(currentLine)
    val lineBottom = getLineBottom(currentLine)
    return lineTop to lineBottom
}

@Preview(showBackground = true)
@Composable
fun RichEditPreview() {
    val actualString = """
            # Recipe
            
            Step by step guide 
            1. Turn on the heat. Let owen preheat 10 minutes
            >!TIMER PT10M
            
            ![desc](IMAGE_PLACEHOLDER)
            2. Cut a fish
            3. Add cut fish to the cake
            ![desc](IMAGE_PLACEHOLDER)
            4. Bake 40 minutes
            >!TIMER PT40M
        """.trimIndent().replace("IMAGE_PLACEHOLDER", R.drawable.image.toString())

    var instructions by remember {
        mutableStateOf(actualString)
    }
    RichEdit(markupString = instructions, onMarkupChanged = {
        instructions = it
    })
}