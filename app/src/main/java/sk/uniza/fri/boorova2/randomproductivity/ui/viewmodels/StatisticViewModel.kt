package sk.uniza.fri.boorova2.randomproductivity.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import dagger.hilt.android.lifecycle.HiltViewModel
import sk.uniza.fri.boorova2.randomproductivity.database.dao.StatisticDao
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class StatisticViewModel @Inject constructor(private val statisticDao: StatisticDao) : ViewModel() {

    fun getCompletionByTask(since: Date): LiveData<Map<Long, Long>> {
        return statisticDao.getAllStatistics().map { stats ->
            val filteredStats = stats.filter { it.timestamp.after(since) }
                .groupBy { it.taskId }
                .mapValues { entry -> entry.value.size.toLong() }

            filteredStats
        }
    }

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

    fun getMonthlyTaskCompletionPercentage(): LiveData<Map<Long, Float>> {
        val oneMonthAgo = Calendar.getInstance().apply {
            add(Calendar.MONTH, -1)
        }.time

        return statisticDao.getAllStatistics().map { stats ->
            val monthlyStats = stats.filter { it.timestamp.after(oneMonthAgo) }
            val totalTasks = monthlyStats.size.toFloat()

            monthlyStats.groupBy { it.taskId }
                .mapValues { entry -> (entry.value.size / totalTasks) * 100 }
        }
    }

    fun getBestTaskAndDay(taskNames: Map<Long, String>): LiveData<Pair<String, String>> {
        val oneMonthAgo = Calendar.getInstance().apply {
            add(Calendar.MONTH, -1)
        }.time

        return statisticDao.getAllStatistics().map { stats ->
            val monthlyStats = stats.filter { it.timestamp.after(oneMonthAgo) }

            val bestTask = monthlyStats.groupBy { it.taskId }
                .maxByOrNull { it.value.size }?.let { entry ->
                    taskNames[entry.key] + ", " + entry.value.size
                } ?: "None"

            val bestDay = monthlyStats.groupBy { SimpleDateFormat("EEE", Locale.getDefault()).format(it.timestamp) }
                .maxByOrNull { it.value.size }?.let { entry ->
                    entry.key + ", " + entry.value.size
                } ?: "None"

            Pair(bestTask, bestDay)
        }
    }

    fun getMonthlyTimeSpentByTask(): LiveData<Map<Long, Long>> {
        val oneMonthAgo = Calendar.getInstance().apply { add(Calendar.MONTH, -1) }.time

        return statisticDao.getAllStatistics().map { stats ->
            stats.filter { it.timestamp.after(oneMonthAgo) }
                .groupBy { it.taskId }
                .mapValues { entry -> entry.value.sumOf { it.timeSpent } }
        }
    }



}
