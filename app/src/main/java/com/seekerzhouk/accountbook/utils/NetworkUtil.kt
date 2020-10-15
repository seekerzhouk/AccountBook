package com.seekerzhouk.accountbook.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.widget.Toast
import com.seekerzhouk.accountbook.R

object NetworkUtil {
    fun isOnline(context: Context): Boolean {
        val connMgr = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo: NetworkInfo? = connMgr.activeNetworkInfo
        return networkInfo?.isConnected == true
    }

    fun doWithNetwork(context: Context,block: () -> Unit) {
        if (isOnline(context)) {
            run(block)
        } else {
            Toast.makeText(context, R.string.network_disconnected, Toast.LENGTH_SHORT).show()
        }
    }
}