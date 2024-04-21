package com.alirezasn80.learn_en.app

import android.app.Application
import com.alirezasn80.learn_en.core.data.datastore.AppDataStore
import com.alirezasn80.learn_en.utill.Key
import com.alirezasn80.learn_en.utill.User
import com.alirezasn80.learn_en.utill.debug
import dagger.hilt.android.HiltAndroidApp
import io.appmetrica.analytics.AppMetrica
import io.appmetrica.analytics.AppMetricaConfig
import io.appmetrica.analytics.push.AppMetricaPush
import ir.cafebazaar.poolakey.Payment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val METRICA_API = "a530bfed-31f4-466f-937a-638e8b7f1a47"
private const val METRICA_API_FAKE = "33dc1c30-fda5-17e8-9a1d-291f2806cb8e"

@HiltAndroidApp
class BaseApplication : Application() {

    @Inject
    lateinit var payment: Payment

    @Inject
    lateinit var dataStore: AppDataStore

    override fun onCreate() {
        super.onCreate()
        initAppMetrica()
    }


    private fun initAppMetrica() {
        val config = AppMetricaConfig.newConfigBuilder(/*if (DEBUG) METRICA_API_FAKE else */METRICA_API).build()
        AppMetrica.activate(this, config)
        AppMetrica.enableActivityAutoTracking(this)
        AppMetricaPush.activate(applicationContext)
    }
}