/*
 * Copyright 2026 Ezra Kanake
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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