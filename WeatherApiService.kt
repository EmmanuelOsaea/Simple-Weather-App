package com.emmanuel.weatherapp.data.remote

import retrofit2.http.GET
import retrofit2.http.Query

data class WeatherResponse(
    val name: String,
    val main: Main,
    val weather: List<Weather>
)
data class Main(val temp: Double, val humidity: Int)
data class Weather(val description: String)

interface WeatherApiService {
    @GET("data/2.5/weather")
    suspend fun getCurrentWeather(
        @Query("q") city: String,
        @Query("appid") apiKey: String,
        @Query("units") units: String = "metric"
    ): WeatherResponse
}
