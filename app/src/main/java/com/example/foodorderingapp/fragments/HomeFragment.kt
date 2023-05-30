package com.example.foodorderingapp.fragments

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.ProgressBar
import android.widget.RelativeLayout
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.foodorderingapp.R
import com.example.foodorderingapp.model.Restaurants
import com.example.foodorderingapp.adapters.HomeRecyclerAdapter
import com.example.foodorderingapp.util.ConnectionManager
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.*

class HomeFragment : Fragment() {

    lateinit var recyclerHome:RecyclerView
    lateinit var layoutManager: RecyclerView.LayoutManager
    lateinit var recyclerAdapter: HomeRecyclerAdapter
    lateinit var progresslayout: RelativeLayout
    lateinit var progressBar: ProgressBar
    val restaurantsList= arrayListOf<Restaurants>()
    val ratingComparator=Comparator<Restaurants>{restaurant1 , restaurant2  ->
        if(restaurant1.restaurantRating.compareTo(restaurant2.restaurantRating,true)==0) {
            restaurant1.restaurantName.compareTo(restaurant2.restaurantName,true)
        }else {
            restaurant1.restaurantRating.compareTo(restaurant2.restaurantRating,true)
        }
    }
    val alphabeticalComparator=Comparator<Restaurants> {restaurant1 , restaurant2 ->
            restaurant1.restaurantName.compareTo(restaurant2.restaurantName,true)
    }


    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view=inflater.inflate(R.layout.fragment_home, container, false)
        setHasOptionsMenu(true)
        recyclerHome=view.findViewById(R.id.recyclerHome)
        progresslayout=view.findViewById(R.id.progressLayout)
        progressBar=view.findViewById(R.id.progressBar)
        layoutManager=LinearLayoutManager(activity)
        progresslayout.visibility=View.VISIBLE
        if(!ConnectionManager().checkConnectivity(activity as Context)){
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
        }else {
            progresslayout.visibility=View.GONE
            val dbRef= FirebaseDatabase.getInstance().getReference("Restaurants")
            dbRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for(postsnapshot in snapshot.children){
                        val rid= postsnapshot.child("rid").getValue()
                        val name = postsnapshot.child("name").getValue()
                        val rating=postsnapshot.child("rating").getValue()
                        val image=postsnapshot.child("image_url").getValue()
                        val address=postsnapshot.child("address").getValue()
                        val restaurantObject= Restaurants(
                            rid.toString(),
                            name.toString(),
                            rating.toString(),
                            address.toString(),
                            image.toString()
                        )
                        restaurantsList.add(restaurantObject)
                    }
                    recyclerAdapter= HomeRecyclerAdapter(activity as Context,restaurantsList)
                    recyclerHome.adapter=recyclerAdapter
                    recyclerHome.layoutManager=layoutManager
                }
                override fun onCancelled(error: DatabaseError) {
                    //Log.e("Firebase", "Error reading data: ${error.message}")
                }
            })
        }
        return view
    }
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater?.inflate(R.menu.menu_sort,menu)
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id=item?.itemId
        if(id==R.id.actionSort) {
            val dialog=AlertDialog.Builder(activity as Context)
            dialog.setTitle("Sort By?")
            dialog.setIcon(R.drawable.ic_sort2_foreground)
            dialog.setPositiveButton("A-Z") { text,listener->
                Collections.sort(restaurantsList,alphabeticalComparator)
                recyclerAdapter.notifyDataSetChanged()
            }
            dialog.setNegativeButton("Z-A") {text,listener ->
                Collections.sort(restaurantsList,alphabeticalComparator)
                restaurantsList.reverse()
                recyclerAdapter.notifyDataSetChanged()
            }
            dialog.setNeutralButton("Rating(High to Low)") {text,listener ->
                Collections.sort(restaurantsList,ratingComparator)
                restaurantsList.reverse()
                recyclerAdapter.notifyDataSetChanged()
            }
            dialog.create()
            dialog.show()
        }
        return super.onOptionsItemSelected(item)
    }
}