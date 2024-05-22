package com.abhishek.weighttrack.database

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import androidx.core.database.getStringOrNull
import com.abhishek.weighttrack.model.AppSettings
import com.abhishek.weighttrack.model.WeightEntry
import com.abhishek.weighttrack.utility.ApplicationUtils
import java.util.ArrayList
import java.util.Date

class WeightTrackDBHelper(context: Context): SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        const val DATABASE_VERSION = 1
        const val DATABASE_NAME = "WeightTrack.db"
        const val TABLE_SETTINGS = "app_settings"
        const val TABLE_WEIGHT_ENTRY = "weight_entry"

        // Application Settings columns
        const val COL_BEGINNING_WEIGHT = "beginningWeight"
        const val COL_START_DATE = "startDate"
        const val COL_TARGET_WEIGHT = "targetWeight"
        const val COL_TARGET_DATE = "targetDate"
        const val COL_GENDER = "gender"
        const val COL_HEIGHT = "height"
        const val COL_SETTINGS_AVAILABLE = "settingsAvailable"

        // Weight Entry columns
        const val COL_ID = "id"
        const val COL_WEIGHT = "weight"
        const val COL_ENTRY_DATE = "entry_date"
        const val COL_IMAGE_DATA = "image_data"

        const val DATE_FORMAT = "MM/dd/yyyy"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createAppSettingsTableQuery = "CREATE TABLE $TABLE_SETTINGS (" +
                "$COL_BEGINNING_WEIGHT REAL, " +
                "$COL_START_DATE TEXT, " +
                "$COL_TARGET_WEIGHT REAL, " +
                "$COL_TARGET_DATE TEXT, " +
                "$COL_GENDER TEXT, " +
                "$COL_HEIGHT REAL, " +
                "$COL_SETTINGS_AVAILABLE INTEGER)"

        val createWeightEntryTableQuery = "CREATE TABLE $TABLE_WEIGHT_ENTRY (" +
                "$COL_ID TEXT PRIMARY KEY, " +
                "$COL_WEIGHT REAL, " +
                "$COL_ENTRY_DATE TEXT, " +
                "$COL_IMAGE_DATA TEXT)"

        db.execSQL(createAppSettingsTableQuery)
        db.execSQL(createWeightEntryTableQuery)
    }

    fun updateAppSettings(appSettings: AppSettings) {
        val db = writableDatabase
        val appUtils = ApplicationUtils()

        var existingAppSettings = readAppSettings()

        val values = ContentValues()
        values.put(COL_BEGINNING_WEIGHT, appSettings.beginningWeight)
        values.put(COL_START_DATE, appUtils.getStringFromDate(appSettings.startDate, DATE_FORMAT))
        values.put(COL_TARGET_WEIGHT, appSettings.targetWeight)
        values.put(COL_TARGET_DATE, appUtils.getStringFromDate(appSettings.targetDate, DATE_FORMAT))
        values.put(COL_HEIGHT, appSettings.height)
        values.put(COL_GENDER, appSettings.gender)
        values.put(COL_SETTINGS_AVAILABLE, (if(appSettings.settingsAvailable) 1 else 0))

        if(existingAppSettings.settingsAvailable) {
            db.update(TABLE_SETTINGS, values, null, null)
        } else {
            db.insert(TABLE_SETTINGS, null, values)
        }
    }

    fun readAppSettings(): AppSettings {
        val db = readableDatabase
        var appSettings = AppSettings()

        val query = "SELECT * from $TABLE_SETTINGS"

        val cursor: Cursor = db.rawQuery(query, null)
        if(cursor.moveToFirst()) {
            val appUtils = ApplicationUtils()
            appSettings = appUtils.getAppSettings(cursor)
        }
        cursor.close()

        return appSettings
    }

    fun addWeightEntry(weightEntry: WeightEntry) {
        val db = writableDatabase
        val appUtils = ApplicationUtils()

        val values = ContentValues()
        values.put(COL_ID, weightEntry.id)
        values.put(COL_WEIGHT, weightEntry.weight)
        values.put(COL_IMAGE_DATA, appUtils.getStringFromBitmap(weightEntry.imageData))
        values.put(COL_ENTRY_DATE, appUtils.getStringFromDate(Date(weightEntry.date), DATE_FORMAT))

        db.insert(TABLE_WEIGHT_ENTRY, null, values)
    }

    fun findWeightEntry(id: String): WeightEntry {
        val db = readableDatabase
        var weightEntry = WeightEntry()

        val query = "SELECT * FROM $TABLE_WEIGHT_ENTRY WHERE $COL_ID = '$id'"
        val cursor: Cursor = db.rawQuery(query, null)

        if(cursor.moveToFirst()) {
            weightEntry = getWeightEntryFromTableRow(cursor)
        }
        cursor.close()

        return weightEntry
    }

    fun deleteWeightEntry(id: String) {
        val db = writableDatabase

        db.delete(TABLE_WEIGHT_ENTRY, "id = ?", arrayOf(id))
    }

    fun findAllWeightEntries(): List<WeightEntry> {
        val db = readableDatabase
        var weightEntries = ArrayList<WeightEntry>()

        val query = "SELECT * FROM $TABLE_WEIGHT_ENTRY"
        val cursor: Cursor = db.rawQuery(query, null)

        if(cursor.moveToFirst()) {
            weightEntries.add(getWeightEntryFromTableRow(cursor))

            while (cursor.moveToNext()) {
                weightEntries.add(getWeightEntryFromTableRow(cursor))
            }
        }
        cursor.close()

        return weightEntries
    }

    fun clearWeightHistory() {
        val db = writableDatabase

        val query = "DELETE from $TABLE_WEIGHT_ENTRY"
        db.execSQL(query)
    }

    @SuppressLint("Range")
    private fun getWeightEntryFromTableRow(cursor: Cursor): WeightEntry {
        val appUtils = ApplicationUtils()

        val weightEntryId = cursor.getString(cursor.getColumnIndex(WeightTrackDBHelper.COL_ID))
        val weight = cursor.getFloat(cursor.getColumnIndex(WeightTrackDBHelper.COL_WEIGHT))
        val imageData = appUtils.getBitmapFromString(
            cursor.getStringOrNull(
                cursor.getColumnIndex(WeightTrackDBHelper.COL_IMAGE_DATA)
            )
        )
        val entryDate = appUtils.getDateFromString(
            cursor.getString(
                cursor.getColumnIndex(WeightTrackDBHelper.COL_ENTRY_DATE)
            ), DATE_FORMAT
        ).time

        println("=========> entry date: ${Date(entryDate)}")

        return WeightEntry(weight, entryDate, imageData, weightEntryId)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        TODO("Not yet implemented")
    }
}