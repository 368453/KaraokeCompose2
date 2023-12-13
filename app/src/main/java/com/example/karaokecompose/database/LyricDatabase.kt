package com.example.karaoke.db.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Lyric::class], version = 1, exportSchema = false)
abstract class LyricDatabase : RoomDatabase() {
    abstract fun lyricDao(): LyricDao

    companion object {
        @Volatile
        private var Instance: LyricDatabase? = null

        fun getDatabase(context: Context): LyricDatabase {
            return Instance ?: synchronized(this) {
                Instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    LyricDatabase::class.java,
                    "lyric_database"
                ).fallbackToDestructiveMigration().build().also{ Instance = it }
            }
        }
    }
}

