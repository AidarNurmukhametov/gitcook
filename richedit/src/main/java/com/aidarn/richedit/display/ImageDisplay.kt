package com.aidarn.richedit.display

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import coil.compose.AsyncImage
import coil.imageLoader
import com.aidarn.richedit.R
import com.aidarn.richedit.data.RenderElement


@Composable
internal fun ImageDisplay(item: RenderElement.Image, modifier: Modifier = Modifier) {
    val fallbackPainter =
        rememberVectorPainter(image = ImageVector.vectorResource(id = R.drawable.image))
    AsyncImage(
        model = item.content,
        contentDescription = item.placeholder,
        imageLoader = LocalContext.current.imageLoader,
        fallback = fallbackPainter,
        modifier = modifier,
    )
}