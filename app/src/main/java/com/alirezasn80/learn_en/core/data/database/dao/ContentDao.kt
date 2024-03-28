package com.alirezasn80.learn_en.core.data.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.alirezasn80.learn_en.core.domain.entity.CategoryEntity
import com.alirezasn80.learn_en.core.domain.entity.ContentEntity


@Dao
interface ContentDao {

    @Delete
    suspend fun deleteContent(content: ContentEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertContent(content: ContentEntity)

    @Update
    fun updateContent(content: ContentEntity)


    @Query("SELECT * FROM CONTENTENTITY WHERE categoryId = :categoryId")
    suspend fun getContents(categoryId: Int): List<ContentEntity>

}