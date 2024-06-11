package sk.uniza.fri.boorova2.randomproductivity.di

import android.app.Application
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import sk.uniza.fri.boorova2.randomproductivity.database.AppDatabase
import sk.uniza.fri.boorova2.randomproductivity.database.dao.StatisticDao
import sk.uniza.fri.boorova2.randomproductivity.database.dao.TaskDao
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideDatabase(app: Application): AppDatabase {
        return Room.databaseBuilder(app, AppDatabase::class.java, "task_db").build()
    }

    @Provides
    @Singleton
    fun provideTaskDao(db: AppDatabase): TaskDao {
        return db.taskDao()
    }

    @Provides
    @Singleton
    fun provideStatisticDao(db: AppDatabase): StatisticDao {
        return db.statisticDao()
    }
}