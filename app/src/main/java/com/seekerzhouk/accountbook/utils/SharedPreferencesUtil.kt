package com.seekerzhouk.accountbook.utils

import android.content.Context

object SharedPreferencesUtil {

    private const val SHP_NAME = "tag_shp"

    private const val FIRST_POSITION_KEY = "first_position"

    private const val SECOND_POSITION_KEY = "second_position"

    /**
     * 用户未登陆过，本地无主数据表格是否已经初始化
     */
    private const val NO_OWNER_FORM_INIT_KEY = "no_owner_form_init"

    /**
     * 用户登陆过，用户本地数据表格是否已经初始化
     */
    private const val LOGIN_USER_FORM_INIT_KEY = "login_user_form_init"

    /**
     * 目前用户是否处于登陆状态。用于刷新Me界面。
     */
    const val IS_LOGIN_KEY = "is_login"

    /**
     * 只要登陆过，就能得到不是empty的userName，即使现在已经登出。此值可以知道用户在本应用是否登陆过。
     */
    private const val USER_NAME_KEY = "user_name"

    /**
     * 记录一下用户云端数据表格是否成功初始化过
     */
    private const val CLOUD_USER_FORM_INIT = "cloud_user_form_init"

    fun saveFirstPosition(context: Context, position: Int) {
        saveValue(context, FIRST_POSITION_KEY, position)
    }

    fun getFirstPosition(context: Context): Int {
        return getValue(context, FIRST_POSITION_KEY, 0)
    }

    fun saveSecondPosition(context: Context, secondTag: Int) {
        saveValue(context, SECOND_POSITION_KEY, secondTag)
    }

    fun getSecondPosition(context: Context): Int {
        return getValue(context, SECOND_POSITION_KEY, 0)
    }

    fun saveNoOwnerFormHasInit(context: Context, onceInsert: Boolean) {
        saveValue(context, NO_OWNER_FORM_INIT_KEY, onceInsert)
    }

    fun getNoOwnerFormHasInit(context: Context): Boolean {
        return getValue(context, NO_OWNER_FORM_INIT_KEY, false)
    }

    fun saveLoginUserFormHasInit(context: Context, onceInsert: Boolean) {
        saveValue(context, LOGIN_USER_FORM_INIT_KEY, onceInsert)
    }

    fun getLoginUserFormHasInit(context: Context): Boolean {
        return getValue(context, LOGIN_USER_FORM_INIT_KEY, false)
    }

    fun saveIsLogin(context: Context, isLogin: Boolean) {
        saveValue(context, IS_LOGIN_KEY, isLogin)
    }

    fun getIsLogin(context: Context): Boolean {
        return getValue(context, IS_LOGIN_KEY, false)
    }

    fun saveUserName(context: Context, userName: String) {
        saveValue(context, USER_NAME_KEY, userName)
    }

    fun getUserName(context: Context): String {
        return getValue(context, USER_NAME_KEY, "")
    }

    fun saveHasCloudFormInit(context: Context, hasInit: Boolean) {
        saveValue(context, CLOUD_USER_FORM_INIT, hasInit)
    }

    fun getHasCloudFormInit(context: Context): Boolean {
        return getValue(context, CLOUD_USER_FORM_INIT, false)
    }


    private fun <T> saveValue(context: Context, key: String, value: T) {
        val editor = context.getSharedPreferences(SHP_NAME, Context.MODE_PRIVATE).edit()
        with(editor) {
            when (value) {
                is Long -> putLong(key, value)
                is String -> putString(key, value)
                is Int -> putInt(key, value)
                is Boolean -> putBoolean(key, value)
                is Float -> putFloat(key, value)
                else -> throw IllegalArgumentException("This type of data cannot be saved!")
            }
        }.apply()
    }

    @Suppress("UNCHECKED_CAST")
    private fun <T> getValue(context: Context, key: String, defValue: T): T {
        val shp = context.getSharedPreferences(SHP_NAME, Context.MODE_PRIVATE)
        with(shp) {
            return when (defValue) {
                is Long -> getLong(key, defValue)
                is String -> getString(key, defValue)
                is Int -> getInt(key, defValue)
                is Boolean -> getBoolean(key, defValue)
                is Float -> getFloat(key, defValue)
                else -> throw IllegalArgumentException("This type of data cannot be saved!")
            } as T
        }
    }
}