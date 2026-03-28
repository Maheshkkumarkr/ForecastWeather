package com.mahi.weatherapp.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.mahi.weatherapp.data.local.entity.ForecastEntity

@Dao
interface ForecastDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(items: List<ForecastEntity>)

    @Query("DELETE FROM forecast WHERE LOWER(cityName) = LOWER(:cityName)")
    suspend fun clear(cityName: String)


    @Query("SELECT * FROM forecast WHERE LOWER(cityName) = LOWER(:cityName) ORDER BY id ASC")
    suspend fun getForecast(cityName: String): List<ForecastEntity>
}
