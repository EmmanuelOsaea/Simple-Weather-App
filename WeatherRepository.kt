package com.example.weatherapp.data

import com.example.weatherapp.data.local.WeatherDao
import com.example.weatherapp.data.local.WeatherEntity
import com.example.weatherapp.data.remote.WeatherApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class WeatherRepository(
    private val api: WeatherApi,
    private val dao: WeatherDao
) {
    suspend fun getWeather(city: String, apiKey: String): WeatherEntity? = withContext(Dispatchers.IO) {
        try {
            val response = api.getCurrentWeather(city, apiKey)
            val entity = WeatherEntity(
                city = response.name,
                temperature = response.main.temp,
                description = response.weather[0].description,
                lastUpdated = System.currentTimeMillis()
            )
            dao.insertWeather(entity)
            entity
        } catch (e: Exception) {
            dao.getWeather(city) // fallback to cached
        }
    }
}
