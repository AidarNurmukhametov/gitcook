package com.aidarn.gitcook.views.edit

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import com.aidarn.gitcook.R
import kotlin.time.Duration


@Composable
fun DurationEditor(time: Duration?, onTimeChanged: (Duration?) -> Unit) {
    val hours = time?.inWholeHours ?: 0
    val minutes = (time?.inWholeMinutes ?: 0) % 60
    val keyboardOptions =
        KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Next)
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(
            dimensionResource(R.dimen.row_spacing)
        )
    ) {
        val outlinedTextModifier = Modifier
            .weight(1f)
            .wrapContentWidth(Alignment.Start)
        OutlinedTextField(
            value = hours.toString(),
            suffix = {
                Text(text = stringResource(id = R.string.hours))
            },
            onValueChange = { strHours ->
                val newHours = strHours.toLongOrNull() ?: 0
                val newDuration = try {
                    Duration.parseIsoString("PT${newHours}H${minutes}M")
                } catch (_: IllegalArgumentException) {
                    null
                }
                onTimeChanged(newDuration)
            },
            keyboardOptions = keyboardOptions,
            modifier = outlinedTextModifier
        )
        OutlinedTextField(
            value = minutes.toString(),
            suffix = {
                Text(text = stringResource(id = R.string.minutes))
            },
            onValueChange = { strMinutes ->
                val newMinutes = strMinutes.toLongOrNull()?.let { it % 60 } ?: 0
                val newDuration = try {
                    Duration.parseIsoString("PT${hours}H${newMinutes}M")
                } catch (_: IllegalArgumentException) {
                    null
                }
                onTimeChanged(newDuration)
            },
            keyboardOptions = keyboardOptions,
            modifier = outlinedTextModifier
        )
    }
}