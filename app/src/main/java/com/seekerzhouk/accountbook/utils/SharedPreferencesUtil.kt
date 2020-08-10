package com.seekerzhouk.accountbook.utils

import android.content.Context

object SharedPreferencesUtil {
    private const val TAG_SHP = "tag_shp"
    private const val FIRST_POSITION_KEY = "first_position"
    private const val SECOND_POSITION_KEY = "second_position"
    private const val ONCE_INSERT_KEY = "is_first_show"
    fun saveFirstPosition(context: Context, position: Int) {
        val editor = context.getSharedPreferences(TAG_SHP, Context.MODE_PRIVATE).edit()
        editor.putInt(FIRST_POSITION_KEY, position)
        editor.apply()
    }

    fun getFirstPosition(context: Context): Int {
        val sharedPreferences = context.getSharedPreferences(TAG_SHP, Context.MODE_PRIVATE)
        return sharedPreferences.getInt(FIRST_POSITION_KEY, 0)
    }

    fun saveSecondPosition(context: Context, secondTag: Int) {
        val editor = context.getSharedPreferences(TAG_SHP, Context.MODE_PRIVATE).edit()
        editor.putInt(SECOND_POSITION_KEY, secondTag)
        editor.apply()
    }

    fun getSecondPosition(context: Context): Int {
        val sharedPreferences = context.getSharedPreferences(TAG_SHP, Context.MODE_PRIVATE)
        return sharedPreferences.getInt(SECOND_POSITION_KEY, 0)
    }

    fun saveOnceInsert(context: Context, onceInsert: Boolean) {
        val editor = context.getSharedPreferences(TAG_SHP, Context.MODE_PRIVATE).edit()
        editor.putBoolean(ONCE_INSERT_KEY, onceInsert)
        editor.apply()
    }

    fun getOnceInsert(context: Context): Boolean {
        val sharedPreferences = context.getSharedPreferences(TAG_SHP, Context.MODE_PRIVATE)
        return sharedPreferences.getBoolean(ONCE_INSERT_KEY, false)
    }
}