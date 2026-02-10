package com.fishjorunal.sofircl.lpnyht.presentation.notificiation

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.os.bundleOf
import com.fishjorunal.sofircl.FrostJournalActivity
import com.fishjorunal.sofircl.R
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

private const val FROST_JOURNAL_CHANNEL_ID = "frost_journal_notifications"
private const val FROST_JOURNAL_CHANNEL_NAME = "FrostJournal Notifications"
private const val FROST_JOURNAL_NOT_TAG = "FrostJournal"

class FrostJournalPushService : FirebaseMessagingService(){
    override fun onNewToken(token: String) {
        super.onNewToken(token)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        // Обработка notification payload
        remoteMessage.notification?.let {
            if (remoteMessage.data.contains("url")) {
                frostJournalShowNotification(it.title ?: FROST_JOURNAL_NOT_TAG, it.body ?: "", data = remoteMessage.data["url"])
            } else {
                frostJournalShowNotification(it.title ?: FROST_JOURNAL_NOT_TAG, it.body ?: "", data = null)
            }
        }

    }

    private fun frostJournalShowNotification(title: String, message: String, data: String?) {
        val frostJournalNotificationManager =
            getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        // Создаем канал уведомлений для Android 8+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                FROST_JOURNAL_CHANNEL_ID,
                FROST_JOURNAL_CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            )
            frostJournalNotificationManager.createNotificationChannel(channel)
        }

        val frostJournalIntent = Intent(this, FrostJournalActivity::class.java).apply {
            putExtras(bundleOf(
                "url" to data
            ))
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        val frostJournalPendingIntent = PendingIntent.getActivity(
            this,
            0,
            frostJournalIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val frostJournalNotification = NotificationCompat.Builder(this, FROST_JOURNAL_CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(message)
            .setSmallIcon(R.drawable.frots_journal_ic_noti)
            .setAutoCancel(true)
            .setContentIntent(frostJournalPendingIntent)
            .build()

        frostJournalNotificationManager.notify(System.currentTimeMillis().toInt(), frostJournalNotification)
    }

}