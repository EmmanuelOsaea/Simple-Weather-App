package com.emmanuel.weatherapp

import android.os.Bundle
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.emmanuel.weatherapp.databinding.ActivityMainBinding
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val baseUrl = "https://api.openweathermap.org/"
    FirebaseApp.initializeApp(this)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in)

        val retrofit = Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val api = retrofit.create(WeatherApiService::class.java)
        val repository = WeatherRepository(api, WeatherDatabase.getDatabase(this).weatherDao())

        binding.checkWeatherButton.setOnClickListener {
            val city = binding.cityInput.text.toString().trim()
            if (city.isEmpty()) {
                Toast.makeText(this, "Enter a city name", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            binding.progressBar.visibility = android.view.View.VISIBLE
            lifecycleScope.launch {
                val entity = repository.getWeather(city, BuildConfig.OPEN_WEATHER_API_KEY)
                binding.progressBar.visibility = android.view.View.GONE
                if (entity != null) {
                    val text = "🌍 City: ${entity.city}\n🌡 ${entity.temperature}°C\n💧 ${entity.humidity}%\n☁️ ${entity.description}"
                    binding.resultText.text = text
                    binding.weatherIcon.setImageResource(resolveIcon(entity.description))
                    binding.bgImage.startAnimation(fadeIn)
                } else {
                    Toast.makeText(this@MainActivity, "Failed to fetch weather", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun resolveIcon(description: String): Int {
        return when {
            description.contains("rain", true) -> R.drawable.ic_rain
            description.contains("cloud", true) -> R.drawable.ic_cloudy
            description.contains("snow", true) -> R.drawable.ic_snow
            description.contains("clear", true) -> R.drawable.ic_sunny
            else -> R.drawable.ic_weather_default
        }
    }
}


private fun updateBackground(condition: String) {
    val bgImage = findViewById<ImageView>(R.id.bgImage)

    when {
        condition.contains("rain", ignoreCase = true) -> {
            bgImage.setImageResource(R.drawable.sky_3499982_1280) // rainy sky
        }
        condition.contains("snow", ignoreCase = true) -> {
            bgImage.setImageResource(R.drawable.snow_496875_1280)
        }
        condition.contains("cloud", ignoreCase = true) -> {
            bgImage.setImageResource(R.drawable.fair_weather_clouds_2117442_1280)
        }
        condition.contains("sun", ignoreCase = true) || condition.contains("clear", ignoreCase = true) -> {
            bgImage.setImageResource(R.drawable.birds_5552482_1280)
        }
        condition.contains("wind", ignoreCase = true) -> {
            bgImage.setImageResource(R.drawable.wood_4449746_1280)
        }
        else -> {
            bgImage.setImageResource(R.drawable.autumn_5649620_1280) // default
        }
    }
}
