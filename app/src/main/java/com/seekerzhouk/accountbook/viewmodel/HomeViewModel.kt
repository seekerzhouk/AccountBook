package com.seekerzhouk.accountbook.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.seekerzhouk.accountbook.repository.MyRepository
import com.seekerzhouk.accountbook.room.details.Record
import com.seekerzhouk.accountbook.room.home.*
import com.seekerzhouk.accountbook.utils.ConsumptionUtil
import com.seekerzhouk.accountbook.utils.DateTimeUtil

class HomeViewModel(application: Application) : AndroidViewModel(application) {

    private val myRepository: MyRepository = MyRepository.getInstance(application)

    fun getIncomeSectors(): LiveData<List<IncomeSector>> {
        val cList =
            ConsumptionUtil.incomeTypeList.filterIndexed { index: Int, _: String -> index > 0 }
        return getSectors(myRepository.loadIncomeRecords(), cList) {
            it.map { entry ->
                IncomeSector(entry.key, entry.value)
            }.toList()
        }
    }

    fun getExpendSectors(): LiveData<List<ExpendSector>> {
        val cList =
            ConsumptionUtil.expendTypeList.filterIndexed { index: Int, _: String -> index > 0 }
        return getSectors(myRepository.loadExpendRecords(), cList) {
            it.map { entry ->
                ExpendSector(entry.key, entry.value)
            }.toList()
        }
    }


    fun getIncomePillars(): LiveData<List<IncomePillar>> =
        getPillars(myRepository.loadIncomeRecords()) {
            it.map { entry ->
                IncomePillar(entry.key, entry.value.toFloat())
            }.toList()
        }


    fun getExpendPillars(): LiveData<List<ExpendPillar>> =
        getPillars(myRepository.loadExpendRecords()) {
            it.map { entry ->
                ExpendPillar(entry.key, entry.value.toFloat())
            }.toList()
        }

    /**
     * 通过调用 Transformations.map()将 live映射成新的LiveData。
     * Transformations.map()的大括号需要返回一个目标List，也就是block的返回值,
     * 然后Transformations.map()函数将List映射成新的LiveData<List<T>>.
     */
    private fun <T : Sector> getSectors(
        live: LiveData<List<Record>>,
        cList: List<String>,
        block: (map: MutableMap<String, Double>) -> List<T>
    ): LiveData<List<T>> {
        return Transformations.map(live) { recordList ->
            // 先 生成一个mMap，并将每个对应的值初始化为0.0
            val mMap = mutableMapOf<String, Double>().apply {
                for (s in cList) {
                    this[s] = 0.0
                }
            }
            // 遍历recordList并将值加到mMap。recordList 本身就是｛live: LiveData<List<Record>>｝的值
            for (item in recordList) {
                mMap[item.consumptionType]?.let {
                    mMap[item.consumptionType] = it + item.money
                }
            }
            // block的作用是将mMap映射成list。
            block(mMap)
        }
    }

    /**
     * 通过调用 Transformations.map()将 List映射成新的LiveData。
     * Transformations.map()的大括号需要返回一个目标List，也就是block的返回值,
     * 然后Transformations.map()函数将这个List映射成新的LiveData<List<T>>.
     */
    private fun <T : Pillar> getPillars(
        live: LiveData<List<Record>>,
        block: (map: MutableMap<String, Double>) -> List<T>
    ): LiveData<List<T>> {
        return Transformations.map(live) { recordList ->
            // 先 生成一个mMap，并将每个月对应的值初始化为0.0
            val mMap = mutableMapOf<String, Double>().apply {
                for (i in 1..12) {
                    this["$i".plus("月")] = 0.0
                }
            }
            // 遍历recordList并将值加到mMap。recordList本身就是｛live: LiveData<List<Record>>｝的值
            for (item in recordList) {
                mMap[DateTimeUtil.getMonth(item.dateTime)]?.let {
                    mMap[DateTimeUtil.getMonth(item.dateTime)] = it + item.money
                }
            }
            // block的作用是将mMap映射成list。
            block(mMap)
        }
    }
}