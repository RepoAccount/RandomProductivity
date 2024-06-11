package sk.uniza.fri.boorova2.randomproductivity.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import sk.uniza.fri.boorova2.randomproductivity.database.dao.TaskDao
import sk.uniza.fri.boorova2.randomproductivity.database.entities.TaskEntity
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class TaskDetailViewModel @Inject constructor(private val taskDao: TaskDao) : ViewModel() {
    private val _selectedTask = MutableLiveData<TaskEntity>()
    val selectedTask: LiveData<TaskEntity> get() = _selectedTask

    fun loadTask(taskId: Long) {
        viewModelScope.launch {
            _selectedTask.value = taskDao.getTaskById(taskId)
        }
    }

    fun completeTask(taskId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            taskDao.completeTask(taskId, Date())
            loadTask(taskId)
        }
    }

    fun updateTaskDetails(task: TaskEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            taskDao.updateTask(task)
        }
    }

    /*fun resetGoal(taskId: Long) {
        viewModelScope.launch {
            taskDao.resetGoal(taskId)
            loadTask(taskId) // Refresh task data
        }
    }*/

}
