package com.aidarn.gitcook.views.edit

import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.layout.MeasurePolicy
import androidx.compose.ui.layout.MeasureScope
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import com.aidarn.gitcook.R
import com.aidarn.gitcook.data.Recipe
import com.aidarn.gitcook.views.RecipePhoto
import kotlin.math.max


@Composable
internal fun ImageBox(
    imageModel: Any,
    onImageUriChanged: (Any) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val activityResultLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            val flag = Intent.FLAG_GRANT_READ_URI_PERMISSION
            val res =
                uri?.let { context.contentResolver.takePersistableUriPermission(uri, flag); uri }
                    ?: Recipe.Default.imageModel
            onImageUriChanged(res)
        }
    InverseColumn(
        modifier = modifier,
        {
            RecipePhoto(imageModel, stringResource(R.string.photo_of_dish))
            Row(
                horizontalArrangement = Arrangement.spacedBy(
                    dimensionResource(R.dimen.row_spacing), Alignment.End
                ), modifier = Modifier.fillMaxWidth()
            ) {
                IconButton(onClick = {
                    activityResultLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                }) {
                    Icon(
                        imageVector = Icons.Filled.Edit,
                        contentDescription = stringResource(R.string.edit_image)
                    )
                }
                IconButton(onClick = { onImageUriChanged(Recipe.Default.imageModel) }) {
                    Icon(
                        imageVector = Icons.Filled.Close,
                        contentDescription = stringResource(R.string.remove_image)
                    )
                }
            }
        },
    )
}

@Composable
private fun InverseColumn(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
    verticalSpacer: Dp = dimensionResource(R.dimen.small_padding),
) {
    Layout(
        content = content,
        modifier = modifier,
        measurePolicy = inverseColumnMeasurePolicy(verticalSpacer)
    )
}

@Composable
private fun inverseColumnMeasurePolicy(
    verticalSpacer: Dp
) = remember {
    MeasurePolicy { measurables, constraints ->
        val positions = reversedColumnRelativePositions(constraints, measurables, verticalSpacer)
        val width = maxOf(positions.maxOf { it.maxXCoordinate }, constraints.minWidth)
        val height = minOf(
            maxOf(positions.maxOf { it.maxYCoordinate }, constraints.minHeight),
            constraints.maxHeight
        )
        layout(width, height) {
            for ((placeable, dx, dy) in positions) {
                placeable.placeRelative(dx, dy)
            }
        }
    }
}

private fun MeasureScope.reversedColumnRelativePositions(
    constraints: Constraints, measurables: List<Measurable>, verticalSpacer: Dp
): List<PlaceableRelativePosition> {
    val res = mutableListOf<PlaceableRelativePosition>()
    val x = 0
    var y = constraints.maxHeight
    var localConstraints = constraints

    for (measurable in measurables.reversed()) {
        val placeable = measurable.measure(localConstraints)
        y -= placeable.height
        res += PlaceableRelativePosition(placeable, x, y)
        y -= verticalSpacer.roundToPx()
        val newMaxHeight =
            max(localConstraints.maxHeight - placeable.height, localConstraints.minHeight)
        localConstraints = localConstraints.copy(maxHeight = newMaxHeight)
    }

    return res
}

private data class PlaceableRelativePosition(val placeable: Placeable, val dx: Int, val dy: Int)

private val PlaceableRelativePosition.maxXCoordinate: Int
    get() = dx + placeable.width

private val PlaceableRelativePosition.maxYCoordinate: Int
    get() = dy + placeable.height

@Preview(showBackground = true)
@Composable
fun ImageBoxPreview() {
    ImageBox(
        imageModel = "",
        onImageUriChanged = { },
    )
}