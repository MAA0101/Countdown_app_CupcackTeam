package com.example.countdown_app_cupcackteam

import android.Manifest
import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.countdown_app_cupcackteam.ui.theme.Countdown_app_CupcackTeamTheme
import android.graphics.BitmapFactory
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

class MainActivity : ComponentActivity() {
    //aaa
    private lateinit var permissionLauncher: ActivityResultLauncher<String>
    private var countdownTimeInMillis: Long = 60000L // 1 minute (60000 milliseconds)
    private var isTimerRunning by mutableStateOf(false)
    private var remainingTime by mutableStateOf(countdownTimeInMillis)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        permissionLauncher = handlePermissionResponse()
        createNotificationChannel()

        setContent {
            Countdown_app_CupcackTeamTheme {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    CountdownUI(
                        timeLeft = remainingTime,
                        isTimerRunning = isTimerRunning,
                        onStartClick = {
                            startCountdown()
                        }
                    )
                }
            }
        }
    }

    private fun handlePermissionResponse(): ActivityResultLauncher<String> {
        return registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                sendNotification("Notification permission granted!") // Notify when permission is granted
            } else {
                // Show a dialog or handle the rejection case
            }
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(
                "countdown_channel", // Channel ID
                "General Notifications", // Channel name
                importance // Channel importance
            ).apply {
                description = "Displays general notifications for the countdown" // Channel description
            }
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }

    private fun checkAndRequestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
        } else {
            sendNotification("Notification permission not required on this version.") // Notify for older versions
        }
    }

    private fun startCountdown() {
        isTimerRunning = true
        object : CountDownTimer(countdownTimeInMillis, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                remainingTime = millisUntilFinished
            }

            override fun onFinish() {
                isTimerRunning = false
                remainingTime = 0
                sendNotification("Countdown finished!") // Notify when the countdown finishes
            }
        }.start()
    }

    private fun sendNotification(message: String) {
        val bitmap = BitmapFactory.decodeResource(resources, R.drawable.compose_quote)
        val soundUri: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        val notification = NotificationCompat.Builder(this, "countdown_channel")
            .setSmallIcon(R.drawable.ic_alarm)
            .setContentTitle("Countdown Complete!")
            .setContentText(message)
            .setAutoCancel(true)
            .setSound(soundUri) // Add sound to the notification
            .setStyle(NotificationCompat.BigPictureStyle().bigPicture(bitmap))
            .build()

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        NotificationManagerCompat.from(this).notify(99, notification)
    }

    @Composable
    fun CountdownUI(
        timeLeft: Long,
        isTimerRunning: Boolean,
        onStartClick: () -> Unit
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(16.dp)
        ) {
            Text(text = "Time left: ${timeLeft / 1000} seconds")

            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = {
                if (!isTimerRunning) {
                    onStartClick()
                }
            }) {
                Text(text = if (isTimerRunning) "Running..." else "Start Countdown")
            }

            // Input fields for events
            EventInput { eventName, eventTime ->
                scheduleNotification(eventName, eventTime) // Schedule notification for the event
            }
        }
    }

    // Input UI for events
    @Composable
    fun EventInput(onEventAdded: (String, Long) -> Unit) {
        var eventName by remember { mutableStateOf("") }
        var eventTime by remember { mutableStateOf("") }

        Column {
            TextField(
                value = eventName,
                onValueChange = { eventName = it },
                label = { Text("Event Name") }
            )
            TextField(
                value = eventTime,
                onValueChange = { eventTime = it },
                label = { Text("Event Time (in milliseconds)") } // Consider using DatePicker for better input
            )
            Button(onClick = {
                val timeInMillis = eventTime.toLong()
                val currentTime = System.currentTimeMillis()
                if (timeInMillis - currentTime < 3600000) {
                    // Show a message to alert the user that the event is less than an hour away
                } else {
                    onEventAdded(eventName, timeInMillis) // Add the event
                }
            }) {
                Text("Add Event")
            }
        }
    }

    // Schedule notification for the event
    private fun scheduleNotification(eventName: String, eventTime: Long) {
        val intent = Intent(this, NotificationReceiver::class.java).apply {
            putExtra("event_name", eventName) // Pass the event name to the receiver
        }
        val pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)

        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, eventTime - 3600000, pendingIntent) // Schedule one hour before the event
    }
}

// BroadcastReceiver to handle notification for events
class NotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val eventName = intent.getStringExtra("event_name") ?: "Event" // Default event name
        val notificationMessage = "$eventName is happening soon!" // Message for the notification
        val soundUri: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        val notification = NotificationCompat.Builder(context, "countdown_channel")
            .setSmallIcon(R.drawable.ic_alarm)
            .setContentTitle("Upcoming Event!")
            .setContentText(notificationMessage)
            .setAutoCancel(true)
            .setSound(soundUri) // Add sound to the notification
            .build()

        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        NotificationManagerCompat.from(context).notify(100, notification) // Notify for the event
    }
}