package com.kanake10.translate_db.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.kanake10.translate_db.dao.TranslationDao
import com.kanake10.translate_db.entities.TranslationEntity

@Database(entities = [TranslationEntity::class], version = 1, exportSchema = true)
abstract class TranslateDatabase : RoomDatabase() {
    abstract fun translationDao(): TranslationDao

    companion object {
        @Volatile
        private var INSTANCE: TranslateDatabase? = null

        fun getInstance(context: Context): TranslateDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    TranslateDatabase::class.java,
                    "translateplus_db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}