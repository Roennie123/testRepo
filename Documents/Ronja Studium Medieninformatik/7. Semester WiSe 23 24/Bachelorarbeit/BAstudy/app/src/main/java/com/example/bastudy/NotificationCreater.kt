package com.example.bastudy

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build


object NotificationCreater {

    fun showNotification(context: Context, title: String?, message: String?, link: String) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // create notification channel for Android 8+ (API Level 26)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel("channel_id", "My Notification Name", NotificationManager.IMPORTANCE_DEFAULT)
            notificationManager.createNotificationChannel(channel)
        }

        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(link))
        val pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT + PendingIntent.FLAG_IMMUTABLE)

        // create notification
        val notification: Notification = Notification.Builder(context, "channel_id")
            .setContentTitle(title)
            .setContentText(message)
            .setSmallIcon(R.drawable.ic_main_reduced)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        // show notification
        notificationManager.notify(0, notification)
    }
}