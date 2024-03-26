package com.alirezasn80.learn_en.utill

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.os.Build

fun isOnline(context: Context): Boolean {
    var nc: NetworkCapabilities? = null
    val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val capabilities =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
        } else {
            val networks: Array<Network> = connectivityManager.allNetworks

            var i = 0
            while (i < networks.size && nc == null) {
                nc = connectivityManager.getNetworkCapabilities(networks[i])
                i++
            }
            nc

        }
    if (capabilities != null) {
        if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
            return true
        } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
            return true
        } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)) {

            return true
        }
    }
    return false
}