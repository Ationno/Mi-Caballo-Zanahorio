package com.example.juegosdidacticos_limpiezadecaballo.utils

import androidx.room.TypeConverter
import java.util.Date

class Converters {
    @TypeConverter
    fun fromDate(date: Date?): Long? {
        return date?.time // Convert Date to Long (milliseconds since epoch)
    }

    @TypeConverter
    fun toDate(millisSinceEpoch: Long?): Date? {
        return millisSinceEpoch?.let { Date(it) } // Convert Long to Date
    }
}