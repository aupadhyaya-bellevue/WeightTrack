package com.abhishek.weighttrack.model

import android.graphics.Bitmap
import java.util.UUID

data class WeightEntry(val weight: Float = 0.00f, val date: Long = 0, val imageData: Bitmap? = null, val id: String = UUID.randomUUID().toString())
