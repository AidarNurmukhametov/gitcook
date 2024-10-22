package com.aidarn.gitcook.views.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.dimensionResource
import com.aidarn.gitcook.R


@Composable
fun TagList(tagList: List<String>, onFilterTagChange: (List<String>) -> Unit) {
    var selectedTags by remember { mutableStateOf<List<String>>(emptyList()) }
    LazyRow(horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.row_spacing))) {
        items(tagList) { tag ->
            var selected by remember { mutableStateOf(false) }
            FilterChip(
                selected = selected,
                label = { Text(tag) },
                onClick = {
                    val newValue = !selected
                    selected = newValue
                    val newSelectedTags = if (newValue) {
                        selectedTags + tag
                    } else {
                        selectedTags - tag
                    }
                    selectedTags = newSelectedTags
                    onFilterTagChange(newSelectedTags)
                }
            )
        }
    }
}