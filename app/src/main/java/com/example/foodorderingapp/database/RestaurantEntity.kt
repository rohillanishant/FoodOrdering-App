package com.example.foodrunner.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "restaurants")
data class RestaurantEntity(
    @PrimaryKey val restaurant_id:Int,
    @ColumnInfo val restaurantName:String,
    @ColumnInfo val restaurantRating:String,
    @ColumnInfo val address:String,
    @ColumnInfo val foodImage:String
)

