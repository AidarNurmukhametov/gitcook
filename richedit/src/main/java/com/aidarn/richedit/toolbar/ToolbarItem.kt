package com.aidarn.richedit.toolbar

import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.IconToggleButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics

@Composable
internal fun ToolbarItem(
    selected: Boolean,
    shape: Shape,
    onClicked: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    val colors = CardDefaults.cardColors().copy(
        containerColor = if (selected) MaterialTheme.colorScheme.secondaryContainer else Color.Transparent
    )
    Card(
        shape = shape,
        border = FilterChipDefaults.filterChipBorder(enabled = true, selected = false),
        colors = colors,
        modifier = modifier
    ) {
        IconToggleButton(checked = selected,
            onCheckedChange = onClicked,
            modifier = Modifier.semantics {
                this.selected = selected
            }) {
            content()
        }
    }
}