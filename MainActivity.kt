package com.example.weatherapp

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.weatherapp.databinding.ActivityMainBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.URL

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val apiKey = "8cd233e9a042b1d1aa5bc6fc65347c49" // Replace with your OpenWeatherMap API key
    private val city = "Lagos"
    private val url = "https://api.openweathermap.org/data/2.5/weather?q=$city&appid=$apiKey&units=metric" 
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.checkWeatherButton.setOnClickListener {
            val city = binding.cityInput.text.toString()
            if (city.isNotEmpty()) {
                fetchWeather(city)
            } else {
                Toast.makeText(this, "Enter a city name", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun fetchWeather(city: String) {
        lifecycleScope.launch {
            val response = withContext(Dispatchers.IO) {
                try {
                    URL("https://api.openweathermap.org/data/2.5/weather?q=$city&appid=$apiKey&units=metric")
                        .readText()
                } catch (e: Exception) {
                    null
                }
            }

            if (response != null) {
                val json = JSONObject(response)
                val main = json.getJSONObject("main")
                val weatherArray = json.getJSONArray("weather")
                val weather = weatherArray.getJSONObject(0)
                val temp = main.getString("temp")
                val humidity = main.getString("humidity")
                val condition = weather.getString("description")

                binding.resultText.text = "🌡️ Temp: $temp°C\n💧 Humidity: $humidity%\n☁️ Condition: $condition"
            } else {
                Toast.makeText(this@MainActivity, "City not found!", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
