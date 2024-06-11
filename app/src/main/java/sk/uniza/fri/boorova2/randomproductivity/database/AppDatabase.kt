package sk.uniza.fri.boorova2.randomproductivity.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import sk.uniza.fri.boorova2.randomproductivity.database.dao.StatisticDao
import sk.uniza.fri.boorova2.randomproductivity.database.dao.TaskDao
import sk.uniza.fri.boorova2.randomproductivity.database.entities.StatisticEntity
import sk.uniza.fri.boorova2.randomproductivity.database.entities.TaskEntity
import kotlin.jvm.Volatile

@Suppress("unused")
@Database(entities = [TaskEntity::class, StatisticEntity::class], version = 3)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun taskDao(): TaskDao
    abstract fun statisticDao(): StatisticDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null
            fun getDatabase(context: Context): AppDatabase {
                // synchronized: one thread at a time
                return INSTANCE ?: synchronized(this) {
                    val instance = Room.databaseBuilder(
                        context.applicationContext,
                        AppDatabase::class.java,
                        "random_productivity_db"
                    ).fallbackToDestructiveMigration().build()
                    INSTANCE = instance
                    instance
                }
            }
    }
}
