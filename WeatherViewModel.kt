package com.example.weatherapp.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.data.WeatherRepository
import com.example.weatherapp.data.local.WeatherEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class WeatherViewModel(private val repository: WeatherRepository) : ViewModel() {

    private val _weather = MutableStateFlow<WeatherEntity?>(null)
    val weather: StateFlow<WeatherEntity?> = _weather

    fun fetchWeather(city: String, apiKey: String) {
        viewModelScope.launch {
            _weather.value = repository.getWeather(city, apiKey)
        }
    }
}
