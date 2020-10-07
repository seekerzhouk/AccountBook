package com.seekerzhouk.accountbook.ui.me

import android.app.Application
import androidx.lifecycle.*
import com.seekerzhouk.accountbook.utils.SharedPreferencesUtil

class MeViewModel(application: Application, private val handle: SavedStateHandle) :
    AndroidViewModel(application) {

    private val key = SharedPreferencesUtil.IS_LOGIN_KEY

    init {
        load()
    }

    private fun load() {
        handle.set(key, SharedPreferencesUtil.getIsLogin(getApplication()))
    }

    fun isLogin(): LiveData<Boolean> = handle.getLiveData(key)

    fun saveLogin(isLogin: Boolean) {
        handle.set(key, isLogin)
        SharedPreferencesUtil.saveIsLogin(getApplication(), isLogin)
    }
}