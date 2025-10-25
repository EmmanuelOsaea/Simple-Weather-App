package com.example.weatherapp.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "weather_table")
data class WeatherEntity(
    @PrimaryKey val city: String,
    val temperature: Double,
    val description: String,
    val lastUpdated: Long
)
