package com.alirezasn80.learn_en.core.data.database


import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.RoomDatabase
import com.alirezasn80.learn_en.core.data.database.dao.CategoryDao
import com.alirezasn80.learn_en.core.data.database.dao.ContentDao
import com.alirezasn80.learn_en.core.data.database.dao.WordDao
import com.alirezasn80.learn_en.core.data.database.dao.WordImgDao
import com.alirezasn80.learn_en.core.domain.entity.CategoryEntity
import com.alirezasn80.learn_en.core.domain.entity.ContentEntity
import com.alirezasn80.learn_en.core.domain.entity.WordEntity
import com.alirezasn80.learn_en.core.domain.entity.WordImgEntity

@Database(
    entities = [
        CategoryEntity::class,
        ContentEntity::class,
        WordEntity::class,
        WordImgEntity::class
    ],
    version = 3,
    autoMigrations = [
        AutoMigration(from = 1, to = 2),
        AutoMigration(from = 2, to = 3)
    ]
)
abstract class AppDB : RoomDatabase() {

    abstract val categoryDao: CategoryDao

    abstract val contentDao: ContentDao

    abstract val wordDao: WordDao

    abstract val wordImgDao: WordImgDao

}