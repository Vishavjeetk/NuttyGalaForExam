package com.example.nuttygala

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.nuttygala.models.DryFruitItemInRoomDatabase

@Database(entities =  [DryFruitItemInRoomDatabase::class], version = 1, exportSchema = false)
abstract class AppDatabase: RoomDatabase() {
    abstract fun daoObject(): Dao
}