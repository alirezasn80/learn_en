package com.alirezasn80.learn_en.core.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.alirezasn80.learn_en.core.data.database.dao.CategoryDao
import com.alirezasn80.learn_en.core.data.database.dao.BookDao
import com.alirezasn80.learn_en.core.data.database.dao.WordDao
import com.alirezasn80.learn_en.core.domain.entity.CategoryEntity
import com.alirezasn80.learn_en.core.domain.entity.BookEntity
import com.alirezasn80.learn_en.core.domain.entity.WordEntity

@Database(
    entities = [
        CategoryEntity::class,
        BookEntity::class,
        WordEntity::class
    ],
    version = 1
)
abstract class AppDB : RoomDatabase() {

    abstract val categoryDao: CategoryDao

    abstract val bookDao: BookDao

    abstract val wordDao: WordDao

}