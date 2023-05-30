package com.example.foodorderingapp.model

data class OrderHistory(
    val restaurantName:String,
    val orderDate:String,
    val orderTime:String,
    val totalCost:String,
    val foodName :ArrayList<String>,
    val foodPrice:ArrayList<String>
)
