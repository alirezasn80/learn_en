package com.alirezasn80.learn_en.di

import android.app.Application
import com.alirezasn80.learn_en.core.data.datastore.AppDataStore
import com.alirezasn80.learn_en.core.data.datastore.AppDataStoreManager
import com.alirezasn80.learn_en.utill.Myket
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ir.myket.billingclient.IabHelper
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ApplicationModule {

    @Singleton
    @Provides
    fun providePayment(application: Application): IabHelper {
        return IabHelper(
            application,
            Myket.PUBLIC_KEY
        )
    }

    @Singleton
    @Provides
    fun provideDataStoreManager(
        application: Application,
    ): AppDataStore = AppDataStoreManager(application)


}