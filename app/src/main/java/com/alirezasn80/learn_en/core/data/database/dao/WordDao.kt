package com.alirezasn80.learn_en.core.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.alirezasn80.learn_en.core.domain.entity.WordEntity
import com.alirezasn80.learn_en.core.domain.entity.WordImgEntity

@Dao
interface WordDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWord(word: WordEntity)

    @Query("select * from WordEntity where word = :word")
    suspend fun getWordEntity(word: String): WordEntity?

    @Query("UPDATE WordEntity SET isHighlight = :isHighlight WHERE word = :word")
    suspend fun changeHighlightMode(isHighlight: Int, word: String)

    @Query("select word from WordEntity WHERE isHighlight = 1")
    suspend fun getHighlights(): List<String>

    //----------------------------------------

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWordImg(wordImg: WordImgEntity)

    @Query("select url from WordImgEntity WHERE word = :word")
    suspend fun getDictImages(word: String): List<String>

}