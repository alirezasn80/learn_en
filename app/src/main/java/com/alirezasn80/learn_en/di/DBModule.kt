package com.alirezasn80.learn_en.di

import android.app.Application
import androidx.room.Room
import com.alirezasn80.learn_en.core.data.database.AppDB
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

        return Room
            .databaseBuilder(
                context = app,
                klass = AppDB::class.java,
                name = "learn_en2.db"
            )
            .build()
    }
}

