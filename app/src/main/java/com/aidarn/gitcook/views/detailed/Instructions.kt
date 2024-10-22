package com.aidarn.gitcook.views.detailed

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import com.aidarn.gitcook.R


@Composable
fun Instructions(modifier: Modifier = Modifier, instructions: @Composable () -> Unit) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.column_spacing))
    ) {
        Text(
            text = stringResource(id = R.string.instructions),
            style = MaterialTheme.typography.headlineMedium
        )
        instructions()
    }
}
