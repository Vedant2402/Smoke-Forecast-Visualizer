package com.example.smokeforecastvisualizer

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.smokeforecastvisualizer.ui.theme.SmokeForecastVisualizerTheme
import kotlinx.coroutines.*
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

class WeatherSearchActivity : ComponentActivity() {
    private val TAG = "WeatherSearch"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SmokeForecastVisualizerTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    WeatherSearchScreen()
                }
            }
        }
    }

    @Composable
    fun WeatherSearchScreen() {
        var location by remember { mutableStateOf("") }
        var weather by remember { mutableStateOf<Current?>(null) }
        var error by remember { mutableStateOf<String?>(null) }
        val focusManager = LocalFocusManager.current

        val backgroundColor = getBackgroundColor(weather?.temp_c)

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(backgroundColor)
                .padding(24.dp),
            contentAlignment = Alignment.TopCenter
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Weather",
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 24.dp)
                )

                OutlinedTextField(
                    value = location,
                    onValueChange = { location = it },
                    placeholder = { Text("Enter location") },
                    keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    shape = RoundedCornerShape(12.dp)
                )

                Button(
                    onClick = {
                        focusManager.clearFocus()
                        fetchWeather(location.trim(), {
                            weather = it
                            error = null
                        }, {
                            weather = null
                            error = it
                        })
                    },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                ) {
                    Text("Get Weather")
                }

                error?.let {
                    Text(
                        text = it,
                        color = Color.Red,
                        modifier = Modifier.padding(top = 16.dp)
                    )
                }

                weather?.let { current ->
                    Spacer(modifier = Modifier.height(24.dp))

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        elevation = CardDefaults.cardElevation(8.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(location, fontWeight = FontWeight.Bold, fontSize = 22.sp)
                            Text("${current.temp_c}°C", fontSize = 20.sp, modifier = Modifier.padding(bottom = 16.dp))

                            Column {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    MetricTile("Humidity", "${current.humidity}%", Modifier.weight(1f))
                                    MetricTile("Wind", "${current.wind_kph} Km/h", Modifier.weight(1f))
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    MetricTile("Visibility", "${current.vis_km} km", Modifier.weight(1f))
                                    MetricTile("Precip", "${current.precip_mm} mm", Modifier.weight(1f))
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    MetricTile("UV", "${current.uv}", Modifier.weight(1f))
                                    MetricTile("PM2.5", "${current.air_quality.pm2_5 ?: "N/A"}", Modifier.weight(1f))
                                }
                            }

                        }
                    }
                }
            }
        }
    }

    @Composable
    fun MetricTile(label: String, value: String, modifier: Modifier = Modifier) {
        Column(
            modifier = modifier
                .background(Color.White, shape = RoundedCornerShape(8.dp))
                .padding(12.dp)
        ) {
            Text(text = label, fontWeight = FontWeight.SemiBold)
            Text(text = value, fontSize = 18.sp)
        }
    }


    private fun getBackgroundColor(temp: Double?): Color {
        return when {
            temp == null -> Color(0xFFF0F0F0)
            temp < 10 -> Color(0xFFB3E5FC) // cool blue
            temp < 25 -> Color(0xFFE1F5FE) // light blue
            temp < 35 -> Color(0xFFFFF9C4) // warm yellow
            else -> Color(0xFFFFCDD2)      // hot red
        }
    }

    private fun fetchWeather(
        location: String,
        onResult: (Current) -> Unit,
        onError: (String) -> Unit
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                Log.d(TAG, "🔑 API: ${BuildConfig.WEATHER_API_KEY}")
                val response = weatherApi.getCurrentWeather(
                    apiKey = BuildConfig.WEATHER_API_KEY,
                    location = location
                )
                withContext(Dispatchers.Main) {
                    onResult(response.current)
                }
            } catch (e: Exception) {
                Log.e(TAG, "❌ Error: ${e.message}", e)
                withContext(Dispatchers.Main) {
                    onError("Failed to fetch weather: ${e.message}")
                }
            }
        }
    }

    // Retrofit API and data classes
    interface WeatherApiService {
        @GET("current.json")
        suspend fun getCurrentWeather(
            @Query("key") apiKey: String,
            @Query("q") location: String,
            @Query("aqi") aqi: String = "yes"
        ): WeatherResponse
    }

    data class WeatherResponse(val current: Current)
    data class Current(
        val temp_c: Double,
        val humidity: Int,
        val wind_kph: Double,
        val vis_km: Double,
        val precip_mm: Double,
        val uv: Double,
        val air_quality: AirQuality
    )

    data class AirQuality(val pm2_5: Double?)

    private val weatherApi: WeatherApiService by lazy {
        Retrofit.Builder()
            .baseUrl("https://api.weatherapi.com/v1/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(WeatherApiService::class.java)
    }
}