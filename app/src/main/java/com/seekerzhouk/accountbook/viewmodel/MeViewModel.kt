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

    fun isLogin(): LiveData<Boolean> = handle.getLiveData(key)

    fun saveLogin(isLogin: Boolean) {
        handle.set(key, isLogin)
        SharedPreferencesUtil.saveIsLogin(getApplication(), isLogin)
    }

    fun cloudAndLocalDataInit() {
        myRepository.cloudAndLocalUserFormInit()
    }

    fun saveIsNeedSync(needSync: Boolean) {
        SharedPreferencesUtil.saveIsNeedSync(getApplication(), needSync)
    }

    // 每次切换到MeFragment都会重新创建fragment，userName不需要使用LiveData
    fun getUserName(): String {
        return SharedPreferencesUtil.getUserName(getApplication())
    }

    fun getPhoneNumber(): String {
        return SharedPreferencesUtil.getPhoneNumber(getApplication())
    }
}