package com.aidarn.richedit.display

import android.Manifest.permission.POST_NOTIFICATIONS
import android.content.pm.PackageManager.PERMISSION_DENIED
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.os.Build
import android.os.CountDownTimer
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.aidarn.richedit.NotificationService
import com.aidarn.richedit.R
import com.aidarn.richedit.data.RenderElement
import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.toDuration


@Composable
internal fun TimerDisplay(item: RenderElement.Timer, modifier: Modifier = Modifier) {
    var notificationsGranted by remember { mutableStateOf<Boolean?>(null) }
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) {
        notificationsGranted = it
    }
    var isRunning by remember { mutableStateOf(false) }
    var isPaused by remember { mutableStateOf(false) }
    var timeElapsed by remember { mutableLongStateOf(0L) }
    var notificationId by remember { mutableIntStateOf(0) }
    var countDownTimer: CountDownTimer? by remember { mutableStateOf(null) }
    val context = LocalContext.current
    val elapsed = timeElapsed.toFloat()
    val total = item.content.inWholeMilliseconds.toFloat()
    val (elapsedString, leftString) = getElapsedLeftString(item.content, timeElapsed)
    fun startTimer() {
        if (isRunning) return
        if (timeElapsed == item.content.inWholeMilliseconds) {
            timeElapsed = 0
        }
        if (notificationsGranted == true) {
            val res = NotificationService.setAlarm(
                context,
                (item.content.inWholeMilliseconds - timeElapsed).toDuration(DurationUnit.MILLISECONDS)
            )
            notificationId = res
        }
        countDownTimer =
            object : CountDownTimer(item.content.inWholeMilliseconds - timeElapsed, 1000) {
                override fun onTick(millisUntilFinished: Long) {
                    timeElapsed = item.content.inWholeMilliseconds - millisUntilFinished
                }

                override fun onFinish() {
                    isRunning = false
                    timeElapsed = item.content.inWholeMilliseconds
                }
            }.start()
        isRunning = true
    }

    fun pauseTimer() {
        countDownTimer?.cancel()
        NotificationService.cancelAlarm(context, notificationId)
        notificationId = 0
        isRunning = false
        isPaused = true
    }

    fun resumeTimer() {
        startTimer()
        isPaused = false
    }

    fun resetTimer() {
        countDownTimer?.cancel()
        NotificationService.cancelAlarm(context, notificationId)
        notificationId = 0
        isRunning = false
        isPaused = false
        timeElapsed = 0
    }

    LaunchedEffect(notificationsGranted) {
        if (notificationsGranted == null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                when (context.checkSelfPermission(POST_NOTIFICATIONS)) {
                    PERMISSION_GRANTED -> {
                        notificationsGranted = true
                    }

                    PERMISSION_DENIED -> {
                        launcher.launch(POST_NOTIFICATIONS)
                    }
                }
            }
        }
    }
    Surface(modifier = modifier, shape = CardDefaults.shape) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            IconButton(onClick = {
                val newValue = !isRunning
                if (newValue) {
                    resumeTimer()
                } else {
                    pauseTimer()
                }
                isRunning = newValue
            }) {
                if (isRunning) {
                    Icon(
                        imageVector = ImageVector.vectorResource(id = R.drawable.pause),
                        contentDescription = stringResource(R.string.pause)
                    )
                } else {
                    Icon(
                        imageVector = ImageVector.vectorResource(id = R.drawable.play),
                        contentDescription = stringResource(R.string.start)
                    )
                }
            }
            Text(text = elapsedString)
            Slider(
                value = elapsed / total,
                onValueChange = { },
                modifier = Modifier.weight(1f)
            )
            Text(text = leftString)
            IconButton(onClick = { resetTimer() }) {
                Icon(
                    imageVector = Icons.Filled.Refresh,
                    contentDescription = stringResource(R.string.reset)
                )
            }
        }
    }
    DisposableEffect(Unit) {
        onDispose {
            if (notificationId != 0) {
                NotificationService.cancelAlarm(context, notificationId)
            }
        }
    }
}

private fun getElapsedLeftString(total: Duration, elapsedMilliseconds: Long): Pair<String, String> {
    val elapsedDuration = elapsedMilliseconds.toDuration(DurationUnit.MILLISECONDS)
    val elapsed = elapsedDuration.toFormatedString()
    val leftDuration = total - elapsedDuration
    val left = leftDuration.toFormatedString()

    return elapsed to left
}

private fun Duration.toFormatedString(): String {
    return toComponents { hours, minutes, seconds, _ ->
        if (hours == 0L) {
            String.format(null, "%d:%02d", minutes, seconds)
        } else {
            String.format(null, "%d:%02d:%02d", hours, minutes, seconds)
        }
    }
}

@Composable
@Preview(showBackground = true)
internal fun TimerDisplayPreview() {
    val item = RenderElement.Timer(7200.0.toDuration(DurationUnit.SECONDS))
    Card {
        Text("Sample text", modifier = Modifier.padding(16.dp))
        TimerDisplay(item)
    }
}