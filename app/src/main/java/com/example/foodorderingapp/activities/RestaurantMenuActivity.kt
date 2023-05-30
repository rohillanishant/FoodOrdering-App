package com.example.foodorderingapp.activities

import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.example.foodorderingapp.R
import com.example.foodorderingapp.adapters.HomeRecyclerAdapter
import com.example.foodorderingapp.adapters.RestaurantMenuRecyclerAdapter
import com.example.foodorderingapp.database.FoodDatabase
import com.example.foodorderingapp.model.Menu
import com.example.foodorderingapp.model.Restaurants
import com.example.foodorderingapp.util.ConnectionManager
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue
import java.util.*
import kotlin.Comparator

class RestaurantMenuActivity : AppCompatActivity() {
    lateinit var recyclerRestaurantmenu:RecyclerView
    lateinit var layoutManager: RecyclerView.LayoutManager
    lateinit var recyclerAdapter: RestaurantMenuRecyclerAdapter
    lateinit var progressBar: ProgressBar
    lateinit var progressLayout:RelativeLayout
    lateinit var btnProceed:Button
    lateinit var rlProceed:RelativeLayout
    lateinit var toolbar:Toolbar
    var restaurant_id:String?=null
    val restaurantMenu: MutableList<Menu> = mutableListOf()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_restaurant_menu)

        toolbar=findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.title=intent.getStringExtra("restaurant_name")
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        recyclerRestaurantmenu=findViewById(R.id.recyclerRestrauntMenu)
        progressLayout=findViewById(R.id.progressLayout)
        progressBar=findViewById(R.id.progressBar)

        btnProceed=findViewById(R.id.btnProceed)
        rlProceed=findViewById(R.id.rlProceed)

        layoutManager=LinearLayoutManager(this@RestaurantMenuActivity)
        progressLayout.visibility= View.VISIBLE
        btnProceed.visibility=View.GONE
        if(intent!=null) {
            restaurant_id=intent.getStringExtra("restaurant_id")
        } else {
            Toast.makeText(this@RestaurantMenuActivity,"Some Unexpected error occurred",Toast.LENGTH_SHORT).show()
            finish()
        }
        if(ConnectionManager().checkConnectivity(this@RestaurantMenuActivity)) {
            progressLayout.visibility=View.GONE
            btnProceed.visibility=View.GONE
            val dbRef= FirebaseDatabase.getInstance().getReference("Restaurants").child((restaurant_id!!.toInt()-1).toString()).child("menu")
            dbRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (postsnapshot in snapshot.children) {
                        val id = postsnapshot.child("id").getValue()
                        val name = postsnapshot.child("name").getValue()
                        val cost = postsnapshot.child("cost").getValue()
                        val imgFood=postsnapshot.child("foodImage").getValue()
                        val menuObject = Menu(
                            id.toString(),
                            name.toString(),
                            imgFood.toString(),
                            cost.toString(),
                            intent.getStringExtra("restaurant_name").toString()
                        )
                        restaurantMenu.add(menuObject)
                    }
                    recyclerAdapter = RestaurantMenuRecyclerAdapter(
                        this@RestaurantMenuActivity,
                        restaurant_id,
                        intent.getStringExtra("restaurant_name"),
                        rlProceed,//pass the relative layout which has the button to enable it later
                        btnProceed,
                        restaurantMenu
                    )
                    recyclerRestaurantmenu.adapter = recyclerAdapter
                    recyclerRestaurantmenu.layoutManager = layoutManager
                }
                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })
        } else {
            val dialog= AlertDialog.Builder(this@RestaurantMenuActivity)
            dialog.setTitle("Error")
            dialog.setMessage("Internet Connection Not Found")
            dialog.setPositiveButton("Open Settings"){ text,listener->
                val settingsIntent= Intent(Settings.ACTION_WIFI_SETTINGS)
                startActivity(settingsIntent)
                this.finish()
            }
            dialog.setNegativeButton("Exit"){ text,listener->
                ActivityCompat.finishAffinity(this@RestaurantMenuActivity)
            }
            dialog.create()
            dialog.show()
        }

    }
    override fun onBackPressed() {
        if (recyclerAdapter.getSelectedItemCount() > 0) {
            val alterDialog = AlertDialog.Builder(this@RestaurantMenuActivity)
            alterDialog.setTitle("Alert!")
            alterDialog.setMessage("Going back will remove everything from cart")
            alterDialog.setPositiveButton("Okay")
            { _, _ ->
                recyclerAdapter.itemSelectedCount=0
                if (DBAsyncTask(this@RestaurantMenuActivity, 0).execute().get()) {
                    //cleared
                    Toast.makeText(
                        this@RestaurantMenuActivity,
                        "Cart database cleared",
                        Toast.LENGTH_SHORT
                    ).show()
                    super.onBackPressed()
                } else {
                    Toast.makeText(
                        this@RestaurantMenuActivity,
                        "Cart database not cleared",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                super.onBackPressed()
            }
            alterDialog.setNegativeButton("No")
            { _, _ ->
                //do nothing
            }
            alterDialog.show()
        } else {
            super.onBackPressed()
        }
    }


    class DBAsyncTask(val context: Context, val mode: Int) :
        AsyncTask<Void, Void, Boolean>() {

        private val db = Room.databaseBuilder(context, FoodDatabase::class.java, "food-db").build()

        override fun doInBackground(vararg params: Void?): Boolean {
            when (mode) {
                //Remove all items from cart
                0 -> {
                    db.FoodDao().clearCart()
                    db.close()
                    return true
                }
                //check if cart is empty or not
                1 -> {
                    val cartItems = db.FoodDao().getAllFoods()
                    db.close()
                    return cartItems.size > 0
                }
            }
            return false
        }
    }
    override fun onCreateOptionsMenu(menu: android.view.Menu): Boolean {
        // Inflate the menu XML file
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.menu_sort, menu)
        return true
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id=item?.itemId
        if(id==R.id.actionSort) {
            val dialog= android.app.AlertDialog.Builder(this@RestaurantMenuActivity)
            dialog.setTitle("Sort By?")
            dialog.setPositiveButton("Price(Low to High)") { text,listener->
                restaurantMenu.sortBy { it.Price.toDouble() }
                recyclerAdapter.notifyDataSetChanged()
            }
            dialog.setNegativeButton("Price(High to Low)") {text,listener ->
                restaurantMenu.sortByDescending { it.Price.toDouble() }
                recyclerAdapter.notifyDataSetChanged()
            }
            dialog.setIcon(R.drawable.ic_sort2_foreground)
            dialog.create()
            dialog.show()
        }
        return super.onOptionsItemSelected(item)
    }
}