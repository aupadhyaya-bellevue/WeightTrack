package com.abhishek.weighttrack

import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.Button
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.DialogFragment
import com.abhishek.weighttrack.database.WeightTrackDBHelper
import com.abhishek.weighttrack.model.WeightEntry
import com.abhishek.weighttrack.utility.ApplicationUtils
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.TextInputLayout
import java.util.Date

class WeightDetailsActivity : AppCompatActivity() {
    private lateinit var txtDate: TextInputLayout
    private lateinit var txtWeight: TextInputLayout
    private lateinit var btnBackToHistory: Button
    private lateinit var btnDelete: Button
    private lateinit var imgWeightCapture: ImageView

    private lateinit var dbHelper: WeightTrackDBHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_weight_details)

        txtDate = findViewById(R.id.txtDate)
        txtWeight = findViewById(R.id.txtWeight)
        btnBackToHistory = findViewById(R.id.btnBackToHistory)
        btnDelete = findViewById(R.id.btnDelete)
        imgWeightCapture = findViewById(R.id.weightCaptureImage)

        dbHelper = WeightTrackDBHelper(this)

        val weightEntryId = intent.getStringExtra("weightEntryId")

        this.addOnNewIntentListener() {
            this.intent = it
        }

        var weightEntry = WeightEntry()
        if(!weightEntryId.isNullOrEmpty()) {
            weightEntry = dbHelper.findWeightEntry(weightEntryId)

            val applicationUtils = ApplicationUtils()

            txtDate.editText?.setText(applicationUtils.getStringFromDate(Date(weightEntry.date),"MM/dd/yyyy"))
            val weight = weightEntry.weight.toString() + " lbs."
            txtWeight.editText?.setText(weight)
            imgWeightCapture.setImageBitmap(weightEntry.imageData)
        } else {
            navigateToHistory()
        }

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

        btnBackToHistory.setOnClickListener {
            navigateToHistory()
        }

        btnDelete.setOnClickListener {
            val builder: AlertDialog.Builder = AlertDialog.Builder(this)
            builder
                .setMessage("Are you sure you want to delete the entry?")
                .setTitle("Delete Entry")
                .setPositiveButton("Yes") { dialog, id ->
                    dbHelper.deleteWeightEntry(weightEntry.id)
                    navigateToHistory()
                }
                .setNegativeButton("No") { dialog, id ->
                    // DO nothing
                }

            val dialog: AlertDialog = builder.create()
            dialog.show()
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.weightDetailsContent)) { v, insets ->
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

    private fun navigateToHistory() {
        val intent = Intent(this, WeightHistoryActivity::class.java)
        startActivity(intent)
    }

    class StartGameDialogFragment : DialogFragment() {
        override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
            return activity?.let {
                val builder = AlertDialog.Builder(it)
                builder.setMessage("Are you sure you want to delete the entry?")
                    .setPositiveButton("Yes") { dialog, id ->

                    }
                    .setNegativeButton("No") { dialog, id ->
                        // User cancelled the dialog.
                    }
                // Create the AlertDialog object and return it.
                builder.create()
            } ?: throw IllegalStateException("Activity cannot be null")
        }
    }
}