package sk.uniza.fri.boorova2.randomproductivity.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "statistics")
data class StatisticEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "task_id") val taskId: Long,
    @ColumnInfo(name = "timestamp") val timestamp: Date,
    @ColumnInfo(name = "time_spent") val timeSpent: Long // min
)

