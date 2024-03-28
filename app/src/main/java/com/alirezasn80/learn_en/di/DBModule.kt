package com.alirezasn80.learn_en.di

import android.app.Application
import androidx.room.Room
import com.alirezasn80.learn_en.core.data.database.AppDB
import com.alirezasn80.learn_en.utill.debug
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DBModule {

    @Singleton
    @Provides
    fun provideAppDatabase(app: Application): AppDB {
        val databaseFile = app.getDatabasePath("learn_en.db")

        return if (databaseFile.exists()) {
            debug("success create")
            Room.databaseBuilder(app, AppDB::class.java, "learn_en.db").build()

        } else {
            Room.databaseBuilder(app, AppDB::class.java, "learn_en.db")
                .createFromAsset("database/my_db.db")
                .build()
        }

    }
}

