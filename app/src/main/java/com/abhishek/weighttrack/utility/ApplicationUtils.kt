package com.abhishek.weighttrack.utility

import android.annotation.SuppressLint
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.abhishek.weighttrack.database.WeightTrackDBHelper
import com.abhishek.weighttrack.model.AppSettings
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.Base64
import java.util.Calendar
import java.util.Date
import java.util.Locale


class ApplicationUtils {
    private val format = "MM/dd/yyyy"

    fun getDateFromString(value: String = "", format: String): Date  {
        val dateFormatter = SimpleDateFormat(format, Locale.US)
        try {
            return dateFormatter.parse(value)
        }catch (e: Exception) {
            return Calendar.getInstance().time
        }
    }

    fun getStringFromDate(value: Date, format: String): String  {
        val dateFormatter = SimpleDateFormat(format, Locale.US)
        try {
            return dateFormatter.format(value)
        }catch (e: Exception) {
            return dateFormatter.format(Calendar.getInstance().time)
        }
    }

    @SuppressLint("Range")
    fun getAppSettings(cursor: Cursor): AppSettings {
        val settingsAvailable: Boolean = if(cursor.getInt(cursor.getColumnIndex(WeightTrackDBHelper.COL_SETTINGS_AVAILABLE)) == 1)  true else false
        if(settingsAvailable) {
            val beginningWeight = cursor.getFloat(cursor.getColumnIndex(WeightTrackDBHelper.COL_BEGINNING_WEIGHT))
            val startDateStr = cursor.getString(cursor.getColumnIndex(WeightTrackDBHelper.COL_START_DATE))
            val targetWeight = cursor.getFloat(cursor.getColumnIndex(WeightTrackDBHelper.COL_TARGET_WEIGHT))
            val targetDateStr = cursor.getString(cursor.getColumnIndex(WeightTrackDBHelper.COL_TARGET_DATE))
            var gender = cursor.getString(cursor.getColumnIndex(WeightTrackDBHelper.COL_GENDER))
            val height = cursor.getFloat(cursor.getColumnIndex(WeightTrackDBHelper.COL_HEIGHT))
            return AppSettings(beginningWeight,
                getDateFromString(startDateStr.toString(), format),
                height,
                gender,
                targetWeight,
                getDateFromString(targetDateStr.toString(), format),
                true)
        }

        return AppSettings();
    }

    fun calculateBMI(weight: Float, height: Float): Double {
        return (weight/Math.pow(height.toDouble(), 2.0) * 703)
    }

    fun getStringFromBitmap(imageData: Bitmap?): String? {
        val COMPRESSION_QUALITY = 100
        var encodedImage: String? = null
        val byteArrayBitmapStream = ByteArrayOutputStream()
        if (imageData != null) {
            imageData.compress(
                Bitmap.CompressFormat.PNG, COMPRESSION_QUALITY,
                byteArrayBitmapStream
            )
            val b = byteArrayBitmapStream.toByteArray()
            encodedImage = Base64.getEncoder().encodeToString(b)
            return encodedImage
        }
        return null
    }

    fun getBitmapFromString(imageData: String?): Bitmap? {
        if (imageData != null) {
            val decodedString: ByteArray =
                Base64.getDecoder().decode(imageData)
            return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
        }

        return null;
    }
}