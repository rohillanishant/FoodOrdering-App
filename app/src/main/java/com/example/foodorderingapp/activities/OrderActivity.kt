package com.example.foodorderingapp.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.example.foodorderingapp.R
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
// Notification channel ID
private const val CHANNEL_ID = "my_channel_id"

// Notification ID
private const val NOTIFICATION_ID = 1

// Notification channel name
private const val CHANNEL_NAME = "My Channel"
class OrderActivity : AppCompatActivity() {
    lateinit var btnOk:Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order)
        btnOk=findViewById(R.id.btnOk)
        btnOk.setOnClickListener {
            val intent= Intent(this@OrderActivity,HomeActivity::class.java)
            showNotification(this, "Order Placed Successfully", "Thanks for Ordering with Food Runner!!")
            startActivity(intent)
        }
    }



    fun showNotification(context: Context, title: String, message: String) {
        // Create notification manager
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Create a notification channel for Android 8.0 and higher
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel =
                NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT)
            notificationManager.createNotificationChannel(channel)
        }

        // Create a notification
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_logo) // Replace with your own notification icon
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()

        // Display the notification
        notificationManager.notify(NOTIFICATION_ID, notification)
    }
}