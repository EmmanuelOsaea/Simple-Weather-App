package com.example.weatherapp

import android.os.Bundle
import android.view.animation.AnimationUtils
import android.widget.*
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
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

btnSearch.setOnClickListener {
   // Haptic feedback
val vibrator = getSystemService(VIBRATOR_SERVICE) as android.os.Vibrator
if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
    vibrator.vibrate(android.os.VibrationEffect.createOneShot(50, android.os.VibrationEffect.DEFAULT_AMPLITUDE))
} else {
    @Suppress("DEPRECATION")
    vibrator.vibrate(50)
}
    
    
    val city = etCity.text.toString().trim()
    if (city.isNotEmpty()) {
        val fadeOut = AnimationUtils.loadAnimation(this, R.anim.fade_out)

        // Fade out old data
        tvResult.startAnimation(fadeOut)
        fadeOut.setAnimationListener(object : android.view.animation.Animation.AnimationListener {
            override fun onAnimationStart(animation: android.view.animation.Animation) {}
            override fun onAnimationRepeat(animation: android.view.animation.Animation) {}
            override fun onAnimationEnd(animation: android.view.animation.Animation) {
                // After fade-out, clear text and fetch new data
                tvResult.text = ""
                progressBar.visibility = ProgressBar.VISIBLE
                fetchWeather(city, tvResult, progressBar, swipeRefresh, fadeIn)
            }
        })
    } else {
        Toast.makeText(this, "Please enter a city name", Toast.LENGTH_SHORT).show()
    }
}
    
// Swipe-to-refresh listener
        swipeRefresh.setOnRefreshListener {
            val city = etCity.text.toString().trim()
            if (city.isNotEmpty()) {
                fetchWeather(city, tvResult, progressBar, swipeRefresh, fadeIn)
            } else {
                swipeRefresh.isRefreshing = false
                Toast.makeText(this, "Enter a city name to refresh", Toast.LENGTH_SHORT).show()
            }
        }
    }




   private fun fetchWeather(city: String, tvResult: TextView, progressBar: ProgressBar, swipeRefresh: SwipeRefreshLayout, fadeIn: android.view.animation.Animation ) {
          lifecycleScope.launch {
            val client = OkHttpClient()
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

 val request = Request.Builder().url(url).build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    progressBar.visibility = ProgressBar.GONE
                    tvResult.text = "Failed to fetch weather: ${e.message}"
                }
            }

            override fun onResponse(call: Call, response: Response) {
                response.body?.let { body ->
                    val data = JSONObject(body.string())
                    val main = data.getJSONObject("main")
                    val temp = main.getDouble("temp")
                    val humidity = main.getInt("humidity")
                    val desc = data.getJSONArray("weather").getJSONObject(0).getString("description")

                    val resultText = """
                        🌍 City: $city
                        🌡 Temperature: $temp°C
                        💧 Humidity: $humidity%
                        ☁️ Condition: $desc
                    """.trimIndent()

                    runOnUiThread {
                        progressBar.visibility = ProgressBar.GONE
                        swipeRefresh.isRefreshing = false
                         tvResult.text = resultText
                         tvResult.startAnimation(fadeIn)
                    }
                }
            }
        })
    }
}
