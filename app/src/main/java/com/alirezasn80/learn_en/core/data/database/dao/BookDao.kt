package com.alirezasn80.learn_en.core.data.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.alirezasn80.learn_en.core.domain.entity.BookEntity

@Dao
interface BookDao {

    @Delete
    suspend fun deleteContent(content: BookEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertContent(bookEntity: BookEntity)

    @Update
    suspend fun updateContent(bookEntity: BookEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    //@Query("UPDATE BookEntity SET isFavorite = 1 WHERE bookId = :bookId AND categoryId = :categoryId")
    suspend fun addToFavorite(bookEntity: BookEntity)

    @Query("UPDATE BookEntity SET isFavorite = 0 WHERE bookId = :bookId AND categoryId = :categoryId")
    suspend fun deleteFromFavorite(bookId: Int, categoryId: Int)

    @Query("SELECT * FROM BookEntity WHERE isFavorite = 1")
    suspend fun getFavoriteBooks(): List<BookEntity>
}