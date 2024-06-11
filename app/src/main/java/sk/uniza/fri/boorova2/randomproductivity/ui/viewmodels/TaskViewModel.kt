package sk.uniza.fri.boorova2.randomproductivity.ui.viewmodels

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import sk.uniza.fri.boorova2.randomproductivity.database.dao.TaskDao
import sk.uniza.fri.boorova2.randomproductivity.database.entities.TaskEntity
import sk.uniza.fri.boorova2.randomproductivity.util.NotificationWorker
import java.util.Calendar
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltViewModel
class TaskViewModel
    @Inject constructor(application: Application, private val taskDao: TaskDao) : AndroidViewModel(application) {

    val allTasks: LiveData<List<TaskEntity>> = taskDao.getAllTasks()

    private val _currentTask = MutableLiveData<TaskEntity?>()
    val currentTask: LiveData<TaskEntity?> get() = _currentTask

    fun selectTask(task: TaskEntity) {
        saveCurrentTask(task.id)
        _currentTask.value = task
    }

    fun clearCurrentTask() {
        saveCurrentTask(-1)
        _currentTask.value = null
    }

    fun addTask(task: TaskEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            taskDao.insertTask(task)
        }
    }

    private fun saveCurrentTask(taskId: Long) {
        val prefs = getApplication<Application>().getSharedPreferences("task_prefs", Context.MODE_PRIVATE)
        prefs.edit().putLong("current_task_id", taskId).apply()
    }

    fun loadCurrentTask() {
        val prefs = getApplication<Application>().getSharedPreferences("task_prefs", Context.MODE_PRIVATE)
        val taskId = prefs.getLong("current_task_id", -1)

        if (taskId != -1L) {
            viewModelScope.launch {
                val task = taskDao.getTaskById(taskId)
                _currentTask.value = task
            }
        }
    }

    fun removeTask(taskId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            taskDao.deleteTask(taskDao.getTaskById(taskId)!!)
        }
    }

    fun scheduleTaskNotification(context: Context, task: TaskEntity) {
        if (task.dueDate != null && task.notify) {
            val workManager = WorkManager.getInstance(context)

            val calendar = Calendar.getInstance().apply {
                time = task.dueDate
                set(Calendar.HOUR_OF_DAY, 14)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
            }

            val oneWeekBefore = calendar.timeInMillis - TimeUnit.DAYS.toMillis(7)
            val oneDayBefore = calendar.timeInMillis - TimeUnit.DAYS.toMillis(1)

            val data = workDataOf(
                "taskName" to task.name,
                "taskId" to task.id
            )

            if (oneWeekBefore > System.currentTimeMillis()) {
                val oneWeekRequest = OneTimeWorkRequestBuilder<NotificationWorker>()
                    .setInitialDelay(oneWeekBefore - System.currentTimeMillis(), TimeUnit.MILLISECONDS)
                    .setInputData(data)
                    .addTag(task.id.toString())
                    .build()
                workManager.enqueue(oneWeekRequest)
            }

            if (oneDayBefore > System.currentTimeMillis()) {
                val oneDayRequest = OneTimeWorkRequestBuilder<NotificationWorker>()
                    .setInitialDelay(oneDayBefore - System.currentTimeMillis(), TimeUnit.MILLISECONDS)
                    .setInputData(data)
                    .addTag(task.id.toString())
                    .build()
                workManager.enqueue(oneDayRequest)
            }
        }
    }

    fun cancelTaskNotifications(context: Context, taskId: Long) {
        WorkManager.getInstance(context).cancelAllWorkByTag(taskId.toString())
    }

}