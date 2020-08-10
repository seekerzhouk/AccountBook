package com.seekerzhouk.accountbook.ui.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.seekerzhouk.accountbook.database.MyRepository
import com.seekerzhouk.accountbook.database.home.ExpendSector
import com.seekerzhouk.accountbook.database.home.IncomeSector
import com.seekerzhouk.accountbook.database.home.Pillar

class HomeViewModel(application: Application) : AndroidViewModel(application) {

    private val myRepository: MyRepository =
        MyRepository(application)

//    fun getIncomeSum(): LiveData<Double> {
//        return myRepository.getIncomeSum()
//    }
//
//    fun getIncomeTypeSum(selectedType: String): LiveData<Double> {
//        return myRepository.getIncomeTypeSum(selectedType)
//    }
//
//    fun getExpendSum(): LiveData<Double> {
//        return myRepository.getExpendSum()
//    }
//
//    fun getExpendTypeSum(selectedType: String): LiveData<Double> {
//        return myRepository.getExpendTypeSum(selectedType)
//    }

    fun getIncomeSectors(): LiveData<List<IncomeSector>> {
        return myRepository.getIncomeSectors()
    }

    fun getExpendSectors(): LiveData<List<ExpendSector>> {
        return myRepository.getExpendSectors()
    }

    fun getPillars(): LiveData<List<Pillar>>{
        return myRepository.getPillars()
    }

}