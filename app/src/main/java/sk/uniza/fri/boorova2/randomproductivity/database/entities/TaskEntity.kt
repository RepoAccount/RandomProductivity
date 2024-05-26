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
    @ColumnInfo(name = "due_date") val dueDate: Date? = null,
    @ColumnInfo(name = "is_completed") val isCompleted: Boolean = false
)