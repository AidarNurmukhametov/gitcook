package com.aidarn.richedit.edit

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import coil.compose.AsyncImage
import coil.imageLoader
import com.aidarn.richedit.R
import com.aidarn.richedit.data.RenderElement


@Composable
fun ImageEdit(
    element: RenderElement.Image, onEditClicked: () -> Unit, onRemoveClicked: () -> Unit
) {
    val fallbackPainter =
        rememberVectorPainter(image = ImageVector.vectorResource(id = R.drawable.image))
    Card {
        Column {
            AsyncImage(
                model = element.content,
                contentDescription = element.placeholder,
                imageLoader = LocalContext.current.imageLoader,
                fallback = fallbackPainter
            )
            Row {
                IconButton(onClick = onEditClicked) {
                    Icon(
                        imageVector = Icons.Filled.Edit,
                        contentDescription = stringResource(R.string.edit_image)
                    )
                }
                IconButton(onClick = onRemoveClicked) {
                    Icon(
                        imageVector = Icons.Filled.Close,
                        contentDescription = stringResource(R.string.remove_image)
                    )
                }
            }
        }
    }
}
