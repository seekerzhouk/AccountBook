package com.seekerzhouk.accountbook.ui.me

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.seekerzhouk.accountbook.utils.SharedPreferencesUtil

class LoginViewModel(application: Application) :
    AndroidViewModel(application){

    fun saveLogin(isLogin: Boolean) {
        SharedPreferencesUtil.saveIsLogin(getApplication(), isLogin)
    }

    fun saveIsNeedSync(needSync: Boolean) {
        SharedPreferencesUtil.saveIsNeedSync(getApplication(), needSync)
    }

    fun saveUserName(userName: String) {
        SharedPreferencesUtil.saveUserName(getApplication(),userName)
    }

    fun saveFirstPosition(position: Int) {
        SharedPreferencesUtil.saveFirstPosition(getApplication(),position)
    }

    fun saveSecondPosition(position: Int) {
        SharedPreferencesUtil.saveSecondPosition(getApplication(),position)
    }
}