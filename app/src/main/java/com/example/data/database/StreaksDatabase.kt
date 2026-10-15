package com.example.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.dao.HabitDao
import com.example.data.dao.HabitLogDao
import com.example.data.model.Habit
import com.example.data.model.HabitLog

@Database(
    entities = [Habit::class, HabitLog::class],
    version = 1,
    exportSchema = false
)
abstract class StreaksDatabase : RoomDatabase() {
    abstract fun habitDao(): HabitDao
    abstract fun habitLogDao(): HabitLogDao

    companion object {
        @Volatile
        private var INSTANCE: StreaksDatabase? = null

        fun getDatabase(context: Context): StreaksDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    StreaksDatabase::class.java,
                    "streaks_plus_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
