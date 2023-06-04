package com.example.foodorderingapp.activities

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.core.app.NotificationCompat
import com.example.foodorderingapp.R
import com.example.foodorderingapp.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase


// Notification channel ID
private const val CHANNEL_ID = "my_channel_id"

// Notification ID
private const val NOTIFICATION_ID = 1

// Notification channel name
private const val CHANNEL_NAME = "My Channel"
class RegisterActivity : AppCompatActivity() {
    lateinit var etName: EditText
    lateinit var etEmail: EditText
    lateinit var etMobileNumber: EditText
    lateinit var etAddress: EditText
    lateinit var btnRegister: Button
    lateinit var etPassword: EditText
    lateinit var etConfirmPassword: EditText
    lateinit var toolbar: Toolbar
    private lateinit var auth: FirebaseAuth
    private lateinit var dbRef:DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        etName=findViewById(R.id.etName)
        etEmail=findViewById(R.id.etEmail)
        etMobileNumber=findViewById(R.id.etMobileNumber)
        etAddress=findViewById(R.id.etAddress)
        btnRegister=findViewById(R.id.btnRegister)
        etPassword=findViewById(R.id.etPassword)
        etConfirmPassword=findViewById(R.id.etConfirmPassword)
        auth = Firebase.auth

        toolbar=findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setIcon(R.mipmap.ic_logo3)
        supportActionBar?.title="Register"

        btnRegister.setOnClickListener() {
            val pass=etPassword?.text.toString()
            val confirm=etConfirmPassword?.text.toString()
            var capital:Boolean=false
            var small:Boolean=false
            var specialChar : Boolean=false
            var number:Boolean=false

            if(etName.text.toString().isEmpty())
                Toast.makeText(this@RegisterActivity,"Enter Name", Toast.LENGTH_LONG).show()
            else if(etName.length()<4) {
                Toast.makeText(this@RegisterActivity,"Name should contain atleast 3 characters",
                    Toast.LENGTH_LONG).show()
            }
            else if(etEmail.text.toString().isEmpty())
                Toast.makeText(this@RegisterActivity,"Enter Email Id", Toast.LENGTH_LONG).show()
            else if(etMobileNumber.text.toString().isEmpty())
                Toast.makeText(this@RegisterActivity,"Enter Mobile Number", Toast.LENGTH_LONG).show()
            else if(etMobileNumber.length()!=10) {
                Toast.makeText(this@RegisterActivity,"Mobile Number should have 10 digits", Toast.LENGTH_LONG).show()
            }
            else if(etAddress.text.toString().isEmpty())
                Toast.makeText(this@RegisterActivity,"Enter Delivery Address", Toast.LENGTH_LONG).show()
            else if(pass.isEmpty()){
                Toast.makeText(this@RegisterActivity,"Enter Password",Toast.LENGTH_LONG).show()
            }else if(pass!=confirm){
                Toast.makeText(this@RegisterActivity,"Passwords doesn't match. Please enter Same Passwords",Toast.LENGTH_LONG).show()
            }else if (pass.length <8) {
                Toast.makeText(this@RegisterActivity, "Password size should be at least 8", Toast.LENGTH_LONG).show()
            }else {
                for(i in 0..(pass.length-1)){
                    if(pass[i].isUpperCase()){
                        capital=true
                    }
                    if(pass[i].isLowerCase()){
                        small=true
                    }
                    if(pass[i].isDigit()){
                        number=true
                    }
                    if(pass[i]>='!' && pass[i]<='~' && !pass[i].isLetterOrDigit()){
                        specialChar=true
                    }
                }
                if(!small){
                    Toast.makeText(this@RegisterActivity,"Password must contain a small letter (a,b,..z)",Toast.LENGTH_SHORT).show()
                }else if(!capital){
                    Toast.makeText(this@RegisterActivity,"Password must contain a Capital letter (A,B..Z",Toast.LENGTH_SHORT).show()
                }else if(!number){
                    Toast.makeText(this@RegisterActivity,"Password must contain a numeric digit (0,1,..9)",Toast.LENGTH_SHORT).show()
                }else if(!specialChar){
                    Toast.makeText(this@RegisterActivity,"Password must contain a special Character (!,@,#..)",Toast.LENGTH_SHORT).show()
                }else {
                    Toast.makeText(this@RegisterActivity,"Registered",Toast.LENGTH_SHORT).show()
                    signup(etName.text.toString(),etEmail.text.toString(),etMobileNumber.text.toString(),etAddress.text.toString(),pass)
                }   //name,email,mobileNumber,address,password
            }
        }
    }

    private fun signup(name:String,email: String,mobileNumber:String , address:String,pass: String) {
        auth.createUserWithEmailAndPassword(email, pass)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    dbRef=FirebaseDatabase.getInstance().getReference()
                    dbRef.child("user").child(auth.currentUser?.uid!!).setValue(User(auth.currentUser?.uid!!,name,email,mobileNumber,address))
                    showNotification(this, "Welcome $name to the Food Runner Family", "Explore Restaurants near you and order your favourite meal!!😋")
                    val intent= Intent(this@RegisterActivity, HomeActivity::class.java)
                    startActivity(intent)
                } else {
                    Toast.makeText(baseContext, "Registration failed.", Toast.LENGTH_SHORT).show()
                }
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
