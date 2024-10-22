package com.aidarn.richedit.edit

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.aidarn.richedit.R
import kotlin.time.Duration


@Composable
fun TimerEdit(duration: Duration, onEditClicked: () -> Unit, onRemoveClicked: () -> Unit) {
    Card {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Spacer(modifier = Modifier.width(8.dp))
            Icon(
                imageVector = ImageVector.vectorResource(id = R.drawable.play),
                contentDescription = stringResource(R.string.start)
            )
            Text(text = stringResource(R.string.timer_description, duration))
            Spacer(modifier = Modifier.weight(1f))
            Row {
                IconButton(onClick = onEditClicked) {
                    Icon(
                        imageVector = Icons.Filled.Edit,
                        contentDescription = stringResource(R.string.edit_timer)
                    )
                }
                IconButton(onClick = onRemoveClicked) {
                    Icon(
                        imageVector = Icons.Filled.Close,
                        contentDescription = stringResource(R.string.remove_timer)
                    )
                }
            }
        }
    }
}
