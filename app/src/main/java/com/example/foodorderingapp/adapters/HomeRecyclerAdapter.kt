package com.example.foodorderingapp.adapters

import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.example.foodorderingapp.R
import com.example.foodorderingapp.activities.RestaurantMenuActivity
import com.example.foodorderingapp.model.Restaurants
import com.example.foodrunner.database.RestaurantDatabase
import com.example.foodrunner.database.RestaurantEntity
import com.squareup.picasso.Picasso

class HomeRecyclerAdapter(val context: Context, val itemList:ArrayList<Restaurants>) : RecyclerView.Adapter<HomeRecyclerAdapter.HomeViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeViewHolder {
        val view=LayoutInflater.from(parent.context).inflate(R.layout.recycler_home_single_row,parent,false)
        return HomeViewHolder(view)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(holder: HomeViewHolder, position: Int) {
        val restaurant=itemList[position]
        holder.txtRestaurantName.text=restaurant.restaurantName
        holder.txtAddress.text=restaurant.address
        holder.txtRestaurantRating.text=restaurant.restaurantRating
        Picasso.get().load(restaurant.foodImage).error(R.mipmap.ic_logo3).into(holder.imgFood)

        val restaurantEntity= RestaurantEntity(
            restaurant.restaurantId.toInt(),
            restaurant.restaurantName,
            restaurant.address,
            restaurant.restaurantRating,
            restaurant.foodImage
        )
        val checkFav=DBAsyncTask(context,restaurantEntity,1).execute()
        val isFav=checkFav.get()
        if(isFav) {
            holder.imgFav.setImageResource(R.drawable.ic_favourite)
        } else {
            holder.imgFav.setImageResource(R.drawable.ic_fav)
        }

        holder.imgFav.setOnClickListener {
            if(!DBAsyncTask(context,restaurantEntity,1).execute().get()) {
                val async=DBAsyncTask(context,restaurantEntity,2).execute()
                val result=async.get()
                if(result) {
                    holder.imgFav.setImageResource(R.drawable.ic_favourite)
                } else {
                    Toast.makeText(context,"Some Error occurred",Toast.LENGTH_SHORT).show()
                }
            } else{
                val async=DBAsyncTask(context,restaurantEntity,3).execute()
                val result=async.get()
                if(result) {
                    holder.imgFav.setImageResource(R.drawable.ic_fav)
                } else {
                    Toast.makeText(context,"Some Error occurred",Toast.LENGTH_SHORT).show()
                }
            }
        }
        holder.content.setOnClickListener{
            val intent= Intent(context, RestaurantMenuActivity::class.java)
            intent.putExtra("restaurant_id", restaurant.restaurantId)
            intent.putExtra("restaurant_name",restaurant.restaurantName)
            context.startActivity(intent)
        }
    }
    class HomeViewHolder(view: View):RecyclerView.ViewHolder(view) {
        val txtRestaurantName:TextView=view.findViewById(R.id.txtRestaurantName)
        val txtRestaurantRating:TextView=view.findViewById(R.id.txtRestaurantRating)
        val txtAddress:TextView=view.findViewById(R.id.txtAddress)
        val imgFood:ImageView=view.findViewById(R.id.imgFood)
        val imgFav:ImageView=view.findViewById(R.id.imgFav)
        val content:RelativeLayout=view.findViewById(R.id.content)
    }
}
class DBAsyncTask(val context: Context, val restaurantEntity: RestaurantEntity, val mode:Int): AsyncTask<Void, Void, Boolean>() {
    val db= Room.databaseBuilder(context, RestaurantDatabase::class.java,"restaurant-db").build()
    @Deprecated("Deprecated in Java")
    override fun doInBackground(vararg p0: Void?): Boolean {
        when(mode) {
            1->{
                val restaurant: RestaurantEntity?=db.RestaurantDao().getRestaurantsById(restaurantEntity.restaurant_id.toString())
                db.close()
                return restaurant!=null
            }
            2->{
                db.RestaurantDao().insertRestaurant(restaurantEntity)
                db.close()
                return true
            }
            3->{
                db.RestaurantDao().deleteRestaurant(restaurantEntity)
                db.close()
                return true
            }
        }
        return false
    }
}