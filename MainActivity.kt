package com.example.weatherapp

import android.os.Bundle
import android.view.animation.AnimationUtils
import android.widget.*
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.activity.ComponentActivity
import androidx.lifecycle.lifecycleScope
import com.example.weatherapp.databinding.ActivityMainBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.URL
import com.example.weatherapp.ui.WeatherScreen
import androidx.activity.compose.setContent

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
    
// Sound feedback (soft click)
val sound = android.media.ToneGenerator(android.media.AudioManager.STREAM_NOTIFICATION, 80)
sound.startTone(android.media.ToneGenerator.TONE_PROP_ACK, 150)


    
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


// Assume weatherCondition holds the main weather type like "Rain", "Clear", "Clouds", etc.
val weatherCondition = weatherResponse.weather[0].main 

// Initialize vibration & sound
val vibrator = getSystemService(VIBRATOR_SERVICE) as android.os.Vibrator
val toneGen = android.media.ToneGenerator(android.media.AudioManager.STREAM_NOTIFICATION, 100)

when (weatherCondition) {
    "Thunderstorm" -> {
        // Strong vibration + long alert tone
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            vibrator.vibrate(android.os.VibrationEffect.createOneShot(600, android.os.VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(600)
        }
        toneGen.startTone(android.media.ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD, 400)
    }

    "Rain" -> {
        // Short vibration + soft tone
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            vibrator.vibrate(android.os.VibrationEffect.createOneShot(200, android.os.VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(200)
        }
        toneGen.startTone(android.media.ToneGenerator.TONE_PROP_BEEP, 200)
    }

    "Clear" -> {
        // Gentle click + cheerful tone
        toneGen.startTone(android.media.ToneGenerator.TONE_PROP_ACK, 150)
    }

    "Clouds" -> {
        // Mild buzz + neutral tone
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            vibrator.vibrate(android.os.VibrationEffect.createOneShot(100, android.os.VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(100)
        }
        toneGen.startTone(android.media.ToneGenerator.TONE_PROP_BEEP2, 200)
    }

    else -> {
        // Default gentle click
        toneGen.startTone(android.media.ToneGenerator.TONE_PROP_ACK, 100)
    }
}


private fun updateBackground(weatherCondition: String) {
    val imageView = findViewById<ImageView>(R.id.bgImage)
    val backgroundRes = when {
        weatherCondition.contains("rain", ignoreCase = true) -> R.drawable.rainy_bg
        weatherCondition.contains("cloud", ignoreCase = true) -> R.drawable.cloudy_bg
        weatherCondition.contains("snow", ignoreCase = true) -> R.drawable.snow_bg
        weatherCondition.contains("sun", ignoreCase = true) || weatherCondition.contains("clear", ignoreCase = true) -> R.drawable.sunny_bg
        else -> R.drawable.default_bg
    }
    imageView.setImageResource(backgroundRes)
}

updateBackground(weatherCondition)


   // Apply fade animation
    val fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in)
    imageView.setImageResource(backgroundRes)
    imageView.startAnimation(fadeIn)
}

updateBackground(weatherCondition)

private fun updateBackground(condition: String) {
    val layout = findViewById<View>(R.id.mainLayout)
    val backgroundRes = when {
        condition.contains("rain", true) -> R.drawable.bg_rainy
        condition.contains("cloud", true) -> R.drawable.bg_cloudy
        condition.contains("sun", true) || condition.contains("clear", true) -> R.drawable.bg_sunny
        else -> R.drawable.bg_default
    }
    layout.setBackgroundResource(backgroundRes)
}

binding.textViewWeather.text = "Condition: $condition"
updateBackground(condition)
