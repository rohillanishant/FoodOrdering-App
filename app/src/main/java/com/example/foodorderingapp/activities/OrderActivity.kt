package com.example.foodorderingapp.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.example.foodorderingapp.R

class OrderActivity : AppCompatActivity() {
    lateinit var btnOk:Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order)
        btnOk=findViewById(R.id.btnOk)
        btnOk.setOnClickListener {
            val intent= Intent(this@OrderActivity,HomeActivity::class.java)
            startActivity(intent)
        }
    }
}