package com.example.foodorderingapp.fragments

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.core.app.NotificationCompat
import com.example.foodorderingapp.R
import com.example.foodorderingapp.activities.*
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
// Notification channel ID
private const val CHANNEL_ID = "my_channel_id"

// Notification ID
private const val NOTIFICATION_ID = 1

// Notification channel name
private const val CHANNEL_NAME = "My Channel"
class SettingsFragment : Fragment() {
    lateinit var btnClearHistory:Button
    lateinit var btnDeleteAccount:Button
    lateinit var btnEditAccount:Button
    lateinit var sharedPreferences:SharedPreferences
    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view=inflater.inflate(R.layout.fragment_settings, container, false)
        sharedPreferences = requireContext().getSharedPreferences(getString(R.string.preference_file_name), Context.MODE_PRIVATE)
        btnEditAccount=view.findViewById(R.id.btnEditAccount)
        btnClearHistory=view.findViewById(R.id.btnClearHistory)
        btnDeleteAccount=view.findViewById(R.id.btnDeleteAccount)
        btnEditAccount.setOnClickListener {
            val intent=Intent(context,EditAccountActivity::class.java)
            startActivity(intent)
        }
        btnDeleteAccount.setOnClickListener {
            val dialog= AlertDialog.Builder(context)
            dialog.setTitle("Delete Account?")
            dialog.setMessage("Are you sure , you want to delete your account?")
            dialog.setPositiveButton("Yes") { text,listener->
                val user = Firebase.auth.currentUser!!
                val myRef = FirebaseDatabase.getInstance().getReference("user")
                //Toast.makeText(context,"${user.uid}",Toast.LENGTH_SHORT).show()
                myRef.child(user.uid).removeValue()     //removing from database
                user.delete()           //removing from authentication
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            showNotification(activity as Context, "Your Account Deleted Successfully", "We are sorry to see you go!☹️😥  If you ever decide to come back, we'll be here for you")
                            Toast.makeText(context,"Your account deleted", Toast.LENGTH_SHORT).show()
                        }
                    }
                sharedPreferences.edit().putBoolean("isLoggedIn",false).apply()
                val intent= Intent(context, LoginActivity::class.java)
                startActivity(intent)
            }.setIcon(R.drawable.ic_profile)
            dialog.setNegativeButton("No") {text,listener ->
            }
            dialog.create()
            dialog.show()
        }
        btnClearHistory.setOnClickListener {
            val dialog= AlertDialog.Builder(activity as Context)
            dialog.setTitle("Clear History?")
            dialog.setMessage("History will be cleared permanently")
            dialog.setPositiveButton("Yes") { text,listener->
                val user = Firebase.auth.currentUser!!
                val myRef = FirebaseDatabase.getInstance().getReference("user")
                myRef.child(user.uid).child("orders").removeValue()     //removing from database
            }
            dialog.setNegativeButton("No") {text,listener ->
            }
            dialog.create()
            dialog.show()
        }
        return view
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
            .setSmallIcon(R.mipmap.ic_logo1_round) // Replace with your own notification icon
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()

        // Display the notification
        notificationManager.notify(NOTIFICATION_ID, notification)
    }
}