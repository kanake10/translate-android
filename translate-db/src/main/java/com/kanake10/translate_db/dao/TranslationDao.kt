package com.kanake10.translate_db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.kanake10.translate_db.entities.TranslationEntity
import kotlinx.coroutines.flow.Flow
@Dao
interface TranslationDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTranslation(translationEntity: TranslationEntity)

    @Query("SELECT * FROM translations ORDER BY timestamp DESC")
    fun getAllTranslations(): Flow<List<TranslationEntity>>

    @Query("DELETE FROM translations")
    suspend fun clearAllTranslations()

    @Delete
    suspend fun delete(translationEntity: TranslationEntity)
}