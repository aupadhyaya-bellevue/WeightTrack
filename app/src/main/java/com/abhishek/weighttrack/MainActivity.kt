package com.abhishek.weighttrack

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.abhishek.weighttrack.database.WeightTrackDBHelper
import com.abhishek.weighttrack.utility.ApplicationUtils
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textview.MaterialTextView
import java.util.Calendar

class MainActivity : AppCompatActivity() {

    private var appUtils: ApplicationUtils = ApplicationUtils()
    private lateinit var dbHelper: WeightTrackDBHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Initialize DB Helper
        dbHelper = WeightTrackDBHelper(this)
        var appSettings = dbHelper.readAppSettings()

        // If application settings are available, continue to home screen, else navigate to settings screen
        if(appSettings.settingsAvailable) {
            setContentView(R.layout.activity_main)

            // Add listeners for bottom navigation buttons
            val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
            bottomNavigationView.background = null
            bottomNavigationView.setOnItemSelectedListener { item: MenuItem ->
                when(item.itemId) {
                    R.id.miSettings -> navigateToSettings()
                    R.id.miHistory -> navigateToHistory()
                }
                return@setOnItemSelectedListener true
            }
            val floatingActionButton = findViewById<FloatingActionButton>(R.id.floatingActionBar)
            floatingActionButton.setOnClickListener {
                navigateToWeightEntry()
            }

            // Set the current date on the UI
            findViewById<MaterialTextView>(R.id.lblCurrentDate).text = appUtils.getStringFromDate(Calendar.getInstance().time, "MM/dd/yyyy")

            // Get weight history and application settings to populate all the metrics
            var weightHistory = dbHelper.findAllWeightEntries()

            weightHistory = weightHistory.sortedByDescending { it.date }

            // Get the latest weight and display on the UI
            val currentWeight: Float = if(weightHistory.isNotEmpty()) {
                weightHistory[0].weight
            } else {
                appSettings.beginningWeight
            }
            findViewById<MaterialTextView>(R.id.currentWeightValue).text = String.format("%.1f lbs.", currentWeight)

            // Calculate BMI and display on the UI
            if (appSettings != null) {
                findViewById<MaterialTextView>(R.id.bmiValue).text = String.format("%.1f", appUtils.calculateBMI(currentWeight, appSettings.height))
            } else {
                findViewById<MaterialTextView>(R.id.bmiValue).text = "N/A"
            }

            // Calculate weight loss and display on the UI
            if (appSettings != null) {
                findViewById<MaterialTextView>(R.id.weightLossToDateValue).text = String.format("%.1f lbs", currentWeight - appSettings.beginningWeight)
            } else {
                findViewById<MaterialTextView>(R.id.weightLossToDateValue).text = "N/A"
            }


            ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
                val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
                insets
            }
        } else run {
            // If settings are not set, navigate to the app settings screen
            navigateToSettings()
        }
    }

    private fun navigateToHistory() {
        val intent = Intent(this, WeightHistoryActivity::class.java)
        startActivity(intent)
    }

    private fun navigateToSettings() {
        val intent = Intent(this, SettingsActivity::class.java)
        startActivity(intent)
    }

    private fun navigateToWeightEntry() {
        val intent = Intent(this, WeightEntryActivity::class.java)
        startActivity(intent)
    }
}