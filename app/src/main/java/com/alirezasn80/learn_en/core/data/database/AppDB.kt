package com.alirezasn80.learn_en.core.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.alirezasn80.learn_en.core.data.database.dao.CategoryDao
import com.alirezasn80.learn_en.core.data.database.dao.ContentDao
import com.alirezasn80.learn_en.core.domain.entity.CategoryEntity
import com.alirezasn80.learn_en.core.domain.entity.ContentEntity

@Database(
    entities = [CategoryEntity::class, ContentEntity::class],
    version = 1
)
abstract class AppDB : RoomDatabase() {

    abstract val categoryDao: CategoryDao

    abstract val contentDao: ContentDao

}