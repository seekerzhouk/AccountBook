package com.seekerzhouk.accountbook.utils

import android.util.Log
import com.seekerzhouk.accountbook.BuildConfig

object MyLog {

    private const val pTag = "MyLog : "
    fun v(tag: String, msg: String) {
        if (BuildConfig.DEBUG) {
            Log.v(pTag + tag, msg)
        }
    }

    fun d(tag: String, msg: String) {
        if (BuildConfig.DEBUG) {
            Log.v(pTag + tag, msg)
        }
    }

    fun i(tag: String, msg: String) {
        if (BuildConfig.DEBUG) {
            Log.i(pTag + tag, msg)
        }
    }

    fun i(tag: String, msg: String, e: Throwable) {
        if (BuildConfig.DEBUG) {
            Log.i(pTag + tag, msg, e)
        }
    }

    fun w(tag: String, msg: String) {
        if (BuildConfig.DEBUG) {
            Log.w(pTag + tag, msg)
        }
    }

    fun e(tag: String, msg: String) {
        if (BuildConfig.DEBUG) {
            Log.e(pTag + tag, msg)
        }
    }
}