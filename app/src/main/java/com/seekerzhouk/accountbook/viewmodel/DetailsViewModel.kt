package com.seekerzhouk.accountbook.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.seekerzhouk.accountbook.repository.MyRepository
import com.seekerzhouk.accountbook.room.details.Record
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DetailsViewModel(application: Application) : AndroidViewModel(application) {

    private val myRepository: MyRepository =
        MyRepository.getInstance(application)

    private fun runInScope(block: () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            run(block)
        }
    }

    fun insertRecords(vararg records: Record) {
        runInScope { myRepository.insertRecords(*records) }
    }

    fun deleteRecords(vararg records: Record) {
        runInScope { myRepository.deleteRecords(*records) }
    }

    fun updateRecords(vararg records: Record) {
        runInScope { myRepository.updateRecords(*records) }
    }

    fun loadAllRecords(): LiveData<List<Record>> {
        return myRepository.loadAllRecords()
    }

    fun findRecordWithPatten(patten: String): LiveData<List<Record>> {
        return myRepository.findRecordsWithPatten(patten)
    }

    fun findIncomeRecords(): LiveData<List<Record>> {
        return myRepository.findIncomeRecords()
    }

    fun findExpendRecords(): LiveData<List<Record>> {
        return myRepository.findExpendRecords()
    }

    fun findIncomeRecordsWithPatten(patten: String): LiveData<List<Record>> {
        return myRepository.findIncomeRecordsWithPatten(patten)
    }

    fun findExpendRecordsWithPatten(patten: String): LiveData<List<Record>> {
        return myRepository.findExpendRecordsWithPatten(patten)
    }

    fun findIncomeRecordsBySelectedType(selectedType: String): LiveData<List<Record>> {
        return myRepository.findIncomeRecordsBySelectedType(selectedType)
    }

    fun findExpendRecordsBySelectedType(selectedType: String): LiveData<List<Record>> {
        return myRepository.findExpendRecordsBySelectedType(selectedType)
    }

    fun findIncomeRecordsBySelectedTypeWithPatten(
        selectedType: String,
        patten: String
    ): LiveData<List<Record>> {
        return myRepository.findIncomeRecordsBySelectedTypeWithPatten(selectedType, patten)
    }


    fun findExpendRecordsBySelectedTypeWithPatten(
        selectedType: String,
        patten: String
    ): LiveData<List<Record>> {
        return myRepository.findExpendRecordsBySelectedTypeWithPatten(selectedType, patten)
    }


}