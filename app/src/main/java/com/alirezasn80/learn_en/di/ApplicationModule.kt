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
        val rsaKey =
            "MIHNMA0GCSqGSIb3DQEBAQUAA4G7ADCBtwKBrwCmMVUPbJvRLn7HmfNX6D0xDg5CLEIA9kgUjDkKtCR+jjuvAFnHLyxkgcO2mVZusRFtXbOzvPYxpAHu74MO4+uXTSkc2nCIYi+od6TTTrb1qS8IQ5BGyQ/etySb7a8sUn68zt6YTutS61qMJztxXxUjDaQcpKKJ3PwP7IG0fJKw6KLpJ6pDLmpoNGctENgo1+qc0V5HcpDLnC6Ao2POJ2dbsBXI+z6ZsZIdMeUqdnkCAwEAAQ=="

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