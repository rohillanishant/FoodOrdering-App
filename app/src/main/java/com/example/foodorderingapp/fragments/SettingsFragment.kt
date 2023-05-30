package com.example.foodorderingapp.fragments

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import com.example.foodorderingapp.R
import com.example.foodorderingapp.activities.EditAccountActivity
import com.example.foodorderingapp.activities.LoginActivity
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase

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
}