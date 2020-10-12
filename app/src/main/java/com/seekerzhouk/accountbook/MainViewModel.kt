package com.seekerzhouk.accountbook

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.SavedStateHandle
import com.seekerzhouk.accountbook.database.MyRepository
import com.seekerzhouk.accountbook.utils.SharedPreferencesUtil

class MainViewModel(application: Application, private val handle: SavedStateHandle) :
    AndroidViewModel(application), SharedPreferences.OnSharedPreferenceChangeListener {
    private val shpKey = SharedPreferencesUtil.IS_LOGIN_KEY
    private val shpName = SharedPreferencesUtil.SHP_NAME
    private val myRepository: MyRepository =
        MyRepository.getInstance(application)

    init {
        application.getSharedPreferences(shpName, Context.MODE_PRIVATE)
            .registerOnSharedPreferenceChangeListener(this)
        load()
    }

    private fun load() {
        handle.set(shpKey, SharedPreferencesUtil.getIsLogin(getApplication()))
    }

    fun isLogin(): LiveData<Boolean> = handle.getLiveData(shpKey)

    fun syncData() {
        myRepository.syncData()
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        if (shpKey == key) {
            load()
        }
    }
}