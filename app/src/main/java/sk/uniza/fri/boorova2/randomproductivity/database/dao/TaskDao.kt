package sk.uniza.fri.boorova2.randomproductivity.database.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import sk.uniza.fri.boorova2.randomproductivity.database.entities.TaskEntity

@Dao
interface TaskDao {
    @Query("SELECT * FROM tasks")
    fun getAllTasks(): LiveData<List<TaskEntity>>

    @Insert
    fun insertTask(task: TaskEntity)

    @Delete
    fun deleteTask(task: TaskEntity)

    @Update
    fun updateTask(task: TaskEntity)
}
