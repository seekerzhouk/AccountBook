package com.seekerzhouk.accountbook.utils

import android.content.Context

object SharedPreferencesUtil {
    const val SHP_NAME = "tag_shp"
    private const val FIRST_POSITION_KEY = "first_position"
    private const val SECOND_POSITION_KEY = "second_position"
    private const val ONCE_INSERT_KEY = "is_first_show"
    const val IS_LOGIN_KEY = "is_login"
    fun saveFirstPosition(context: Context, position: Int) {
        val editor = context.getSharedPreferences(SHP_NAME, Context.MODE_PRIVATE).edit()
        editor.putInt(FIRST_POSITION_KEY, position)
        editor.apply()
    }

    fun getFirstPosition(context: Context): Int {
        val sharedPreferences = context.getSharedPreferences(SHP_NAME, Context.MODE_PRIVATE)
        return sharedPreferences.getInt(FIRST_POSITION_KEY, 0)
    }

    fun saveSecondPosition(context: Context, secondTag: Int) {
        val editor = context.getSharedPreferences(SHP_NAME, Context.MODE_PRIVATE).edit()
        editor.putInt(SECOND_POSITION_KEY, secondTag)
        editor.apply()
    }

    fun getSecondPosition(context: Context): Int {
        val sharedPreferences = context.getSharedPreferences(SHP_NAME, Context.MODE_PRIVATE)
        return sharedPreferences.getInt(SECOND_POSITION_KEY, 0)
    }

    fun saveOnceInsert(context: Context, onceInsert: Boolean) {
        val editor = context.getSharedPreferences(SHP_NAME, Context.MODE_PRIVATE).edit()
        editor.putBoolean(ONCE_INSERT_KEY, onceInsert)
        editor.apply()
    }

    fun getOnceInsert(context: Context): Boolean {
        val sharedPreferences = context.getSharedPreferences(SHP_NAME, Context.MODE_PRIVATE)
        return sharedPreferences.getBoolean(ONCE_INSERT_KEY, false)
    }

    fun saveIsLogin(context: Context, onceInsert: Boolean) {
        val editor = context.getSharedPreferences(SHP_NAME, Context.MODE_PRIVATE).edit()
        editor.putBoolean(IS_LOGIN_KEY, onceInsert)
        editor.apply()
    }

    fun getIsLogin(context: Context): Boolean {
        val sharedPreferences = context.getSharedPreferences(SHP_NAME, Context.MODE_PRIVATE)
        return sharedPreferences.getBoolean(IS_LOGIN_KEY, false)
    }
}