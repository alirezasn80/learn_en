package com.alirezasn80.learn_en.core.data.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.alirezasn80.learn_en.core.domain.entity.CategoryEntity


@Dao
interface CategoryDao {

    @Delete
    suspend fun deleteCategory(category: CategoryEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategory(category: CategoryEntity)

    @Query("UPDATE CATEGORYENTITY SET title = :title WHERE categoryId = :categoryId")
    fun updateCategory(categoryId: Int, title: String)

    @Query("SELECT * FROM CATEGORYENTITY")
    suspend fun getCategories(): List<CategoryEntity>

    @Query("SELECT * FROM CATEGORYENTITY WHERE type = :type")
    suspend fun getCategories(type: String): List<CategoryEntity>

}