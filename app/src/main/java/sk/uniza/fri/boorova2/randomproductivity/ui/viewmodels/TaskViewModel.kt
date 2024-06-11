package sk.uniza.fri.boorova2.randomproductivity.ui.viewmodels

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import sk.uniza.fri.boorova2.randomproductivity.database.dao.TaskDao
import sk.uniza.fri.boorova2.randomproductivity.database.entities.TaskEntity
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
}