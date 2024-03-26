package com.alirezasn80.learn_en.di

import android.app.Application
import com.alirezasn80.learn_en.core.data.datastore.AppDataStore
import com.alirezasn80.learn_en.core.data.datastore.AppDataStoreManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ir.cafebazaar.poolakey.Payment
import ir.cafebazaar.poolakey.config.PaymentConfiguration
import ir.cafebazaar.poolakey.config.SecurityCheck
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ApplicationModule {

    @Singleton
    @Provides
    fun providePayment(application: Application): Payment {
        val rsaKey = ""

        val localSecurityCheck =
            SecurityCheck.Enable(rsaPublicKey = rsaKey)
        val paymentConfiguration = PaymentConfiguration(localSecurityCheck = localSecurityCheck)
        return Payment(context = application, config = paymentConfiguration)
    }

    @Singleton
    @Provides
    fun provideDataStoreManager(
        application: Application,
    ): AppDataStore = AppDataStoreManager(application)


}