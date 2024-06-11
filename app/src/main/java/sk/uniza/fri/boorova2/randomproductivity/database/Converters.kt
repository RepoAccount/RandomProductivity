package sk.uniza.fri.boorova2.randomproductivity.database

import androidx.room.TypeConverter
import java.util.Date

class Converters {
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? = value?.let { Date(it) }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? = date?.time

    @TypeConverter
    fun fromDateList(value: List<Date>): String = value.joinToString(",") { it.time.toString() }

    @TypeConverter
    fun toDateList(value: String): List<Date> = value.split(",").mapNotNull { it.toLongOrNull()?.let { Date(it) } }
}