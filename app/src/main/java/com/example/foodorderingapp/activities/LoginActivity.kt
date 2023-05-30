package com.example.foodorderingapp.activities

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import com.example.foodorderingapp.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class LoginActivity : AppCompatActivity() {
    lateinit var sharedPreferences: SharedPreferences
    lateinit var txtForgotPassword: TextView
    lateinit var txtSignUp: TextView
    lateinit var btnLogin: Button
    lateinit var etEmail: EditText
    lateinit var toolbar: Toolbar
    lateinit var etPassword: EditText
    private lateinit var auth: FirebaseAuth;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Initialize Firebase Auth
        auth = Firebase.auth

        sharedPreferences=getSharedPreferences("FoodRunner Preferences", Context.MODE_PRIVATE)
        toolbar=findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setLogo(R.mipmap.ic_logo3)
        supportActionBar?.title="Login"

        txtForgotPassword=findViewById(R.id.txtForgotPassword)
        txtSignUp=findViewById(R.id.txtSignUp)
        btnLogin=findViewById(R.id.btnLogin)
        etEmail=findViewById(R.id.etEmail)
        etPassword=findViewById(R.id.etPassword)
        val isLoggedIn=sharedPreferences.getBoolean("isLoggedIn",false)
        if(isLoggedIn) {
            val intent=Intent(this@LoginActivity, HomeActivity::class.java)
            startActivity(intent)
        }

        txtForgotPassword.setOnClickListener() {
            val intent= Intent(this@LoginActivity, ForgotPasswordActivity::class.java)
            startActivity(intent)
        }
        txtSignUp.setOnClickListener() {
            val intent= Intent(this@LoginActivity, RegisterActivity::class.java)
            startActivity(intent)
        }
        btnLogin.setOnClickListener {
            val email=etEmail.text.toString()
            val password=etPassword.text.toString()
            if(email.isEmpty()){
                Toast.makeText(this@LoginActivity,"Enter email address",Toast.LENGTH_SHORT).show()
            }else if(password.isEmpty()){
                Toast.makeText(this@LoginActivity,"Enter Password",Toast.LENGTH_SHORT).show()
            }else {
                login(email,password)
            }

        }
    }

    private fun login(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    sharedPreferences.edit().putBoolean("isLoggedIn",true).apply()
                    val intent=Intent(this@LoginActivity, HomeActivity::class.java)
                    startActivity(intent)
                } else {
                    Toast.makeText(baseContext, "Login failed.", Toast.LENGTH_SHORT).show()
                }
            }
    }
}