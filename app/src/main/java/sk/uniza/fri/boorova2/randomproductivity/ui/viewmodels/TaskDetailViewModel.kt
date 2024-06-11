package sk.uniza.fri.boorova2.randomproductivity.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import sk.uniza.fri.boorova2.randomproductivity.database.dao.StatisticDao
import sk.uniza.fri.boorova2.randomproductivity.database.dao.TaskDao
import sk.uniza.fri.boorova2.randomproductivity.database.entities.StatisticEntity
import sk.uniza.fri.boorova2.randomproductivity.database.entities.TaskEntity
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class TaskDetailViewModel @Inject constructor(
    private val taskDao: TaskDao,
    private val statisticDao: StatisticDao
) : ViewModel() {

    private val _selectedTask = MutableLiveData<TaskEntity>()
    val selectedTask: LiveData<TaskEntity> get() = _selectedTask

    fun loadTask(taskId: Long) {
        viewModelScope.launch {
            _selectedTask.value = taskDao.getTaskById(taskId)
        }
    }


    fun completeTask(taskId: Long, timeSpent: Long = 0) {
        viewModelScope.launch(Dispatchers.IO) {
            taskDao.completeTask(taskId)
            statisticDao.insertStatistic(StatisticEntity(taskId = taskId, timestamp = Date(), timeSpent = timeSpent))
            loadTask(taskId)
        }
    }


    fun updateTaskDetails(task: TaskEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            taskDao.updateTask(task)
        }
    }

    fun resetGoal(taskId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            taskDao.resetGoal(taskId)
            loadTask(taskId)
        }
    }

}
