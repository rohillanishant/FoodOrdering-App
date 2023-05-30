package com.example.foodorderingapp.database

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.RoomDatabase


@Database(entities = [FoodEntity::class], version = 2,  exportSchema = true, autoMigrations = [
    AutoMigration (from = 1, to = 2)])
abstract class FoodDatabase :RoomDatabase(){
    abstract fun FoodDao():FoodDao
}