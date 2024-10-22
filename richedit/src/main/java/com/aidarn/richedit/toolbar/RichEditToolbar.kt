package com.aidarn.richedit.toolbar

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.aidarn.richedit.R
import com.aidarn.richedit.Style
import com.aidarn.richedit.StyleType
import com.aidarn.richedit.controllers.SelectionInfo


@Composable
internal fun RichEditToolbar(
    selectionInfo: SelectionInfo,
    onHeading1Clicked: () -> Unit,
    onHeading2Clicked: () -> Unit,
    onBoldClicked: () -> Unit,
    onItalicClicked: () -> Unit,
    onUnorderedListClicked: (Boolean) -> Unit,
    onOrderedListClicked: (Boolean) -> Unit,
    onImageAddClicked: () -> Unit,
    onTimerAddClicked: () -> Unit
) {
    val cornerRadius = 16.dp
    val h1Label = remember {
        buildAnnotatedString {
            pushStyle(Style[StyleType.Heading1])
            append("H1")
            pop()
        }
    }
    val h2Label = remember {
        buildAnnotatedString {
            pushStyle(Style[StyleType.Heading2])
            append("H2")
            pop()
        }
    }
    val boldLabel = remember {
        buildAnnotatedString {
            pushStyle(Style[StyleType.Bold])
            append("Bold")
            pop()
        }
    }
    val italicLabel = remember {
        buildAnnotatedString {
            pushStyle(Style[StyleType.Italic])
            append("Italic")
            pop()
        }
    }
    Row(
        horizontalArrangement = Arrangement.spacedBy(4.dp), modifier = Modifier.horizontalScroll(
            rememberScrollState()
        )
    ) {
        Row {
            ToolbarItem(
                selected = selectionInfo.isH1Selected,
                shape = RoundedCornerShape(topStart = cornerRadius, bottomStart = cornerRadius),
                onClicked = { onHeading1Clicked() }
            ) {
                Text(text = h1Label)
            }
            ToolbarItem(
                selected = selectionInfo.isH2Selected,
                shape = RoundedCornerShape(topEnd = cornerRadius, bottomEnd = cornerRadius),
                onClicked = { onHeading2Clicked() }
            ) {
                Text(text = h2Label)
            }
        }
        Row {
            ToolbarItem(
                selected = selectionInfo.isBold,
                shape = RoundedCornerShape(topStart = cornerRadius, bottomStart = cornerRadius),
                onClicked = { onBoldClicked() }
            ) {
                Text(text = boldLabel)
            }
            ToolbarItem(
                selected = selectionInfo.isItalic,
                shape = RoundedCornerShape(topEnd = cornerRadius, bottomEnd = cornerRadius),
                onClicked = { onItalicClicked() }
            ) {
                Text(text = italicLabel)
            }
        }
        Row {
            ToolbarItem(
                selected = selectionInfo.isUnorderedList,
                shape = RoundedCornerShape(topStart = cornerRadius, bottomStart = cornerRadius),
                onClicked = onUnorderedListClicked
            ) {
                Icon(
                    imageVector = ImageVector.vectorResource(id = R.drawable.unordered_list),
                    contentDescription = stringResource(R.string.toggle_unordered_list)
                )
            }
            ToolbarItem(
                selected = selectionInfo.isOrderedList,
                shape = RoundedCornerShape(topEnd = cornerRadius, bottomEnd = cornerRadius),
                onClicked = onOrderedListClicked
            ) {
                Icon(
                    imageVector = ImageVector.vectorResource(id = R.drawable.ordered_list),
                    contentDescription = stringResource(R.string.toggle_ordered_list)
                )
            }
        }

        Row {
            ToolbarItem(
                selected = false,
                shape = RoundedCornerShape(topStart = cornerRadius, bottomStart = cornerRadius),
                onClicked = { onImageAddClicked() }
            ) {
                Icon(
                    imageVector = ImageVector.vectorResource(id = R.drawable.image),
                    contentDescription = stringResource(R.string.add_image)
                )
            }
            ToolbarItem(
                selected = false,
                shape = RoundedCornerShape(topEnd = cornerRadius, bottomEnd = cornerRadius),
                onClicked = { onTimerAddClicked() }
            ) {
                Icon(
                    imageVector = ImageVector.vectorResource(id = R.drawable.timer),
                    contentDescription = stringResource(R.string.add_timer)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun RichEditToolbarPreview() {
    RichEditToolbar(selectionInfo = SelectionInfo(isH1Selected = true),
        onHeading1Clicked = { },
        onHeading2Clicked = { },
        onBoldClicked = { },
        onItalicClicked = { },
        onUnorderedListClicked = { },
        onOrderedListClicked = { },
        onImageAddClicked = { },
        onTimerAddClicked = { })
}