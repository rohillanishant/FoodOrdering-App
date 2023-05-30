package com.example.foodorderingapp.adapters

import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.example.foodorderingapp.R
import com.example.foodorderingapp.activities.CartActivity
import com.example.foodorderingapp.database.FoodDatabase
import com.example.foodorderingapp.database.FoodEntity
import com.example.foodorderingapp.model.Menu
import com.squareup.picasso.Picasso
import java.lang.Exception

class RestaurantMenuRecyclerAdapter(val context: Context,
                                   val restaurantId: String?,
                                   val restaurantName: String?,
                                   val proceedPassed: RelativeLayout,
                                   val btnProceed: Button,
                                   val itemList:MutableList<Menu>)
    : RecyclerView.Adapter<RestaurantMenuRecyclerAdapter.RestaurantMenuViewHolder>(){

    var itemSelectedCount: Int = 0
    lateinit var proceedToCart: RelativeLayout
    var totalCost:Int?=0
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RestaurantMenuViewHolder {
        val view=LayoutInflater.from(parent.context).inflate(R.layout.recycler_restaurant_menu_single_row,parent,false)
        return RestaurantMenuViewHolder(view)
    }

    fun getSelectedItemCount(): Int {
        return itemSelectedCount
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(holder: RestaurantMenuViewHolder, position: Int) {
        val menu=itemList[position]
        proceedToCart=proceedPassed //Proceed button passed from activity to adapter
        holder.btnAdd.setTag(menu.foodId + "")//save the item id in textViewName Tag ,will be used to add to cart
        holder.txtFoodName.text=menu.foodName
        Picasso.get().load(menu.foodImage).error(R.mipmap.ic_logo3).into(holder.imgFood)
        holder.txtPrice.text="Rs."+menu.Price

        val foodEntity= FoodEntity(
            menu.foodId.toInt(),
            menu.foodName,
            menu.Price,
            menu.restaurantName,
            restaurantId.toString()
        )

        btnProceed.setOnClickListener(View.OnClickListener
        {
            val intent = Intent(context, CartActivity::class.java)
            intent.putExtra("restaurantId", restaurantId.toString())
            intent.putExtra("restaurantName", restaurantName)
            intent.putExtra("total_cost",totalCost)
            context.startActivity(intent)
            val checkAdd=DBAsyncTaskCart(context,foodEntity,1).execute().get()
            if(checkAdd){
                holder.btnAdd.text = "Remove"
                val color=ContextCompat.getColor(context, R.color.yellow)
                holder.btnAdd.setBackgroundColor(color)
            } else {
                holder.btnAdd.text = "Add"
                val color=ContextCompat.getColor(context,R.color.app_color)
                holder.btnAdd.setBackgroundColor(color)
            }
            totalCost=0
        })
        holder.btnAdd.setOnClickListener{
            if(!DBAsyncTaskCart(context,foodEntity,1).execute().get()) {
                val async=DBAsyncTaskCart(context,foodEntity,2).execute()
                val result=async.get()
                if(result) {
                    itemSelectedCount++ //item selected
                    Toast.makeText(context,"items selected : $itemSelectedCount",Toast.LENGTH_SHORT).show()
                    holder.btnAdd.text = "Remove"
                    holder.btnAdd.setBackgroundResource(R.color.yellow)
                    try {
                        totalCost = totalCost?.plus(menu.Price.toInt())
                    }catch (e:Exception) {
                        Toast.makeText(context,"$e",Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(context,"Some Error occurred", Toast.LENGTH_SHORT).show()
                }
            } else{
                val async=DBAsyncTaskCart(context,foodEntity,3).execute()
                val result=async.get()
                if(result) {
                    itemSelectedCount--     //item unselected
                    holder.btnAdd.text = "Add"
                    val color=ContextCompat.getColor(context,R.color.app_color)
                    holder.btnAdd.setBackgroundColor(color)
                    try {
                        totalCost= totalCost?.minus(menu.Price.toInt())
                    }catch (e:Exception) {
                        Toast.makeText(context,"$e",Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(context,"Some Error occurred", Toast.LENGTH_SHORT).show()
                }
            }

            if (itemSelectedCount > 0) {
                btnProceed.visibility=View.VISIBLE
            } else {
                btnProceed.visibility=View.GONE
            }
        }
    }
    class RestaurantMenuViewHolder(view: View):RecyclerView.ViewHolder(view) {
        val imgFood:ImageView=view.findViewById(R.id.imgFood)
        val txtFoodName:TextView=view.findViewById(R.id.txtFoodname)
        val txtPrice:TextView=view.findViewById(R.id.txtPrice)
        val btnAdd:Button=view.findViewById(R.id.btnAdd)
    }
}
class DBAsyncTaskCart(val context: Context, val foodEntity: FoodEntity, val mode:Int): AsyncTask<Void, Void, Boolean>() {
    val db= Room.databaseBuilder(context, FoodDatabase::class.java,"food-db").build()
    override fun doInBackground(vararg p0: Void?): Boolean {
        when(mode) {
            1->{
                val food: FoodEntity?=db.FoodDao().getFoodById(foodEntity.food_id.toString())
                db.close()
                return food!=null
            }
            2->{
                db.FoodDao().insertFood(foodEntity)
                db.close()
                return true
            }
            3->{
                db.FoodDao().deleteFood(foodEntity)
                db.close()
                return true
            }
        }
        return false
    }
}