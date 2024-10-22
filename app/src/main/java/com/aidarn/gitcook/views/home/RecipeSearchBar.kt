package com.aidarn.gitcook.views.home

import android.app.Activity
import android.content.Intent
import android.speech.RecognizerIntent
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.aidarn.gitcook.R
import com.aidarn.gitcook.data.Recipe
import java.util.Locale


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipeSearchBar(
    queryResult: List<Recipe>,
    onQueryChange: (String) -> Unit,
    navigateTo: (Int) -> Unit
) {
    var text by rememberSaveable { mutableStateOf("") }
    var active by rememberSaveable { mutableStateOf(false) }
    val launcher =
        rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                val data = it.data
                val result = data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                result?.get(0)?.let { speech ->
                    onQueryChange(speech)
                    text = speech
                }
            }
        }
    val onActiveChange = { it: Boolean -> active = it }
    val colors = SearchBarDefaults.colors()
    SearchBar(
        inputField = {
            SearchBarDefaults.InputField(
                query = text,
                onQueryChange = { text = it; onQueryChange(it) },
                onSearch = onQueryChange,
                expanded = active,
                onExpandedChange = onActiveChange,
                enabled = true,
                placeholder = { Text(stringResource(R.string.search)) },
                leadingIcon = {
                    if (!active) {
                        Icon(Icons.Default.Search, stringResource(R.string.search))
                    } else {
                        IconButton(onClick = { active = false }) {
                            Icon(
                                Icons.AutoMirrored.Default.ArrowBack,
                                stringResource(R.string.search)
                            )
                        }
                    }
                },
                trailingIcon = {
                    IconButton(onClick = {
                        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
                        intent.putExtra(
                            RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
                        )
                        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
                        launcher.launch(intent)
                    }) {
                        Icon(
                            ImageVector.vectorResource(R.drawable.mic),
                            contentDescription = stringResource(R.string.voice_search)
                        )
                    }
                },
                interactionSource = null,
            )
        },
        expanded = active,
        onExpandedChange = onActiveChange,
        modifier = Modifier.fillMaxWidth(),
        shape = SearchBarDefaults.inputFieldShape,
        colors = colors,
        tonalElevation = SearchBarDefaults.TonalElevation,
        shadowElevation = SearchBarDefaults.ShadowElevation,
        windowInsets = WindowInsets(0),
        content = {
            if (queryResult.isNotEmpty()) {
                LazyColumn {
                    items(queryResult, key = { it.id }) {
                        RowRecipeItem(it, navigateTo)
                    }
                }
            } else {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = ImageVector.vectorResource(R.drawable.no_meals),
                        contentDescription = null,
                        modifier = Modifier.size(dimensionResource(R.dimen.no_recipes_icon_size))
                    )
                    Text(stringResource(R.string.no_matches))
                }
            }
        },
    )
    BackHandler(enabled = active) {
        active = false
    }
}

@Composable
@Preview(showBackground = true)
fun RecipeSearchBarPreview() {
    var query by remember { mutableStateOf("search") }
    Column(modifier = Modifier.padding(16.dp)) {
        RecipeSearchBar(
            queryResult = emptyList(),
            onQueryChange = { query = it },
            navigateTo = {}
        )
    }
}
