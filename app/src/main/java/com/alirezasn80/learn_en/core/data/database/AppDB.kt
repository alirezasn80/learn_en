package com.alirezasn80.learn_en.core.data.database


import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.RoomDatabase
import com.alirezasn80.learn_en.core.data.database.dao.CategoryDao
import com.alirezasn80.learn_en.core.data.database.dao.ContentDao
import com.alirezasn80.learn_en.core.data.database.dao.WordDao
import com.alirezasn80.learn_en.core.domain.entity.CategoryEntity
import com.alirezasn80.learn_en.core.domain.entity.ContentEntity
import com.alirezasn80.learn_en.core.domain.entity.WordEntity

@Database(
    entities = [CategoryEntity::class, ContentEntity::class, WordEntity::class],
    version = 2,
    autoMigrations = [AutoMigration(from = 1, to = 2)]
)
abstract class AppDB : RoomDatabase() {

    abstract val categoryDao: CategoryDao

    abstract val contentDao: ContentDao

    abstract val wordDao: WordDao

}