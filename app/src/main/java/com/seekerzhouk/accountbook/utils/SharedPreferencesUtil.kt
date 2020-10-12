package com.seekerzhouk.accountbook.utils

import android.content.Context

object SharedPreferencesUtil {

    const val SHP_NAME = "tag_shp"

    private const val FIRST_POSITION_KEY = "first_position"

    private const val SECOND_POSITION_KEY = "second_position"

    /**
     * 用户未登陆过，本地无主数据表格是否已经初始化
     */
    private const val NO_OWNER_FORM_INIT_KEY = "no_owner_form_init"

    /**
     * 目前用户是否处于登陆状态。用于刷新Me界面。
     */
    const val IS_LOGIN_KEY = "is_login"

    /**
     * 只要登陆过，就能得到不是empty的userName，即使现在已经登出。此值可以知道用户在本应用是否登陆过。
     */
    private const val USER_NAME_KEY = "user_name"

    /**
     * 同步是否完成
     */
    const val HAS_SYNC_FINISHED = "sync_finished"

    /**
     * 是否需要同步
     */
    const val IS_NEED_SYNC = "is_need_sync"

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

    fun saveIsLogin(context: Context, isLogin: Boolean) {
        saveValue(context, IS_LOGIN_KEY, isLogin)
    }

    fun getIsLogin(context: Context): Boolean {
        return getValue(context, IS_LOGIN_KEY, false)
    }

    fun saveHasSyncFinished(context: Context, hasFinished: Boolean) {
        saveValue(context, HAS_SYNC_FINISHED, hasFinished)
    }

    fun getHasSyncFinished(context: Context): Boolean {
        return getValue(context, HAS_SYNC_FINISHED, false)
    }

    fun saveIsNeedSync(context: Context, needSync: Boolean) {
        saveValue(context, IS_NEED_SYNC, needSync)
    }

    fun getIsNeedSync(context: Context): Boolean {
        return getValue(context, IS_NEED_SYNC, false)
    }

    fun saveUserName(context: Context, userName: String) {
        saveValue(context, USER_NAME_KEY, userName)
    }

    fun getUserName(context: Context): String {
        return getValue(context, USER_NAME_KEY, "")
    }

    /**
     * 保存状态：具体用户的LeanCloud数据表格是否已经初始化
     * @param userName 用户名
     * @param hasInit 是否已经初始化
     */
    fun saveUserCloudFormStatus(context: Context, userName: String, hasInit: Boolean) {
        val key = "cloud_$userName"
        saveValue(context, key, hasInit)
    }

    /**
     * 获取状态：具体用户的LeanCloud数据表格是否已经初始化
     * @param userName 用户名
     * @return Boolean 是否已经初始化
     */
    fun getUserCloudFormStatus(context: Context, userName: String): Boolean {
        val key = "cloud_$userName"
        return getValue(context, key, false)
    }

    /**
     * 保存状态：具体用户的本地数据表格是否已经初始化
     * @param userName 用户名
     * @param hasInit 是否已经初始化
     */
    fun saveUserLocalFormStatus(context: Context, userName: String, hasInit: Boolean) {
        val key = "local_$userName"
        saveValue(context, key, hasInit)
    }

    /**
     * 获取状态：具体用户的本地数据表格是否已经初始化
     * @param userName 用户名
     * @return Boolean 是否已经初始化
     */
    fun getUserLocalFormStatus(context: Context, userName: String): Boolean {
        val key = "local_$userName"
        return getValue(context, key, false)
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
                else -> throw IllegalArgumentException("Unexpected type!")
            } as T
        }
    }
}