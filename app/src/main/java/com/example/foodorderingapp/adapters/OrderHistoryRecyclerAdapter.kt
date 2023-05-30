package com.example.foodorderingapp.adapters

import android.content.Context
import android.content.Context.LAYOUT_INFLATER_SERVICE
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.foodorderingapp.R
import com.example.foodorderingapp.model.OrderHistory

class OrderHistoryRecyclerAdapter(val context: Context,val itemList:ArrayList<OrderHistory>) : RecyclerView.Adapter<OrderHistoryRecyclerAdapter.OrderViewHolder>(){

    class OrderViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val txtRestaurantName:TextView=view.findViewById(R.id.txtRestaurantName)
        val txtOrderDate:TextView=view.findViewById(R.id.txtOrderDate)
        val txtTotalCost:TextView=view.findViewById(R.id.txtTotalCost)
        val txtOrderTime:TextView=view.findViewById(R.id.txtOrderTime)
        val llFood:LinearLayout=view.findViewById(R.id.llFood)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val view= LayoutInflater.from(parent.context).inflate(R.layout.recycler_order_history_restaurant,parent,false)
        return OrderViewHolder(view)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        val food=itemList[position]
        holder.txtRestaurantName.text=food.restaurantName
        holder.txtOrderDate.text=food.orderDate.replace('-','/')
        holder.txtTotalCost.text="Rs."+food.totalCost
        holder.txtOrderTime.text=food.orderTime
        for(i in 0 until food.foodName.size) {
            val inflater: LayoutInflater? =
                context.getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater?
            val llFoodItem =
                inflater?.inflate(R.layout.recycler_cart_single_row, null) as LinearLayout

            val txtFoodName: TextView = llFoodItem.findViewById(R.id.txtFoodname)

            val txtFoodPrice: TextView = llFoodItem.findViewById(R.id.txtPrice)

            val itemName = food.foodName[i]
            val itemCost = "Rs. ${food.foodPrice[i]}"
            txtFoodName.text = itemName
            txtFoodPrice.text = itemCost

            holder.llFood.addView(llFoodItem)
        }
    }

    override fun getItemCount(): Int {
        return itemList.size
    }
}