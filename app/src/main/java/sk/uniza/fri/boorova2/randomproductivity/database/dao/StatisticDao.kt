package sk.uniza.fri.boorova2.randomproductivity.database.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import sk.uniza.fri.boorova2.randomproductivity.database.entities.StatisticEntity

@Dao
interface StatisticDao {
    @Query("SELECT * FROM statistics")
    fun getAllStatistics(): LiveData<List<StatisticEntity>>

    @Insert
    fun insertStatistic(statistic: StatisticEntity)

    @Delete
    fun deleteStatistic(statistic: StatisticEntity)

    @Query("SELECT * FROM statistics WHERE task_id = :taskId")
    fun getStatisticsByTaskId(taskId: Long): LiveData<List<StatisticEntity>>

}
