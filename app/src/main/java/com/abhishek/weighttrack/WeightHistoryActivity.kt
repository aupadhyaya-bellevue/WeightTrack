package com.abhishek.weighttrack

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.abhishek.weighttrack.adapter.WeightHistoryAdapter
import com.abhishek.weighttrack.database.WeightTrackDBHelper
import com.abhishek.weighttrack.listeners.ItemSelectionListener
import com.abhishek.weighttrack.utility.ApplicationUtils
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton

class WeightHistoryActivity : AppCompatActivity(), ItemSelectionListener {

    private lateinit var weightHistoryRecyclerView: RecyclerView
    private lateinit var applicationUtils: ApplicationUtils

    private lateinit var dbHelper: WeightTrackDBHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_weight_history)

        applicationUtils = ApplicationUtils()

        dbHelper = WeightTrackDBHelper(this)

        val weightHistory = dbHelper.findAllWeightEntries()

        weightHistoryRecyclerView = findViewById(R.id.weightHistoryRecyclerView)
        weightHistoryRecyclerView.layoutManager = LinearLayoutManager(this)
        weightHistoryRecyclerView.setHasFixedSize(true)
        weightHistoryRecyclerView.adapter = WeightHistoryAdapter(weightHistory, this)

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        bottomNavigationView.background = null

        bottomNavigationView.setOnItemSelectedListener { item: MenuItem ->
            when(item.itemId) {
                R.id.miSettings -> navigateToSettings()
                R.id.miHome -> navigateToHome()
            }
            return@setOnItemSelectedListener true
        }
        val floatingActionButton = findViewById<FloatingActionButton>(R.id.floatingActionBar)
        floatingActionButton.setOnClickListener {
            navigateToWeightEntry()
        }


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.historyMenuBar)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun navigateToHome() {
        val intent = Intent(this, MainActivity::class.java)
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

    override fun onItemSelect(weightEntryId: String) {

        val intent = Intent(this, WeightDetailsActivity::class.java)
        intent.putExtra("weightEntryId", weightEntryId)
        startActivity(intent)
    }
}