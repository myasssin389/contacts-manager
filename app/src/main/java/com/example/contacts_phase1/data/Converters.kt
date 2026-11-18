package com.example.contacts_phase1.data

import androidx.room.TypeConverter
import java.time.LocalDate

/** Room cannot store a LocalDate directly, so birthdays are saved as a day number. */
class Converters {

    @TypeConverter
    fun fromEpochDay(value: Long?): LocalDate? = value?.let { LocalDate.ofEpochDay(it) }

    @TypeConverter
    fun toEpochDay(date: LocalDate?): Long? = date?.toEpochDay()
}
