package sk.uniza.fri.boorova2.randomproductivity.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import sk.uniza.fri.boorova2.randomproductivity.database.dao.TaskDao
import sk.uniza.fri.boorova2.randomproductivity.database.entities.TaskEntity
import javax.inject.Inject

@HiltViewModel
class TaskViewModel
    @Inject constructor(private val taskDao: TaskDao) : ViewModel() {

    val allTasks: LiveData<List<TaskEntity>> = taskDao.getAllTasks()

    fun addTask(task: TaskEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            taskDao.insertTask(task)
        }
    }

    fun updateTask(task: TaskEntity) {
        viewModelScope.launch {
            taskDao.updateTask(task)
        }
    }

    fun deleteTask(task: TaskEntity) {
        viewModelScope.launch {
            taskDao.deleteTask(task)
        }
    }

}