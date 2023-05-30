package com.example.foodorderingapp.fragments

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.provider.Settings
import android.view.*
import android.widget.TextView
import androidx.fragment.app.Fragment
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.foodorderingapp.R
import com.example.foodorderingapp.adapters.HomeRecyclerAdapter
import com.example.foodorderingapp.adapters.OrderHistoryRecyclerAdapter
import com.example.foodorderingapp.model.OrderHistory
import com.example.foodorderingapp.model.Restaurants
import com.example.foodorderingapp.util.ConnectionManager
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import java.util.*
import kotlin.collections.ArrayList

class OrderHistoryFragment : Fragment() {
    lateinit var sharedPreferences: SharedPreferences
    lateinit var recyclerOrder: RecyclerView
    lateinit var layoutManager: RecyclerView.LayoutManager
    lateinit var recyclerAdapter: OrderHistoryRecyclerAdapter
    var foodList= arrayListOf<OrderHistory>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view=inflater.inflate(R.layout.fragment_order_history, container, false)
        recyclerOrder=view.findViewById(R.id.recyclerOrder)
        layoutManager= LinearLayoutManager(activity)
        sharedPreferences=requireContext().getSharedPreferences(getString(R.string.preference_file_name), Context.MODE_PRIVATE)
        val userId= Firebase.auth.currentUser?.uid
        if(ConnectionManager().checkConnectivity(activity as Context)) {
            val dbRef= FirebaseDatabase.getInstance().getReference("user").child(userId.toString()).child("orders")
            dbRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (postsnapshot in snapshot.children) {
                        val foodName = ArrayList<String>()
                        val foodPrice = ArrayList<String>()
                        val dateTime = postsnapshot.child("date_time").getValue().toString()
                        val totalCost = postsnapshot.child("total_cost").getValue().toString()
                        lateinit var restaurantName:String
                        for(i in postsnapshot.children){
                            if(i.hasChildren()){
                                restaurantName=i.child("restaurantName").getValue().toString()
                                foodName.add(i.child("foodName").getValue().toString()?:"")
                                foodPrice.add(i.child("price").getValue().toString())
                            }
                        }
                        val orderHistory=OrderHistory(
                            restaurantName,
                            dateTime.substring(0,10),
                            dateTime.substring(11),
                            totalCost,
                            foodName,
                            foodPrice
                        )
                        foodList.add(orderHistory)
                    }
                    if(sharedPreferences.getBoolean("newToOld",false)){
                        foodList.reverse()
                    }
                    recyclerAdapter= OrderHistoryRecyclerAdapter(activity as Context,foodList)
                    recyclerOrder.adapter=recyclerAdapter
                    recyclerOrder.layoutManager=layoutManager
                }
                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            }
            )
        } else {
            val dialog= AlertDialog.Builder(activity as Context)
            dialog.setTitle("Error")
            dialog.setMessage("Internet connection is not found")
            dialog.setPositiveButton("Open settings") { text,listener->
                val intent= Intent(Settings.ACTION_WIFI_SETTINGS)
                startActivity(intent)
                activity?.finish()
            }
            dialog.setNegativeButton("Exit app") {text,listener ->
                ActivityCompat.finishAffinity(activity as Activity)
            }
            dialog.create()
            dialog.show()
        }
        return view
    }
}