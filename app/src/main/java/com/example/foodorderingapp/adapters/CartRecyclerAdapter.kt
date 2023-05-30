package com.example.foodorderingapp.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.foodorderingapp.R
import com.example.foodorderingapp.model.Cart

class CartRecyclerAdapter(val context: Context,val itemList:ArrayList<Cart>) : RecyclerView.Adapter<CartRecyclerAdapter.CartViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val view= LayoutInflater.from(parent.context).inflate(R.layout.recycler_cart_single_row,parent,false)
        return CartViewHolder(view)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        val cart=itemList[position]
        holder.txtFoodName.text=cart.foodName
        holder.txtPrice.text="Rs."+ cart.Price
    }
    class CartViewHolder(view: View):RecyclerView.ViewHolder(view) {
        val txtFoodName: TextView =view.findViewById(R.id.txtFoodname)
        val txtPrice: TextView =view.findViewById(R.id.txtPrice)
    }
}