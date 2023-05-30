package com.example.foodorderingapp.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import com.example.foodorderingapp.R
import com.example.foodorderingapp.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase

class EditAccountActivity : AppCompatActivity() {

    lateinit var etName: EditText
    lateinit var etMobileNumber: EditText
    lateinit var etAddress: EditText
    lateinit var btnUpdate: Button
    lateinit var toolbar: Toolbar
    private lateinit var auth: FirebaseAuth
    private lateinit var dbRef: DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_account)
        etName=findViewById(R.id.etName)
        etMobileNumber=findViewById(R.id.etMobileNumber)
        etAddress=findViewById(R.id.etAddress)
        auth = Firebase.auth
        btnUpdate=findViewById(R.id.btnUpdate)

        toolbar=findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setIcon(R.mipmap.ic_edit)
        supportActionBar?.title="Edit Details"
        val userRef= FirebaseDatabase.getInstance().getReference("user").child(Firebase.auth.currentUser!!.uid)
        userRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val name = snapshot.child("name").getValue()
                val mobileNumber=snapshot.child("mobileNumber").getValue()
                val address=snapshot.child("address").getValue()
                etName.hint=name.toString()
                etAddress.hint=address.toString()
                etMobileNumber.hint=mobileNumber.toString()
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("Firebase", "Error reading data: ${error.message}")
            }
        })
        btnUpdate.setOnClickListener {
            if(etName.text.toString().isEmpty())
                Toast.makeText(this@EditAccountActivity,"Enter Name", Toast.LENGTH_LONG).show()
            else if(etName.length()<4) {
                Toast.makeText(this@EditAccountActivity,"Name should contain atleast 3 characters",
                    Toast.LENGTH_LONG).show()
            }
            else if(etMobileNumber.text.toString().isEmpty())
                Toast.makeText(this@EditAccountActivity,"Enter Mobile Number", Toast.LENGTH_LONG).show()
            else if(etMobileNumber.length()!=10) {
                Toast.makeText(this@EditAccountActivity,"Mobile Number should have 10 digits", Toast.LENGTH_LONG).show()
            }
            else if(etAddress.text.toString().isEmpty())
                Toast.makeText(this@EditAccountActivity,"Enter Delivery Address", Toast.LENGTH_LONG).show()
            else {
                dbRef=FirebaseDatabase.getInstance().getReference()
                dbRef.child("user").child(auth.currentUser?.uid!!).child("name").setValue(etName.text.toString())
                dbRef.child("user").child(auth.currentUser?.uid!!).child("mobileNumber").setValue(etMobileNumber.text.toString())
                dbRef.child("user").child(auth.currentUser?.uid!!).child("address").setValue(etAddress.text.toString())
                Toast.makeText(this@EditAccountActivity,"Details Updated Successfully",Toast.LENGTH_SHORT).show()
                val intent= Intent(this@EditAccountActivity,HomeActivity::class.java)
                startActivity(intent)
            }
        }

    }
}