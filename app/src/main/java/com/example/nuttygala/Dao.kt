package com.example.nuttygala

import androidx.room.Insert
import androidx.room.Query
import com.example.nuttygala.models.DryFruitItemInRoomDatabase
import kotlinx.coroutines.flow.Flow


@androidx.room.Dao
interface Dao {
    @Query("SELECT * FROM DryFruitItemInRoomDatabase")
    fun getAllDryFruitItems(): Flow<List<DryFruitItemInRoomDatabase>>

    @Insert
    fun addDryFruitsItems(item: DryFruitItemInRoomDatabase)
}