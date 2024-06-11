package sk.uniza.fri.boorova2.randomproductivity.database.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import sk.uniza.fri.boorova2.randomproductivity.database.entities.TaskEntity
import java.util.Date

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

    @Query("SELECT * FROM tasks WHERE id = :taskId LIMIT 1")
    suspend fun getTaskById(taskId: Long): TaskEntity?

    @Query("UPDATE tasks SET completion_count = completion_count + 1," +
            "completion_dates = completion_dates, due_date = null || :date, " +
            "progress = CASE WHEN goal_amount IS NOT NULL THEN progress + 1 ELSE progress END WHERE id = :taskId")
    fun completeTask(taskId: Long, date: Date)

    @Query("UPDATE tasks SET goal_amount = :goalAmount, progress = 0 WHERE id = :taskId")
    fun setGoal(taskId: Long, goalAmount: Int)

    @Query("UPDATE tasks SET goal_amount = NULL, progress = 0 WHERE id = :taskId")
    fun resetGoal(taskId: Long)


}
