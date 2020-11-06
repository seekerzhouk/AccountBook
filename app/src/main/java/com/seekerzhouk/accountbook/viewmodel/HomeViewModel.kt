package com.seekerzhouk.accountbook.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.seekerzhouk.accountbook.repository.MyRepository
import com.seekerzhouk.accountbook.room.details.Record
import com.seekerzhouk.accountbook.room.home.*
import com.seekerzhouk.accountbook.utils.ConsumptionUtil
import com.seekerzhouk.accountbook.utils.DateTimeUtil

enum class Switch {
    ByYear, ByMonth
}

class HomeViewModel(application: Application) : AndroidViewModel(application) {

    private val myRepository: MyRepository = MyRepository.getInstance(application)

    private val _titleSwitch = MutableLiveData(Switch.ByMonth)

    val titleSwitch: LiveData<Switch> = _titleSwitch

    fun turnTitleSwitch() {
        _titleSwitch.postValue(if (_titleSwitch.value == Switch.ByMonth) Switch.ByYear else Switch.ByMonth)
    }

    fun getAllIncomeSectors(): LiveData<List<Sector>> {
        val cList =
            ConsumptionUtil.incomeTypeList.filterIndexed { index: Int, _: String -> index > 0 }
        return getSectors(myRepository.loadIncomeRecords(), cList) {
            sectorsToList(it)
        }
    }

    fun getAllExpendSectors(): LiveData<List<Sector>> {
        val cList =
            ConsumptionUtil.expendTypeList.filterIndexed { index: Int, _: String -> index > 0 }
        return getSectors(myRepository.loadExpendRecords(), cList) {
            sectorsToList(it)
        }
    }

    fun getIncomeSectorsByYear(year: String): LiveData<List<Sector>> {
        val cList =
            ConsumptionUtil.incomeTypeList.filterIndexed { index: Int, _: String -> index > 0 }
        return getSectors(myRepository.loadIncomeRecordsByYear(year), cList) {
            sectorsToList(it)
        }
    }

    fun getExpendSectorsByYear(year: String): LiveData<List<Sector>> {
        val cList =
            ConsumptionUtil.expendTypeList.filterIndexed { index: Int, _: String -> index > 0 }
        return getSectors(myRepository.loadExpendRecordsByYear(year), cList) {
            sectorsToList(it)
        }
    }

    fun getIncomeSectorsByMonth(specificMonth: String): LiveData<List<Sector>> {
        val cList =
            ConsumptionUtil.incomeTypeList.filterIndexed { index: Int, _: String -> index > 0 }
        return getSectors(myRepository.loadIncomeRecordsByMonth(specificMonth), cList) {
            sectorsToList(it)
        }
    }

    fun getExpendSectorsByMonth(specificMonth: String): LiveData<List<Sector>> {
        val cList =
            ConsumptionUtil.expendTypeList.filterIndexed { index: Int, _: String -> index > 0 }
        return getSectors(myRepository.loadExpendRecordsByMonth(specificMonth), cList) {
            sectorsToList(it)
        }
    }


    fun getIncomePillarsByYear(year: String): LiveData<List<Pillar>> =
        getPillars(myRepository.loadIncomeRecordsByYear(year)) {
            pillarsToList(it)
        }


    fun getExpendPillarsByYear(year: String): LiveData<List<Pillar>> =
        getPillars(myRepository.loadExpendRecordsByYear(year)) {
            pillarsToList(it)
        }

    fun getExpendPointsByMonth(specificMonth: String) =
        getPoints(
            myRepository.loadExpendRecordsByMonth(specificMonth),
            DateTimeUtil.getDaysOfMonth(specificMonth)
        ) {
            pointsToList(it)
        }

    fun getIncomePointsByMonth(specificMonth: String) =
        getPoints(
            myRepository.loadIncomeRecordsByMonth(specificMonth),
            DateTimeUtil.getDaysOfMonth(specificMonth)
        ) {
            pointsToList(it)
        }

    private fun sectorsToList(map: MutableMap<String, Float>): List<Sector> {
        return map.map {
            Sector(it.key, it.value)
        }
    }

    private fun pillarsToList(map: MutableMap<String, Float>): List<Pillar> {
        return map.map {
            Pillar(it.key, it.value)
        }
    }

    private fun pointsToList(map: MutableMap<String, Float>): List<Point> {
        return map.map {
            Point(it.key, it.value)
        }
    }

    /**
     * 将数据源映射成一个新的LiveData。
     * Transformations.map(live) {}的作用是将{}的最后一行映射成LiveData。
     * @param live 用来做映射的数据源
     * @param cList 消费或收入类型的List
     * @param block 接收一个MutableMap<String, Double>，并将其映射成一个List<Sector>
     */
    private fun getSectors(
        live: LiveData<List<Record>>,
        cList: List<String>,
        block: (map: MutableMap<String, Float>) -> List<Sector>
    ): LiveData<List<Sector>> {
        return Transformations.map(live) { recordList ->
            // 先 生成一个mMap，并将每个对应的值初始化为0.0
            val mMap = mutableMapOf<String, Float>().apply {
                for (s in cList) {
                    this[s] = 0F
                }
            }
            // 遍历recordList并将值加到mMap。recordList 本身就是｛live: LiveData<List<Record>>｝的值
            for (item in recordList) {
                mMap[item.consumptionType]?.let {
                    mMap[item.consumptionType] = (it + item.money).toFloat()
                }
            }
            // block的作用是将mMap映射成list。
            block(mMap)
        }
    }

    /**
     * 将数据源映射成一个新的LiveData。
     * Transformations.map(live) {}的作用是将{}的最后一行映射成LiveData。
     * @param live 用来做映射的数据源
     * @param block 接收一个MutableMap<String, Double>，并将其映射成一个List<Pillar>
     */
    private fun getPillars(
        live: LiveData<List<Record>>,
        block: (map: MutableMap<String, Float>) -> List<Pillar>
    ): LiveData<List<Pillar>> {
        return Transformations.map(live) { recordList ->
            // 先 生成一个mMap，并将每个月对应的值初始化为0.0
            val mMap = mutableMapOf<String, Float>().apply {
                for (i in 1..12) {
                    this["$i".plus("月")] = 0F
                }
            }
            // 遍历recordList并将值加到mMap。recordList本身就是数据源live的值
            for (item in recordList) {
                mMap[DateTimeUtil.getMonth(item.dateTime)]?.let {
                    mMap[DateTimeUtil.getMonth(item.dateTime)] = (it + item.money).toFloat()
                }
            }
            block(mMap)
        }
    }

    /**
     * 将数据源映射成一个新的LiveData。
     * Transformations.map(live) {}的作用是将{}的最后一行映射成LiveData。
     * @param live 用来做映射的数据源
     * @param dayCount 某个月的天数
     * @param block 接收一个MutableMap<String, Double>，并将其映射成一个List<Point>
     */
    private fun getPoints(
        live: LiveData<List<Record>>,
        dayCount: Int,
        block: (map: MutableMap<String, Float>) -> List<Point>
    ): LiveData<List<Point>> {
        return Transformations.map(live) { recordList ->
            // 先 生成一个mMap，并将每天对应的值初始化为0.0
            val mMap = mutableMapOf<String, Float>().apply {
                for (i in 1..dayCount) {
                    this["$i" + "日"] = 0F
                }
            }
            // 遍历recordList并将值加到mMap。recordList本身就是数据源live的值
            for (item in recordList) {
                mMap[DateTimeUtil.getDayOfMonth(item.dateTime)]?.let {
                    mMap[DateTimeUtil.getDayOfMonth(item.dateTime)] = (it + item.money).toFloat()
                }
            }
            block(mMap)
        }
    }
}