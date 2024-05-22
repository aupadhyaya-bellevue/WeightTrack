package com.abhishek.weighttrack

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.abhishek.weighttrack.database.WeightTrackDBHelper
import com.abhishek.weighttrack.model.AppSettings
import com.abhishek.weighttrack.utility.ApplicationUtils
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import com.google.android.material.textfield.TextInputLayout
import com.google.android.material.textview.MaterialTextView
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


class SettingsActivity : AppCompatActivity() {
    // Define all the UI components
    private lateinit var startDateTextView: MaterialTextView
    private lateinit var txtStartDateTextEdit: TextInputLayout
    private lateinit var txtBeginningWeight: TextInputLayout
    private lateinit var txtHeight: TextInputLayout
    private lateinit var txtGender: TextInputLayout
    private lateinit var txtTargetWeight: TextInputLayout
    private lateinit var txtTargetDate: TextInputLayout
    private lateinit var targetDateTextView: MaterialTextView
    private lateinit var btnSave: Button
    private val GENDERS = arrayOf("Male", "Female")

    private lateinit var dbHelper: WeightTrackDBHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContentView(R.layout.activity_settings)

        dbHelper = WeightTrackDBHelper(this)

        // Assign controls
        startDateTextView = findViewById(R.id.lblCalendar)
        txtStartDateTextEdit = findViewById(R.id.startDate)
        txtBeginningWeight = findViewById(R.id.beginningWeight)
        txtHeight = findViewById(R.id.txtHeight)
        txtGender = findViewById(R.id.txtGender)
        txtTargetWeight = findViewById(R.id.targetWeight)
        txtTargetDate = findViewById(R.id.targetDate)
        targetDateTextView = findViewById(R.id.lblTargetCalendar)
        btnSave = findViewById(R.id.btnSave)


        // Assign gender list
        (txtGender.editText as? MaterialAutoCompleteTextView)?.setSimpleItems(GENDERS)

        val calendar = Calendar.getInstance()

        // Set default dates for start date and target date
        updateStartDate(calendar)
        updateTargetDate(calendar)

        // Load settings data if available
        loadData()

        // Create date picker listeners
        val datePicker = DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
            calendar.set(Calendar.YEAR, year)
            calendar.set(Calendar.MONTH, month)
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)

            updateStartDate(calendar)
        }
        val targetDatePicker = DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
            calendar.set(Calendar.YEAR, year)
            calendar.set(Calendar.MONTH, month)
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)

            updateTargetDate(calendar)
        }

        // Open date picker for start and target dates
        startDateTextView.setOnClickListener {
            val datePickerDialog =
            DatePickerDialog(this, AlertDialog.THEME_DEVICE_DEFAULT_DARK, datePicker, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH))

            datePickerDialog.show()
        }
        targetDateTextView.setOnClickListener {
            val datePickerDialog =
                DatePickerDialog(this, AlertDialog.THEME_DEVICE_DEFAULT_DARK, targetDatePicker, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH))

            datePickerDialog.show()
        }

        // Save button action listener
        btnSave.setOnClickListener {
            var appSettings = AppSettings()
            val appUtils = ApplicationUtils()
            val format = "MM/dd/yyyy"

            appSettings.beginningWeight = txtBeginningWeight.editText?.getText().toString().toFloat()
            appSettings.startDate = appUtils.getDateFromString(txtStartDateTextEdit.editText?.getText().toString(), format)
            appSettings.targetWeight = txtTargetWeight.editText?.getText().toString().toFloat()
            appSettings.targetDate = appUtils.getDateFromString(txtTargetDate.editText?.getText().toString(), format)
            appSettings.gender = txtGender.editText?.getText().toString()
            appSettings.height = txtHeight.editText?.getText().toString().toFloat()
            appSettings.settingsAvailable = true

            dbHelper.updateAppSettings(appSettings)

            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.appSettings)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun updateStartDate(calendar: Calendar) {
        val dateFormat = "MM/dd/yyyy"
        val dateFormatter = SimpleDateFormat(dateFormat, Locale.US)
        txtStartDateTextEdit.editText?.setText(dateFormatter.format(calendar.time))
    }

    private fun updateTargetDate(calendar: Calendar) {
        val dateFormat = "MM/dd/yyyy"
        val dateFormatter = SimpleDateFormat(dateFormat, Locale.US)
        txtTargetDate.editText?.setText(dateFormatter.format(calendar.time))
    }

    private fun loadData() {
        val appSettings = dbHelper.readAppSettings()
        val dateFormat = "MM/dd/yyyy"
        val appUtils = ApplicationUtils()

        if(appSettings.settingsAvailable) {
            txtBeginningWeight.editText?.setText(appSettings.beginningWeight.toString())
            txtStartDateTextEdit.editText?.setText(appUtils.getStringFromDate(appSettings.startDate, dateFormat))
            txtTargetWeight.editText?.setText(appSettings.targetWeight.toString())
            txtTargetDate.editText?.setText(appUtils.getStringFromDate(appSettings.targetDate, dateFormat))
            val gender = appSettings.gender
            println("========> $gender")
            val selectedGenderPosition = if(gender.equals("Male"))  0 else 1
            println("========> $selectedGenderPosition")
            (txtGender.editText as? MaterialAutoCompleteTextView)?.setSimpleItems(GENDERS)
            (txtGender.editText as? MaterialAutoCompleteTextView)?.setSelection(selectedGenderPosition)
            txtHeight.editText?.setText(appSettings.height.toString())
        }
    }
}