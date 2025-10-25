package com.example.weatherapp.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.weatherapp.viewmodel.WeatherViewModel
import com.example.weatherapp.viewmodel.WeatherViewModelFactory
import com.example.weatherapp.data.WeatherRepository
import com.example.weatherapp.data.WeatherDatabase
import com.example.weatherapp.data.WeatherApiService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeatherScreen() {
    val context = LocalContext.current

    // --- Initialize Retrofit and Room manually ---
    val retrofit = Retrofit.Builder()
        .baseUrl("https://api.openweathermap.org/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val api = retrofit.create(WeatherApiService::class.java)
    val dao = WeatherDatabase.getDatabase(context).weatherDao()
    val repository = WeatherRepository(api, dao)

    val viewModel: WeatherViewModel = viewModel(
        factory = WeatherViewModelFactory(repository)
    )

    var city by remember { mutableStateOf(TextFieldValue("")) }
    val weather by viewModel.weatherState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "🌤️ Weather Tracker",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = city,
            onValueChange = { city = it },
            label = { Text("Enter City") }
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                if (city.text.isNotEmpty()) {
                    viewModel.fetchWeather(city.text, "YOUR_API_KEY")
                }
            }
        ) {
            Text("Check Weather")
        }

        Spacer(modifier = Modifier.height(20.dp))

        weather?.let {
            Text("City: ${it.city}", fontWeight = FontWeight.Bold)
            Text("Temperature: ${it.temperature}°C")
            Text("Condition: ${it.description}")
            Text("Humidity: ${it.humidity}%")
        }
    }
}
