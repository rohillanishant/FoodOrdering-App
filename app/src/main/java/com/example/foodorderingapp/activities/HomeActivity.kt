package com.example.foodorderingapp.activities

import android.app.AlertDialog
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.FrameLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.app.NotificationCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.example.foodorderingapp.R
import com.example.foodorderingapp.fragments.*
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase

// Notification channel ID
private const val CHANNEL_ID = "my_channel_id"

// Notification ID
private const val NOTIFICATION_ID = 1

// Notification channel name
private const val CHANNEL_NAME = "My Channel"

class HomeActivity : AppCompatActivity() {
    lateinit var sharedPreferences: SharedPreferences
    lateinit var drawerLayout: DrawerLayout
    lateinit var coordinatorLayout: CoordinatorLayout
    lateinit var toolbar:androidx.appcompat.widget.Toolbar
    lateinit var frameLayout: FrameLayout
    lateinit var navigationView: NavigationView
    lateinit var txtUserName: TextView
    lateinit var txtContactDetails:TextView
    var previousmenuItem:MenuItem?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        drawerLayout=findViewById(R.id.drawerLayout)
        coordinatorLayout=findViewById(R.id.coordinatorLayout)
        toolbar=findViewById(R.id.toolbar)
        frameLayout=findViewById(R.id.frameLayout)
        navigationView=findViewById(R.id.navigationView)
        val headerView = navigationView.getHeaderView(0)
        txtUserName = headerView.findViewById(R.id.txtUserName)
        txtContactDetails = headerView.findViewById(R.id.txtContactDetails)

        sharedPreferences=getSharedPreferences(getString(R.string.preference_file_name), Context.MODE_PRIVATE)

        val userRef= FirebaseDatabase.getInstance().getReference("user").child(Firebase.auth.currentUser!!.uid)
        userRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val name = snapshot.child("name").getValue()
                val mobileNumber=snapshot.child("mobileNumber").getValue()
                txtUserName.text=name.toString()
                txtContactDetails.text=mobileNumber.toString()
                if(sharedPreferences.getBoolean("justLoggedIn",false)){
                    sharedPreferences.edit().putBoolean("justLoggedIn",false).apply()
                    showNotification(this@HomeActivity, "Welcome Back , ${name.toString()}", "We are thrilled to have you back!!😋😀")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("Firebase", "Error reading data: ${error.message}")
            }
        })

        headerView.setOnClickListener {
            supportFragmentManager.beginTransaction().replace(R.id.frameLayout, ProfileFragment()).commit()
            supportActionBar?.title="My Profile"
            drawerLayout.closeDrawers()
            navigationView.setCheckedItem(R.id.myProfile)
        }
        setupToolbar()

        openHome()
        val actionBarDrawerToggle=ActionBarDrawerToggle(
            this@HomeActivity,
            drawerLayout,
            R.string.open_drawer,
            R.string.close_drawer
        )
        drawerLayout.addDrawerListener(actionBarDrawerToggle)
        actionBarDrawerToggle.syncState()

        navigationView.setNavigationItemSelectedListener {
            if(previousmenuItem!=null){
                previousmenuItem?.isChecked=false
            }
            it.isCheckable=true
            it.isChecked=true
            previousmenuItem=it

            when(it.itemId){
                R.id.home ->{
                    openHome()
                }
                R.id.myProfile ->{
                    supportFragmentManager.beginTransaction().replace(R.id.frameLayout, ProfileFragment()).commit()
                    Toast.makeText(this@HomeActivity,"My Profile",Toast.LENGTH_SHORT).show()
                    drawerLayout.closeDrawers()
                    supportActionBar?.title="My profile"
                }
                R.id.favourites ->{
                    supportFragmentManager.beginTransaction().replace(R.id.frameLayout, FavouritesFragment()).commit()
                    Toast.makeText(this@HomeActivity,"Favourite Restaurants",Toast.LENGTH_SHORT).show()
                    drawerLayout.closeDrawers()
                    supportActionBar?.title="Favourite Restaurants"
                }
                R.id.orderHistory ->{
                    val dialog=AlertDialog.Builder(this@HomeActivity)
                    dialog.setTitle("Sort By?")
                    dialog.setPositiveButton("Newest to Oldest") { text,listener->
                        sharedPreferences.edit().putBoolean("newToOld",true).apply()
                        supportFragmentManager.beginTransaction().replace(R.id.frameLayout, OrderHistoryFragment()).commit()
                        Toast.makeText(this@HomeActivity,"Order History",Toast.LENGTH_SHORT).show()
                        drawerLayout.closeDrawers()
                        supportActionBar?.title="Order History"
                    }
                    dialog.setNegativeButton("Oldest to Newest") {text,listener ->
                        sharedPreferences.edit().putBoolean("newToOld",false).apply()
                        supportFragmentManager.beginTransaction().replace(R.id.frameLayout, OrderHistoryFragment()).commit()
                        Toast.makeText(this@HomeActivity,"Order History",Toast.LENGTH_SHORT).show()
                        drawerLayout.closeDrawers()
                        supportActionBar?.title="Order History"
                    }
                    dialog.setIcon(R.drawable.ic_sort2_foreground)
                    dialog.create()
                    dialog.show()
                }
                R.id.settings->{
                    supportFragmentManager.beginTransaction().replace(R.id.frameLayout, SettingsFragment()).commit()
                    Toast.makeText(this@HomeActivity,"Settings",Toast.LENGTH_SHORT).show()
                    drawerLayout.closeDrawers()
                    supportActionBar?.title="Settings"
                }
                R.id.faqs ->{
                    supportFragmentManager.beginTransaction().replace(R.id.frameLayout, FaqFragment()).commit()
                    Toast.makeText(this@HomeActivity,"FAQs",Toast.LENGTH_SHORT).show()
                    drawerLayout.closeDrawers()
                    supportActionBar?.title="FAQs"
                }
                R.id.logOut ->{
                    drawerLayout.closeDrawers()
                    val dialog= AlertDialog.Builder(this@HomeActivity)
                    dialog.setTitle("Log Out?")
                    dialog.setIcon(R.drawable.ic_log_out)
                    dialog.setMessage("Are you sure , you want to log out?")
                    dialog.setPositiveButton("Yes") { text,listener->
                        sharedPreferences.edit().clear().apply()
                        Firebase.auth.signOut()
                        val intent= Intent(this@HomeActivity, LoginActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                    dialog.setNegativeButton("No") {text,listener ->
                    }
                    dialog.create()
                    dialog.show()
                }
            }
            return@setNavigationItemSelectedListener true
        }
    }

    private fun openHome() {
        supportFragmentManager.beginTransaction().replace(R.id.frameLayout, HomeFragment()).commit()
        drawerLayout.closeDrawers()
        supportActionBar?.title="Home"
        Toast.makeText(this@HomeActivity,"Home",Toast.LENGTH_SHORT).show()
    }

    fun setupToolbar() {
        setSupportActionBar(toolbar)
        supportActionBar?.title="Home"
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
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
    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        val frag=supportFragmentManager.findFragmentById(R.id.frameLayout)
        when(frag){
            !is HomeFragment ->openHome()
        }
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id=item.itemId
        if(id==android.R.id.home){
            drawerLayout.openDrawer(GravityCompat.START)
        }
        return super.onOptionsItemSelected(item)
    }
}