package com.seekerzhouk.accountbook.viewmodel

import android.app.Application
import androidx.lifecycle.*
import com.seekerzhouk.accountbook.repository.MyRepository
import com.seekerzhouk.accountbook.utils.SharedPreferencesUtil

class MeViewModel(application: Application, private val handle: SavedStateHandle) :
    AndroidViewModel(application) {

    private val key = SharedPreferencesUtil.IS_LOGIN_KEY

    private val myRepository: MyRepository =
        MyRepository.getInstance(application)

    init {
        load()
    }

    private fun load() {
        handle.set(key, SharedPreferencesUtil.getIsLogin(getApplication()))
    }

    fun getIsLogin(): LiveData<Boolean> = handle.getLiveData(key)

    fun saveIsLogin(isLogin: Boolean) {
        handle.set(key, isLogin)
        SharedPreferencesUtil.saveIsLogin(getApplication(), isLogin)
    }

    fun cloudAndLocalDataInit() {
        myRepository.cloudAndLocalUserFormInit()
    }

    fun saveIsNeedSync(needSync: Boolean) {
        SharedPreferencesUtil.saveIsNeedSync(getApplication(), needSync)
    }

    fun getUserName(): String {
        return SharedPreferencesUtil.getUserName(getApplication())
    }

    fun getPhoneNumber(): String {
        return SharedPreferencesUtil.getPhoneNumber(getApplication())
    }
}