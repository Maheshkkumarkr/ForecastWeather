package com.mahi.weatherapp.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.mahi.weatherapp.data.local.entity.CityEntity

@Dao
interface CityDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(city: CityEntity)

    @Query("SELECT * FROM cities WHERE LOWER(name) = LOWER(:name) LIMIT 1")
    suspend fun getCity(name: String): CityEntity?
}
