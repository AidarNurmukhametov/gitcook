package com.aidarn.gitcook.views.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.DrawerState
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import com.aidarn.gitcook.Constants
import com.aidarn.gitcook.R
import com.aidarn.gitcook.data.Recipe
import com.aidarn.gitcook.viewmodels.HomeScreenUiState

@Composable
fun AppDrawer(
    drawerState: DrawerState,
    recipes: List<Recipe>,
    selectedRecipes: List<Recipe>,
    favoriteRecipes: List<Recipe>,
    queryResult: List<Recipe>,
    tagList: List<String>,
    navigateToDetails: (Int) -> Unit,
    mainScreenUiState: HomeScreenUiState,
    onSelectionChanged: (List<Recipe>) -> Unit,
    onAllSelectedPressed: (Boolean) -> Unit,
    onQueryChange: (String) -> Unit,
    onFilterTagChange: (List<String>) -> Unit,
    modifier: Modifier = Modifier
) {
    val repository = stringResource(R.string.repository)
    val textLinkStyles = TextLinkStyles(
        SpanStyle(
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.SemiBold,
            textDecoration = TextDecoration.Underline
        )
    )
    val link = buildAnnotatedString {
        pushLink(LinkAnnotation.Url(Constants.REPOSITORY_URL, textLinkStyles))
        append(repository)
        pop()
    }
    ModalNavigationDrawer(
        drawerState = drawerState,
        modifier = modifier,
        drawerContent = {
            ModalDrawerSheet {
                if (favoriteRecipes.isNotEmpty()) {
                    Text(text = stringResource(R.string.favorites))
                    LazyColumn {
                        items(favoriteRecipes) { recipe ->
                            RowRecipeItem(recipe, navigateToDetails)
                        }
                    }
                    Spacer(Modifier.height(dimensionResource(R.dimen.column_spacing)))
                }
                Row(
                    horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.row_spacing)),
                    modifier = Modifier.padding(
                        dimensionResource(R.dimen.small_padding)
                    )
                ) {
                    Icon(
                        imageVector = ImageVector.vectorResource(R.drawable.ic_github),
                        contentDescription = null,
                        modifier = Modifier.size(dimensionResource(R.dimen.github_icon))
                    )
                    Text(link)
                }
            }
        },
    ) {
        Column(
            modifier = Modifier.padding(horizontal = dimensionResource(R.dimen.page_padding)),
            verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.small_padding))
        ) {
            when (mainScreenUiState) {
                is HomeScreenUiState.Loading -> Text(text = stringResource(id = R.string.loading_message))
                is HomeScreenUiState.Error -> Text(
                    text = stringResource(
                        id = R.string.error_message,
                        mainScreenUiState.errorMessage
                            ?: stringResource(R.string.unknown_error)
                    )
                )

                else -> RecipesList(
                    recipes = recipes,
                    navigateToDetails = navigateToDetails,
                    selectedRecipes = selectedRecipes,
                    onSelectionChanged = onSelectionChanged,
                    onAllSelectedPressed = onAllSelectedPressed,
                    queryResult = queryResult,
                    onQueryChange = onQueryChange,
                    tagList = tagList,
                    onFilterTagChange = onFilterTagChange
                )
            }
        }
    }
}
