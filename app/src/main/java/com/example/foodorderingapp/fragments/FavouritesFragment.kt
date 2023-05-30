package com.example.foodorderingapp.fragments

import android.app.AlertDialog
import android.content.Context
import android.os.AsyncTask
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.example.foodorderingapp.R
import com.example.foodorderingapp.model.Restaurants
import com.example.foodorderingapp.adapters.HomeRecyclerAdapter
import com.example.foodrunner.database.RestaurantDatabase
import com.example.foodrunner.database.RestaurantEntity
import java.util.*
import kotlin.Comparator

class FavouritesFragment : Fragment() {
    lateinit var recyclerFav: RecyclerView
    lateinit var layoutManager: RecyclerView.LayoutManager
    lateinit var recyclerAdapter: HomeRecyclerAdapter
    lateinit var progresslayout: RelativeLayout
    lateinit var progressBar: ProgressBar

    var dbRestaurantList=arrayListOf<Restaurants>()
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
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view =inflater.inflate(R.layout.fragment_favourites, container, false)
        setHasOptionsMenu(true)
        recyclerFav=view.findViewById(R.id.recyclerFav)
        progresslayout=view.findViewById(R.id.progressLayout)
        progressBar=view.findViewById(R.id.progressBar)
        progresslayout.visibility=View.VISIBLE
        layoutManager= LinearLayoutManager(activity)
        val restaurantList=RetrieveFavourites(activity as Context).execute().get()
        for(i in restaurantList) {
            dbRestaurantList.add(
                Restaurants(
                    i.restaurant_id.toString(),
                    i.restaurantName,
                    i.address,
                    i.restaurantRating,
                    i.foodImage
                )
            )
        }
        if(dbRestaurantList.isEmpty()) {
            Toast.makeText(context,"You have not selected any restraunt as your favourite", Toast.LENGTH_SHORT).show()
        }
        if(activity!=null) {
            progresslayout.visibility=View.GONE
            recyclerAdapter= HomeRecyclerAdapter(activity as Context,dbRestaurantList)
            recyclerFav.adapter=recyclerAdapter
            recyclerFav.layoutManager=layoutManager
        }
        return view
    }
    class RetrieveFavourites(val context: Context): AsyncTask<Void, Void, List<RestaurantEntity>>() {
        override fun doInBackground(vararg p0: Void?): List<RestaurantEntity> {
            val db= Room.databaseBuilder(context, RestaurantDatabase::class.java,"restaurant-db").build()
            return db.RestaurantDao().getAllRestaurants()
        }

    }
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater?.inflate(R.menu.menu_sort,menu)
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id=item?.itemId
        if(id==R.id.actionSort) {
            val dialog= AlertDialog.Builder(activity as Context)
            dialog.setTitle("Sort By?")
            dialog.setIcon(R.drawable.ic_sort2_foreground)
            dialog.setPositiveButton("A-Z") { text,listener->
                Collections.sort(dbRestaurantList,alphabeticalComparator)
                recyclerAdapter.notifyDataSetChanged()
            }
            dialog.setNegativeButton("Z-A") {text,listener ->
                Collections.sort(dbRestaurantList,alphabeticalComparator)
                dbRestaurantList.reverse()
                recyclerAdapter.notifyDataSetChanged()
            }
            dialog.setNeutralButton("Rating(High to Low)") {text,listener ->
                Collections.sort(dbRestaurantList,ratingComparator)
                recyclerAdapter.notifyDataSetChanged()
            }
            dialog.create()
            dialog.show()
        }
        return super.onOptionsItemSelected(item)
    }
}