package com.example.countdown_app_cupcackteam

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.Worker
import androidx.work.WorkerParameters

class CountdownWorker(appContext: Context, workerParams: WorkerParameters) : Worker(appContext, workerParams) {


    override  fun doWork(): Result {
        println("lugi mansion3")
        val eventId = inputData.getInt("event_id", 0)
        val eventTime = inputData.getLong("event_time", System.currentTimeMillis())
        val eventText = inputData.getString("event_text")?:"Event"
        val eventTitle = inputData.getString("event_title")?:"Event"



        sendNotification(eventId, eventText, eventTitle)
        createNotificationChannel(applicationContext)


        // Continue running the worker
        return Result.success()
    }

    private fun sendNotification(eventId: Int,eventTitle:String,eventText:String ) {
        val intent = Intent(applicationContext, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }


        val pendingIntent: PendingIntent = PendingIntent.getActivity(
            applicationContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notification = NotificationCompat.Builder(applicationContext, "event_channel")
            .setSmallIcon(R.drawable.baseline_notifications_active_24)
            .setContentTitle(eventTitle)
            .setContentText(eventText)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()


        notificationManager.notify(eventId, notification)
    }
}
private fun createNotificationChannel(context: Context) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val name = "Event Countdown"
        val descriptionText = "Notifications for event countdowns"
        val importance = NotificationManager.IMPORTANCE_HIGH
        val channel = NotificationChannel("event_channel", name, importance).apply {
            description = descriptionText
        }

        val notificationManager: NotificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }
}





