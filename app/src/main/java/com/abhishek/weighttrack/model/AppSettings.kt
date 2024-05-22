package com.abhishek.weighttrack.model

import java.util.Calendar
import java.util.Date

data class AppSettings(
    var beginningWeight: Float = 0.00f,
    var startDate: Date = Calendar.getInstance().time,
    var height: Float = 0.00f,
    var gender: String = "",
    var targetWeight: Float = 0.00f,
    var targetDate: Date = Calendar.getInstance().time,
    var settingsAvailable: Boolean = false
)