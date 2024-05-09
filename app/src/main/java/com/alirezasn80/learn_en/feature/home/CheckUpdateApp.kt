package com.alirezasn80.learn_en.feature.home

import android.content.ComponentName
import android.content.ServiceConnection
import android.os.IBinder
import com.farsitel.bazaar.IUpdateCheckService

interface CheckUpdateAppListener {
    fun needUpdate(value: Boolean)
}

internal class CheckUpdateApp(private val listener: CheckUpdateAppListener) : ServiceConnection {
    private var service: IUpdateCheckService? = null


    override fun onServiceConnected(name: ComponentName, boundService: IBinder) {
        service = IUpdateCheckService.Stub.asInterface(boundService)

        try {
            val versionCode: Long? = service?.getVersionCode("com.alirezasn80.eitaacleaner")
            versionCode?.let {
                listener.needUpdate(it != -1L)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    override fun onServiceDisconnected(name: ComponentName) {
        service = null
    }
}