package com.alirezasn80.learn_en.core.data.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.alirezasn80.learn_en.core.domain.entity.ContentEntity
import com.alirezasn80.learn_en.core.domain.entity.Items


@Dao
interface ContentDao {

    @Delete
    suspend fun deleteContent(content: ContentEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertContent(content: ContentEntity)

    @Update
    suspend fun updateContent(contentEntity: ContentEntity)

    @Query("UPDATE CONTENTENTITY SET favorite = 1 WHERE contentId = :contentId AND categoryId = :categoryId")
    suspend fun addToBookmark(contentId: Int, categoryId: Int)

    @Query("UPDATE CONTENTENTITY SET favorite = 0 WHERE contentId = :contentId AND categoryId = :categoryId")
    suspend fun deleteFromBookmark(contentId: Int, categoryId: Int)

    @Query("SELECT categoryId,contentId,title FROM CONTENTENTITY WHERE categoryId = :categoryId")
    suspend fun getItems(categoryId: Int): List<Items>

    @Query("SELECT categoryId,contentId,title FROM CONTENTENTITY WHERE favorite = 1")
    suspend fun getFavorites(): List<Items>

    @Query("SELECT * FROM CONTENTENTITY WHERE categoryId = :categoryId AND contentId = :contentId")
    suspend fun getContent(categoryId: Int, contentId: Int): ContentEntity

}