package com.abhishek.weighttrack

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.view.MenuItem
import android.widget.Button
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.abhishek.weighttrack.database.WeightTrackDBHelper
import com.abhishek.weighttrack.model.WeightEntry
import com.abhishek.weighttrack.utility.ApplicationUtils
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.textfield.TextInputLayout
import com.google.android.material.textview.MaterialTextView
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.ArrayList
import java.util.Calendar
import java.util.Locale

class WeightEntryActivity : AppCompatActivity() {
    private lateinit var lblCalendar: MaterialTextView
    private lateinit var txtDate: TextInputLayout
    private lateinit var txtWeight: TextInputLayout
    private lateinit var btnSave: Button
    private lateinit var btnImageCapture: Button
    private lateinit var imgWeightCapture: ImageView

    private var appUtils: ApplicationUtils = ApplicationUtils()
    private final val CAMERA_REQ_CODE:Int = 100
    private var imageData: Bitmap? = null

    private lateinit var dbHelper: WeightTrackDBHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_weight_entry)

        // Assign controls
        lblCalendar = findViewById(R.id.lblCalendar)
        txtDate = findViewById(R.id.txtDate)
        txtWeight = findViewById(R.id.txtWeight)
        btnSave = findViewById(R.id.btnSave)
        btnImageCapture = findViewById(R.id.btnCapture)
        imgWeightCapture = findViewById(R.id.weightCaptureImage)

        val calendar = Calendar.getInstance()

        dbHelper = WeightTrackDBHelper(this)

        // Set default dates for start date and target date
        updateDateLabel(calendar)

        // Create date picker listeners
        val datePicker = DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
            calendar.set(Calendar.YEAR, year)
            calendar.set(Calendar.MONTH, month)
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)

            updateDateLabel(calendar)
        }

        // Open date picker for start and target dates
        lblCalendar.setOnClickListener {
            val datePickerDialog =
                DatePickerDialog(this, AlertDialog.THEME_DEVICE_DEFAULT_DARK, datePicker, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH))

            datePickerDialog.show()
        }

        // Save button action listener
        btnSave.setOnClickListener {
            val weightValue = txtWeight.editText?.getText().toString().toFloat()
            val entryDate = txtDate.editText?.getText().toString()
            println("===========>Entry date: ${appUtils.getDateFromString(entryDate, "MM/dd/yyyy")}")
            val weightEntry = WeightEntry(weightValue, appUtils.getDateFromString(entryDate, "MM/dd/yyyy").time, imageData)
            dbHelper.addWeightEntry(weightEntry)

            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        btnImageCapture.setOnClickListener {
            var iCamera = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(iCamera, CAMERA_REQ_CODE)
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

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.weightEntryMenuBar)) { v, insets ->
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

    private fun updateDateLabel(calendar: Calendar) {
        val dateFormat = "MM/dd/yyyy"
        val dateFormatter = SimpleDateFormat(dateFormat, Locale.US)
        txtDate.editText?.setText(dateFormatter.format(calendar.time))
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == RESULT_OK) {
            if(requestCode == CAMERA_REQ_CODE) {
                imageData = data?.extras?.get("data") as Bitmap
                imgWeightCapture.setImageBitmap(imageData)
            }
        }
    }
}