package com.example.foodorderingapp.fragments

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import com.example.foodorderingapp.activities.LoginActivity
import com.example.foodorderingapp.R
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase

class ProfileFragment : Fragment() {
    lateinit var txtName:TextView
    lateinit var txtPhoneNumber:TextView
    lateinit var txtAddress:TextView
    lateinit var txtEmail:TextView
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view =inflater.inflate(R.layout.fragment_profile, container, false )

        txtName=view.findViewById(R.id.txtName)
        txtPhoneNumber=view.findViewById(R.id.txtPhoneNumber)
        txtAddress=view.findViewById(R.id.txtAddress)
        txtEmail=view.findViewById(R.id.txtEmail)
        val userRef=FirebaseDatabase.getInstance().getReference("user").child(Firebase.auth.currentUser!!.uid)
        userRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val name = snapshot.child("name").getValue()
                val email=snapshot.child("email").getValue()
                val mobileNumber=snapshot.child("mobileNumber").getValue()
                val address=snapshot.child("address").getValue()
                txtName.text=name.toString()
                txtEmail.text=email.toString()
                txtAddress.text=address.toString()
                txtPhoneNumber.text=mobileNumber.toString()
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("Firebase", "Error reading data: ${error.message}")
            }
        })


        return view
    }
}