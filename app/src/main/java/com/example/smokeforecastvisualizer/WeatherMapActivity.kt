package com.example.smokeforecastvisualizer


import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

class WeatherMapActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var googleMap: GoogleMap
    private val apiKey by lazy { BuildConfig.WEATHER_API_KEY }

    private val meteoApi by lazy {
        Retrofit.Builder()
            .baseUrl("https://api.open-meteo.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(OpenMeteoService::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_weather_map)

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map
        val center = LatLng(34.05, -118.25)
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(center, 9f))
        fetchTemperatureGrid()
    }

    private fun fetchTemperatureGrid() {
        lifecycleScope.launch(Dispatchers.IO) {
            val step = 0.01  // Adjust for resolution (smaller = denser)
            val latRange = 33.8..34.3
            val lonRange = -118.6..-117.9

            for (lat in latRange.step(step)) {
                for (lon in lonRange.step(step)) {
                    try {
                        val response = meteoApi.getTemperatureGrid(
                            latitude = lat,
                            longitude = lon,
                            hourly = "temperature_2m",
                            timezone = "America/Los_Angeles"
                        )

                        val temp = response.hourly.temperature_2m.firstOrNull() ?: continue

                        Log.d("OpenMeteo", "Fetched $lat,$lon: $temp°C")

                        withContext(Dispatchers.Main) {
                            drawTile(lat, lon, temp)
                        }

                    } catch (e: Exception) {
                        Log.e("OpenMeteo", "Failed @ $lat,$lon", e)
                    }
                }
            }

            withContext(Dispatchers.Main) {
                Toast.makeText(this@WeatherMapActivity, "Finished plotting all tiles", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun drawTile(lat: Double, lon: Double, temp: Double) {
        val offset = 0.05 // matches step size
        val color = getColorFromTemperature(temp)

        googleMap.addPolygon(
            PolygonOptions()
                .add(
                    LatLng(lat - offset / 2, lon - offset / 2),
                    LatLng(lat + offset / 2, lon - offset / 2),
                    LatLng(lat + offset / 2, lon + offset / 2),
                    LatLng(lat - offset / 2, lon + offset / 2)
                )
                .strokeColor(Color.BLACK)
                .strokeWidth(0.5f)
                .fillColor(color)
        )
    }




    private fun getColorFromTemperature(temp: Double): Int {
        return when {
            temp <= 5 -> Color.argb(100, 0, 0, 255)        // Blue
            temp <= 15 -> Color.argb(100, 0, 255, 0)       // Green
            temp <= 25 -> Color.argb(100, 255, 255, 0)     // Yellow
            temp <= 35 -> Color.argb(100, 255, 165, 0)     // Orange
            else -> Color.argb(100, 255, 0, 0)             // Red
        }
    }

    private fun ClosedFloatingPointRange<Double>.step(step: Double): Sequence<Double> =
        generateSequence(start) { prev ->
            if (prev + step <= endInclusive) prev + step else null
        }
}

val apiKey = BuildConfig.WEATHER_API_KEY