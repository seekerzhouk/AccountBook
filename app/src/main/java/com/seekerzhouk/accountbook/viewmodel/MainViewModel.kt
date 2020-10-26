package com.seekerzhouk.accountbook.viewmodel

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.SavedStateHandle
import com.seekerzhouk.accountbook.repository.MyRepository
import com.seekerzhouk.accountbook.utils.SharedPreferencesUtil

class MainViewModel(application: Application, private val handle: SavedStateHandle) :
    AndroidViewModel(application), SharedPreferences.OnSharedPreferenceChangeListener {
    private val syncFinishedKey = SharedPreferencesUtil.HAS_SYNC_FINISHED
    private val needSyncKey = SharedPreferencesUtil.IS_NEED_SYNC
    private val shpName = SharedPreferencesUtil.SHP_NAME
    private val myRepository: MyRepository =
        MyRepository.getInstance(application)

    init {
        application.getSharedPreferences(shpName, Context.MODE_PRIVATE)
            .registerOnSharedPreferenceChangeListener(this)
        loadHasSyncFinished()
        loadIsNeedSync()
    }

    private fun loadHasSyncFinished() {
        handle.set(syncFinishedKey, SharedPreferencesUtil.getHasSyncFinished(getApplication()))
    }

    private fun loadIsNeedSync() {
        handle.set(needSyncKey, SharedPreferencesUtil.getIsNeedSync(getApplication()))
    }

    fun hasSyncFinished(): LiveData<Boolean> = handle.getLiveData(syncFinishedKey)

    fun isNeedSync(): LiveData<Boolean> = handle.getLiveData(needSyncKey)

    fun saveHasSyncFinished(hasFinished: Boolean) {
        SharedPreferencesUtil.saveHasSyncFinished(getApplication(), hasFinished)
    }

    fun saveIsNeedSync(needSync: Boolean) {
        SharedPreferencesUtil.saveIsNeedSync(getApplication(), needSync)
    }

    fun syncData() {
        myRepository.syncData()
    }

    fun loadCloudPic() {
        myRepository.loadCloudPic()
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        when (key) {
            syncFinishedKey -> loadHasSyncFinished()
            needSyncKey -> loadIsNeedSync()
        }

    }
}