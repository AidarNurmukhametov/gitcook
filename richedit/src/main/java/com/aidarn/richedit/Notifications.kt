package com.aidarn.richedit

import android.Manifest.permission.POST_NOTIFICATIONS
import android.app.AlarmManager
import android.app.AlarmManager.OnAlarmListener
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.os.Build
import android.os.Handler
import android.os.Looper
import androidx.annotation.DrawableRes
import androidx.core.app.NotificationCompat
import com.aidarn.richedit.AlarmReceiver.Companion.notificationIcon
import com.aidarn.richedit.AlarmReceiver.Companion.notificationId
import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.toDuration


private const val ALARMS_TIMERS_CHANNEL = "alarms_timers_channel"

object NotificationService {
    private val pendingListeners = HashMap<Int, ICancellable>()
    private var listenerId = 0
    fun createNotificationChannel(context: Context) {
        val channelId = ALARMS_TIMERS_CHANNEL
        val channelName = context.resources.getString(R.string.channel_name)
        val importance = NotificationManager.IMPORTANCE_HIGH
        val channel = NotificationChannel(channelId, channelName, importance).apply {
            description = context.resources.getString(R.string.channel_description)
        }

        val notificationManager: NotificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    fun setNotificationIcon(@DrawableRes iconId: Int) {
        notificationIcon = iconId
    }

    fun setAlarm(context: Context, duration: Duration): Int {
        val canSendNotification = when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> context.checkSelfPermission(
                POST_NOTIFICATIONS
            ) == PERMISSION_GRANTED

            else -> (context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager).areNotificationsEnabled()
        }
        if (!canSendNotification) return 0

        val res: Int = ++listenerId
        val cancellable = if (duration > 5.0.toDuration(DurationUnit.SECONDS)) {
            AlarmManagerSender(context, duration)
        } else {
            HandlerSender(context, duration)
        }
        pendingListeners[res] = cancellable

        return res
    }

    fun cancelAlarm(context: Context, id: Int) {
        val alarmReceiver = pendingListeners[id] ?: return
        alarmReceiver.cancel(context)
        pendingListeners.remove(id)
    }
}

private interface ICancellable {
    fun cancel(context: Context)
}

private class AlarmManagerSender(context: Context, duration: Duration) : ICancellable {
    companion object {
        private var nextAlarmId = 0
    }

    private val alarmListener: OnAlarmListener
    private val id = ++nextAlarmId

    init {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, AlarmReceiver::class.java)

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            id,
            intent,
            PendingIntent.FLAG_IMMUTABLE
        )

        alarmListener = OnAlarmListener { pendingIntent.send() }

        alarmManager.set(
            AlarmManager.RTC_WAKEUP,
            System.currentTimeMillis() + duration.inWholeMilliseconds,
            null,
            alarmListener,
            null
        )
    }

    override fun cancel(context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            id,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_NO_CREATE
        )

        pendingIntent.cancel()
        alarmManager.cancel(alarmListener)
    }
}

private class HandlerSender(context: Context, duration: Duration) : ICancellable {
    private val handler: Handler = Handler(Looper.getMainLooper())
    private val runnable: Runnable = kotlinx.coroutines.Runnable {
        sendNotification(context)
    }

    init {
        handler.postDelayed(runnable, duration.inWholeMilliseconds)
    }

    override fun cancel(context: Context) {
        handler.removeCallbacks(runnable)
    }
}

class AlarmReceiver : BroadcastReceiver() {
    companion object {
        @DrawableRes
        internal var notificationIcon: Int = R.drawable.timer
        internal var notificationId: Int = 1
    }

    override fun onReceive(context: Context, intent: Intent?) {
        showNotification(context)
    }

    private fun showNotification(context: Context) {
        sendNotification(context)
    }
}

private fun sendNotification(context: Context) {
    val channelId = ALARMS_TIMERS_CHANNEL

    val notificationBuilder = NotificationCompat.Builder(context, channelId)
        .setSmallIcon(notificationIcon)
        .setContentTitle(context.getString(R.string.timer))
        .setContentText(context.getString(R.string.timer_expired))
        .setPriority(NotificationCompat.PRIORITY_HIGH)
        .setOnlyAlertOnce(false)
        .setAutoCancel(true)

    val notificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    notificationManager.notify(notificationId++, notificationBuilder.build())
}
