package com.aidarn.richedit.dialog

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Card
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.aidarn.richedit.R
import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.toDuration


@Composable
fun TimerDialog(
    onDismissRequest: () -> Unit,
    onConfirmation: (Duration) -> Unit,
) {
    var duration by remember { mutableStateOf(10.toDuration(DurationUnit.MINUTES)) }
    val (hours, minutes, seconds) = duration.toComponents { hours, minutes, seconds, _ ->
        Triple(hours, minutes, seconds)
    }

    Dialog(onDismissRequest = { onDismissRequest() }) {
        Card(
            modifier = Modifier
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
        ) {
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = stringResource(R.string.set_duration),
                    modifier = Modifier.padding(16.dp),
                )
                Row(
                    modifier = Modifier.padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    OutlinedTextField(
                        value = hours.blankIfZero(),
                        placeholder = { Text("0") },
                        onValueChange = {
                            duration = (it.toLongOrNull()
                                ?: 0).toDuration(DurationUnit.HOURS) + minutes.toDuration(
                                DurationUnit.MINUTES
                            ) + seconds.toDuration(DurationUnit.SECONDS)
                        },
                        suffix = { Text(text = stringResource(R.string.hours_short)) },
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number, imeAction = ImeAction.Next
                        )
                    )
                    OutlinedTextField(
                        value = minutes.blankIfZero(),
                        placeholder = { Text("0") },
                        onValueChange = {
                            duration = ((it.toIntOrNull()
                                ?: 0) % 60).toDuration(DurationUnit.MINUTES) + hours.toDuration(
                                DurationUnit.HOURS
                            ) + seconds.toDuration(DurationUnit.SECONDS)
                        },
                        suffix = { Text(text = stringResource(R.string.minutes_short)) },
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number, imeAction = ImeAction.Next
                        )
                    )
                    OutlinedTextField(
                        value = seconds.blankIfZero(),
                        placeholder = { Text("0") },
                        onValueChange = {
                            duration = ((it.toIntOrNull()
                                ?: 0) % 60).toDuration(DurationUnit.SECONDS) + minutes.toDuration(
                                DurationUnit.MINUTES
                            ) + hours.toDuration(DurationUnit.HOURS)
                        },
                        suffix = { Text(text = stringResource(R.string.seconds_short)) },
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number, imeAction = ImeAction.Done
                        )
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                ) {
                    TextButton(
                        onClick = { onDismissRequest() },
                        modifier = Modifier.padding(8.dp),
                    ) {
                        Text(stringResource(R.string.dismiss))
                    }
                    TextButton(
                        onClick = { onConfirmation(duration) },
                        modifier = Modifier.padding(8.dp),
                    ) {
                        Text(stringResource(R.string.confirm))
                    }
                }
            }
        }
    }
}

private fun Number.blankIfZero(): String {
    if (this == 0)
        return ""
    return this.toString()
}
