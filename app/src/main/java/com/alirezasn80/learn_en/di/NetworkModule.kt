package com.alirezasn80.learn_en.di


import com.alirezasn80.learn_en.core.data.service.ApiService
import com.alirezasn80.learn_en.utill.DEBUG
import com.alirezasn80.learn_en.utill.logging
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Protocol
import okhttp3.Request
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit


import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Singleton
    @Provides
    fun provideGsonBuilder(): Gson = GsonBuilder()
        .serializeNulls()
        .create()


    @Provides
    @Singleton
    fun provideOkClient() =
        OkHttpClient.Builder()
            .apply {
                if (DEBUG) {
                    addInterceptor(logging())
                }
            }
            .addInterceptor(Interceptor { chain ->
                val newRequest: Request = chain.request().newBuilder()
                    .header("Content-Type", "application/json")
                    .build()
                chain.proceed(newRequest)
            })
            .connectTimeout(7, TimeUnit.MINUTES)
            .writeTimeout(7, TimeUnit.MINUTES)
            .readTimeout(7, TimeUnit.MINUTES)
            .protocols(listOf(Protocol.HTTP_1_1))
            .build()

    @Singleton
    @Provides
    fun provideRetrofitBuilder(gsonBuilder: Gson, okHttpClient: OkHttpClient): Retrofit =
        Retrofit.Builder()
            .baseUrl("https://alirezasn80.ir/api/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gsonBuilder))
            .build()

    @Singleton
    @Provides
    fun provideApiService(retrofit: Retrofit): ApiService =
        retrofit.create(ApiService::class.java)
}
