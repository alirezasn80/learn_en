package com.alirezasn80.learn_en.core.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.alirezasn80.learn_en.core.domain.entity.WordEntity
import com.alirezasn80.learn_en.core.domain.entity.WordImgEntity

@Dao
interface WordImgDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWordImg(wordImg: WordImgEntity)

    @Query("select url from WordImgEntity WHERE word = :word")
    suspend fun getDictImages(word: String): List<String>

}