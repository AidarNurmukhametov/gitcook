package com.aidarn.gitcook.views.edit

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.InputChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.aidarn.gitcook.Constants
import com.aidarn.gitcook.R


@OptIn(ExperimentalLayoutApi::class)
@Composable
fun TagEditor(rawItems: String?, onItemsChanged: (String) -> Unit) {
    val items = rawItems?.split(Constants.SEPARATOR) ?: emptyList()
    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.row_spacing)),
        verticalArrangement = Arrangement.Center
    ) {
        items.forEach { item ->
            if (item.isNotEmpty()) {
                InputChip(
                    selected = false,
                    onClick = { },
                    label = { Text(text = item.trim()) },
                    trailingIcon = {
                        IconButton(
                            onClick = {
                                val stringBuilder = StringBuilder()
                                items.filter { it != item }
                                    .forEach { stringBuilder.append(it, "${Constants.SEPARATOR}") }
                                if (stringBuilder.lastIndex != -1) {
                                    stringBuilder.deleteCharAt(stringBuilder.lastIndex)
                                }
                                onItemsChanged(stringBuilder.toString())
                            },
                            modifier = Modifier.size(18.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = stringResource(id = R.string.remove)
                            )
                        }
                    }
                )
            }
        }

        NewItemEntryField(stringResource(R.string.tag)) { newItem ->
            val res = if (rawItems != null) {
                StringBuilder().append(rawItems, Constants.SEPARATOR, newItem).toString()
            } else newItem
            onItemsChanged(res)
        }
    }
}
