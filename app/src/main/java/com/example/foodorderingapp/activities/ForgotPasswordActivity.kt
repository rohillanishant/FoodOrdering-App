package com.example.foodorderingapp.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.foodorderingapp.R
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class ForgotPasswordActivity : AppCompatActivity() {
    lateinit var etEmail: EditText
    lateinit var btnNext: Button
    lateinit var toolbar:androidx.appcompat.widget.Toolbar
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)

        toolbar=findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.title="Forgot Password"
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        etEmail=findViewById(R.id.etEmail)
        btnNext=findViewById(R.id.btnNext)

        btnNext.setOnClickListener {
            Firebase.auth.sendPasswordResetEmail(etEmail.text.toString())
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(this@ForgotPasswordActivity,"Check Email",Toast.LENGTH_SHORT).show()
                    }else {
                        Toast.makeText(this@ForgotPasswordActivity,"Error",Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }
}