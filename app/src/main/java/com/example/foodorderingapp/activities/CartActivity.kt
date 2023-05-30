package com.example.foodorderingapp.activities

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.example.foodorderingapp.R
import com.example.foodorderingapp.adapters.CartRecyclerAdapter
import com.example.foodorderingapp.database.FoodDatabase
import com.example.foodorderingapp.database.FoodEntity
import com.example.foodorderingapp.model.Cart
import com.example.foodorderingapp.model.User
import com.example.foodorderingapp.util.ConnectionManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.razorpay.Checkout
import com.razorpay.PaymentResultListener
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date

class CartActivity : AppCompatActivity(), PaymentResultListener {
    lateinit var toolbar: Toolbar
    lateinit var txtRestaurantName: TextView
    lateinit var recyclerCart: RecyclerView
    lateinit var layoutManager: RecyclerView.LayoutManager
    lateinit var recyclerAdapter: CartRecyclerAdapter
    lateinit var btnOrder: Button
    lateinit var dbRef: DatabaseReference
    lateinit var auth: FirebaseAuth
   var dbCartList= arrayListOf<Cart>()
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart)
        toolbar=findViewById(R.id.toolbar)
        txtRestaurantName=findViewById(R.id.txtRestaurantName)
        btnOrder=findViewById(R.id.btnOrder)
        setSupportActionBar(toolbar)
        supportActionBar?.title="My Cart"
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        auth = Firebase.auth
        recyclerCart=findViewById(R.id.recyclerCart)
        layoutManager= LinearLayoutManager(this@CartActivity)

        val foodList=RetrieveCart(applicationContext).execute().get()
        for(i in foodList) {
            dbCartList.add(
                Cart(
                    i.food_id.toString(),
                    i.foodName,
                    i.foodPrice,
                    i.restaurantName,
                    i.restaurantId
                )
            )
            recyclerAdapter= CartRecyclerAdapter(applicationContext,dbCartList)
            recyclerCart.adapter=recyclerAdapter
            recyclerCart.layoutManager=layoutManager
        }
        txtRestaurantName.text="Ordering from : " + intent.getStringExtra("restaurantName")
        val totalCost= intent.getIntExtra("total_cost",0)
        val cost=totalCost.toString()
        btnOrder.text="Place Order(Total Rs. ${cost} )"
        btnOrder.setOnClickListener {
            DeleteCart(applicationContext).execute().get()
            if(ConnectionManager().checkConnectivity(this@CartActivity)) {
                startPayment(totalCost)
            } else {
                val dialog= AlertDialog.Builder(this@CartActivity)
                dialog.setTitle("Error")
                dialog.setMessage("Internet Connection Not Found")
                dialog.setPositiveButton("Open Settings"){ text,listener->
                    val settingsIntent= Intent(Settings.ACTION_WIFI_SETTINGS)
                    startActivity(settingsIntent)
                    this.finish()
                }
                dialog.setNegativeButton("Exit"){ text,listener->
                    ActivityCompat.finishAffinity(this@CartActivity)
                }
                dialog.create()
                dialog.show()
            }
        }
    }
    private fun startPayment(totalCost: Int) {
        /*
        *  You need to pass the current activity to let Razorpay create CheckoutActivity
        * */
        val co = Checkout()

        try {
            val options = JSONObject()
            options.put("name","Food Runner")
            options.put("description","Demoing Charges")
            //You can omit the image option to fetch the image from the dashboard
            //options.put("image","https://s3.amazonaws.com/rzp-mobile/images/rzp.jpg")
            options.put("theme.color", "#FC4C3B");
            options.put("currency","INR");
            var cost=totalCost.toString()
            cost+="00";
            options.put("amount", cost)//pass amount in currency subunits

            val prefill = JSONObject()
            prefill.put("email","")
            prefill.put("contact","")

            options.put("prefill",prefill)
            co.open(this,options)
        }catch (e: Exception){
            Toast.makeText(this,"Error in payment: "+ e.message,Toast.LENGTH_LONG).show()
            e.printStackTrace()
        }
    }
    override fun onBackPressed() {
        DeleteCart(applicationContext).execute().get()
        val intent= Intent(this@CartActivity,HomeActivity::class.java)
        startActivity(intent)
        super.onBackPressed()
    }
    class RetrieveCart(val context: Context): AsyncTask<Void, Void, List<FoodEntity>>() {
        @Deprecated("Deprecated in Java")
        override fun doInBackground(vararg p0: Void?): List<FoodEntity> {
            val db= Room.databaseBuilder(context, FoodDatabase::class.java,"food-db").build()
            return db.FoodDao().getAllFoods()
        }
    }
    class DeleteCart(val context: Context): AsyncTask<Void, Void, Boolean>() {
        val db= Room.databaseBuilder(context, FoodDatabase::class.java,"food-db").build()
        override fun doInBackground(vararg p0: Void?): Boolean {
            db.FoodDao().clearCart()
            db.close()
            return true
        }
    }
    override fun onPaymentSuccess(p0: String?) {
        val currentDate = Date()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        val formattedDate = dateFormat.format(currentDate)
        dbRef= FirebaseDatabase.getInstance().getReference()
        val totalCost= intent.getIntExtra("total_cost",0)
        dbRef.child("user").child(auth.currentUser?.uid!!).child("orders").child(formattedDate).setValue(dbCartList)
        dbRef.child("user").child(auth.currentUser?.uid!!).child("orders").child(formattedDate).child("total_cost").setValue(totalCost)
        dbRef.child("user").child(auth.currentUser?.uid!!).child("orders").child(formattedDate).child("date_time").setValue(formattedDate)
        Toast.makeText(this@CartActivity,"Payment Successfull",Toast.LENGTH_SHORT).show()
            val intent=Intent(this@CartActivity,OrderActivity::class.java)
            startActivity(intent)
            finishAffinity()
    }
    override fun onPaymentError(p0: Int, p1: String?) {
        Toast.makeText(this@CartActivity,"Payment Failed",Toast.LENGTH_SHORT).show()
    }
}
