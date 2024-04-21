package com.display.photos.util

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.widget.Toast

object Utils {

    /**
     * to check whether network connection is there or not.
     * @param context the context
     *
     * @return true if network exists otherwise returns false
     */
    fun hasInternetConnection(context: Context?): Boolean {
        try {
            context?.let {
                val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE)
                        as ConnectivityManager
                val activeNetwork = connectivityManager.activeNetwork ?: return false
                val networkCapabilities =
                    connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false
                return when {
                    networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                    networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                    networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                    else -> false
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return false
    }

    /**
     * To show a toast message
     */
    fun displayToast(context: Context, message: String) {
        Toast.makeText(
            context,
            message,
            Toast.LENGTH_SHORT
        ).show()
    }
}