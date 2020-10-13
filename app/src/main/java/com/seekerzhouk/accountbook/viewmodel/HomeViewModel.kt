package com.seekerzhouk.accountbook.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.seekerzhouk.accountbook.repository.MyRepository
import com.seekerzhouk.accountbook.room.home.ExpendSector
import com.seekerzhouk.accountbook.room.home.IncomeSector
import com.seekerzhouk.accountbook.room.home.ExpendPillar
import com.seekerzhouk.accountbook.room.home.IncomePillar

class HomeViewModel(application: Application) : AndroidViewModel(application) {

    private val myRepository: MyRepository =
        MyRepository.getInstance(application)

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

    fun getExpendPillars(): LiveData<List<ExpendPillar>>{
        return myRepository.getExpendPillars()
    }

    fun getIncomePillars(): LiveData<List<IncomePillar>> {
        return myRepository.getIncomePillars()
    }

}