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
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import com.google.android.gms.tasks.Tasks

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var googleMap: GoogleMap
    private val db = FirebaseFirestore.getInstance()
    private val frameIds = (0..23).map { i -> "f" + i.toString().padStart(2, '0') }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map

        val losAngeles = LatLng(34.0522, -118.2437)
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(losAngeles, 8f))

        animateFrames()
    }

    private fun animateFrames() {
        lifecycleScope.launch {
            for (frame in frameIds) {
                drawFrame(frame)
                delay(1000) // 1 second delay between frames
            }
        }
    }

    private fun drawFrame(frameId: String) {
        val latDoc = db.collection(frameId).document("latitude").get()
        val lonDoc = db.collection(frameId).document("longitude").get()
        val densDoc = db.collection(frameId).document("mdens").get()

        Tasks.whenAllSuccess<DocumentSnapshot>(latDoc, lonDoc, densDoc)
            .addOnSuccessListener { results ->
                val latitudesMap = results[0].data ?: return@addOnSuccessListener
                val longitudesMap = results[1].data ?: return@addOnSuccessListener
                val densitiesMap = results[2].data ?: return@addOnSuccessListener

                // Clear previous frame
                googleMap.clear()

                var cellCount = 0

                for (rowKey in latitudesMap.keys) {
                    val latRow = latitudesMap[rowKey] as? List<*>
                    val lonRow = longitudesMap[rowKey] as? List<*>
                    val densRow = densitiesMap[rowKey] as? List<*>

                    if (latRow == null || lonRow == null || densRow == null) continue

                    val rowSize = minOf(latRow.size, lonRow.size, densRow.size)

                    for (i in 0 until rowSize) {
                        val lat = (latRow[i] as? Number)?.toDouble() ?: continue
                        val lonRaw = (lonRow[i] as? Number)?.toDouble() ?: continue
                        val density = (densRow[i] as? Number)?.toDouble() ?: continue

                        val lon = lonRaw - 360

                        if (lat !in 33.5..34.6 || lon !in -119.0..-117.0 || density < 1e-12) continue

                        drawCell(lat, lon, density)
                        cellCount++
                    }
                }

                Log.d("Animation", "Frame $frameId: Plotted $cellCount cells")
                Toast.makeText(this@MapsActivity, "Frame: $frameId", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Log.e("Animation", "Failed to load frame $frameId", it)
                Toast.makeText(this@MapsActivity, "Failed to load frame $frameId", Toast.LENGTH_LONG).show()
            }
    }

    private fun drawCell(lat: Double, lon: Double, density: Double) {
        val micrograms = density * 1e9
        val color = getColorFromAQI(micrograms)

        val latOffset = 0.027
        val lonOffset = 0.027

        val polygon = PolygonOptions()
            .add(
                LatLng(lat - latOffset / 2, lon - lonOffset / 2),
                LatLng(lat + latOffset / 2, lon - lonOffset / 2),
                LatLng(lat + latOffset / 2, lon + lonOffset / 2),
                LatLng(lat - latOffset / 2, lon + lonOffset / 2)
            )
            .strokeColor(Color.BLACK)
            .strokeWidth(0.5f)
            .fillColor(color)

        googleMap.addPolygon(polygon)
    }

    private fun getColorFromAQI(micrograms: Double): Int {
        return when {
            micrograms <= 12 -> Color.argb(100, 0, 228, 0)
            micrograms <= 35.4 -> Color.argb(100, 255, 255, 0)
            micrograms <= 55.4 -> Color.argb(100, 255, 126, 0)
            micrograms <= 150.4 -> Color.argb(100, 255, 0, 0)
            micrograms <= 250.4 -> Color.argb(100, 143, 63, 151)
            else -> Color.argb(100, 126, 0, 35)
        }
    }

    @Deprecated("This method has been deprecated in favor of using the\n      {@link OnBackPressedDispatcher} via {@link #getOnBackPressedDispatcher()}.\n      The OnBackPressedDispatcher controls how back button events are dispatched\n      to one or more {@link OnBackPressedCallback} objects.")
    override fun onBackPressed() {
        super.onBackPressed()
        // Prevent back press from exiting the activity
        // You can optionally add custom behavior if needed
        Log.d("MapsActivity", "Back button pressed but activity will stay open")
    }
}
