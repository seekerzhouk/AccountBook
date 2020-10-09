package com.seekerzhouk.accountbook.utils

import android.content.Context

object SharedPreferencesUtil {

    private const val SHP_NAME = "tag_shp"

    private const val FIRST_POSITION_KEY = "first_position"

    private const val SECOND_POSITION_KEY = "second_position"

    /**
     * 用户未登陆过，本地无主数据表格是否已经创建
     */
    private const val UNREGISTERED_INSERT_KEY = "is_unregistered_insert"

    /**
     * 用户登陆过，用户本地数据表格是否已经创建
     */
    private const val LOGIN_INSERT_KEY = "is_login_insert"

    const val IS_LOGIN_KEY = "is_login"

    private const val USER_NAME_KEY = "save_user_name"

    fun saveFirstPosition(context: Context, position: Int) {
        saveValue(context,FIRST_POSITION_KEY,position)
    }

    fun getFirstPosition(context: Context): Int {
        return getValue(context,FIRST_POSITION_KEY, 0)
    }

    fun saveSecondPosition(context: Context, secondTag: Int) {
        saveValue(context,SECOND_POSITION_KEY,secondTag)
    }

    fun getSecondPosition(context: Context): Int {
        return getValue(context,SECOND_POSITION_KEY, 0)
    }

    fun saveUnregisteredInsert(context: Context, onceInsert: Boolean) {
        saveValue(context,UNREGISTERED_INSERT_KEY,onceInsert)
    }

    fun getUnregisteredInsert(context: Context): Boolean {
        return getValue(context,UNREGISTERED_INSERT_KEY, false)
    }

    fun saveLoginInsert(context: Context, onceInsert: Boolean) {
        saveValue(context,LOGIN_INSERT_KEY,onceInsert)
    }

    fun getLoginInsert(context: Context): Boolean {
        return getValue(context,LOGIN_INSERT_KEY, false)
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

    fun getUserName(context: Context):String {
        return getValue(context, USER_NAME_KEY, "")
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