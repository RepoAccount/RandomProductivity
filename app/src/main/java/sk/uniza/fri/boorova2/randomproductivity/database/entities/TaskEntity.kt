package sk.uniza.fri.boorova2.randomproductivity.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "tasks")
data class TaskEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "priority") val priority: Int = 1,
    @ColumnInfo(name = "hide_from_shuffler") val hideFromShuffler: Boolean = false,
    @ColumnInfo(name = "due_date") val dueDate: Date? = null,
    @ColumnInfo(name = "notify") val notify: Boolean = false,
    @ColumnInfo(name = "completion_count") val completionCount: Int = 0,
    @ColumnInfo(name = "completion_dates") val completionDates: List<Date> = emptyList(),
    @ColumnInfo(name = "goal_amount") val goalAmount: Int? = null,
    @ColumnInfo(name = "progress") val progress: Int = 0
)