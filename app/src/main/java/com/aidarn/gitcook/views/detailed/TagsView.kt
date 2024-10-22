package com.aidarn.gitcook.views.detailed

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.aidarn.gitcook.Constants
import com.aidarn.gitcook.R


@Composable
fun TagsView(rawTags: String?, modifier: Modifier = Modifier) {
    if (rawTags == null)
        return

    val tags = rawTags.split(Constants.SEPARATOR).filter { it.isNotBlank() }
    if (tags.isEmpty())
        return

    val stringBuilder = StringBuilder().append(stringResource(id = R.string.tags), ": ")
    for (i in 0 until tags.size - 1) {
        stringBuilder.append(tags[i], ", ")
    }
    stringBuilder.append(tags.last())
    Text(
        text = stringBuilder.toString(),
        modifier = modifier,
        style = MaterialTheme.typography.bodyLarge
    )
}