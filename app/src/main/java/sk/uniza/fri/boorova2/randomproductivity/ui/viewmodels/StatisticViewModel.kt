package sk.uniza.fri.boorova2.randomproductivity.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import dagger.hilt.android.lifecycle.HiltViewModel
import sk.uniza.fri.boorova2.randomproductivity.database.dao.StatisticDao
// import sk.uniza.fri.boorova2.randomproductivity.database.entities.StatisticEntity
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class StatisticViewModel @Inject constructor(private val statisticDao: StatisticDao) : ViewModel() {
    /*fun getStatistics(taskId: Long): LiveData<List<StatisticEntity>> {
        return statisticDao.getStatisticsByTaskId(taskId)
    }*/

    fun getWeekCompletionCount(taskId: Long): LiveData<Map<String, Long>> {
        val oneWeekAgo = Calendar.getInstance().apply {
            add(Calendar.DAY_OF_YEAR, -7)
        }.time

        return statisticDao.getStatisticsByTaskId(taskId).map { stats ->
            val allDays = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
            val weeklyStats = stats.filter { it.timestamp.after(oneWeekAgo) }
                .groupBy { SimpleDateFormat("EEE", Locale.getDefault()).format(it.timestamp) }
                .mapValues { entry -> entry.value.size.toLong() }

            allDays.associateWith { weeklyStats[it] ?: 0L }
        }
    }

    fun getWeekTimeSpent(taskId: Long): LiveData<Map<String, Long>> {
        val oneWeekAgo = Calendar.getInstance().apply {
            add(Calendar.DAY_OF_YEAR, -7)
        }.time

        return statisticDao.getStatisticsByTaskId(taskId).map { stats ->
            val allDays = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
            val weeklyStats = stats.filter { it.timestamp.after(oneWeekAgo) }
                .groupBy { SimpleDateFormat("EEE", Locale.getDefault()).format(it.timestamp) }
                .mapValues { entry -> entry.value.sumOf { it.timeSpent } }

            allDays.associateWith { weeklyStats[it] ?: 0L }
        }
    }

    /*fun getAllStatistics(): LiveData<List<StatisticEntity>> {
        return statisticDao.getAllStatistics()
    }*/

}
