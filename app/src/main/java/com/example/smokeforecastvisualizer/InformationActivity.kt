package com.example.smokeforecastvisualizer

import android.os.Bundle
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import com.example.smokeforecastvisualizer.databinding.ActivityInformationBinding

class InformationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityInformationBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize View Binding
        binding = ActivityInformationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Setup toolbar with back button
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        // Create a SpannableStringBuilder for rich text styling
        val content = SpannableStringBuilder()

        // Title
        val title = "Air Quality Index (AQI) Basics\n\n"
        content.append(title)
        content.setSpan(ForegroundColorSpan(0xFF2E8B57.toInt()), 0, title.length, 0) // Green color

        // What is the U.S. AQI
        val section1 = "What is the U.S. Air Quality Index (AQI)?\n"
        content.append(section1)
        content.setSpan(StyleSpan(Typeface.BOLD), content.length - section1.length, content.length, 0)
        content.append("The U.S. AQI is the EPA's index for reporting air quality. It provides an easy way to communicate the level of air pollution and its health risks.\n\n")

        // How does AQI work
        val section2 = "How does the AQI work?\n"
        content.append(section2)
        content.setSpan(StyleSpan(Typeface.BOLD), content.length - section2.length, content.length, 0)
        content.append("The AQI is a scale for reporting air quality, divided into six color-coded categories, each corresponding to a range of pollution levels.\n\n")

        // Table header
        content.append("AQI Levels\n")
        content.setSpan(StyleSpan(Typeface.BOLD), content.length - "AQI Levels\n".length, content.length, 0)
        content.append("Color-coded levels that help identify air quality levels:\n\n")

        // AQI Table: instead of HTML, use formatted list or manual structure
        val levels = listOf(
            "Green - Good (0-50): Air quality is satisfactory, and air pollution poses little or no risk.",
            "Yellow - Moderate (51-100): Air quality is acceptable, but there may be a risk for sensitive groups.",
            "Orange - Unhealthy for Sensitive Groups (101-150): Sensitive groups may experience health effects.",
            "Red - Unhealthy (151-200): Health effects possible for the general public.",
            "Purple - Very Unhealthy (201-300): Health alert: The risk of health effects is increased for everyone.",
            "Maroon - Hazardous (301+): Health warning of emergency conditions: everyone is more likely to be affected."
        )

        // Add AQI levels
        levels.forEach { level ->
            content.append(level + "\n")
            content.setSpan(ForegroundColorSpan(0xFF000000.toInt()), content.length - level.length, content.length, 0) // Black text color
            content.append("\n")
        }

        // Major Pollutants
        val section3 = "Major Pollutants\n"
        content.append(section3)
        content.setSpan(StyleSpan(Typeface.BOLD), content.length - section3.length, content.length, 0)
        content.append("Here are the five major pollutants that are monitored to determine the AQI:\n")

        // Pollutants
        val pollutants = listOf(
            "Ground-level ozone: Can lead to respiratory issues.",
            "Particle pollution (PM2.5 & PM10): Tiny particles that can penetrate deep into the lungs.",
            "Carbon monoxide: A harmful, colorless, odorless gas.",
            "Sulfur dioxide: A gas that irritates the respiratory system.",
            "Nitrogen dioxide: A gas that contributes to respiratory problems."
        )

        pollutants.forEach { pollutant ->
            content.append("• $pollutant\n")
        }

        // Health Tips
        val section4 = "\nTips for Protecting Your Health\n"
        content.append(section4)
        content.setSpan(StyleSpan(Typeface.BOLD), content.length - section4.length, content.length, 0)
        content.append("When AQI levels are unhealthy, sensitive individuals should limit outdoor activities.\n")

        // Display the final formatted content
        binding.contentTextView.text = content
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
