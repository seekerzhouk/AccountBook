package com.seekerzhouk.accountbook.database

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import com.seekerzhouk.accountbook.database.details.Record
import com.seekerzhouk.accountbook.database.details.RecordsDatabase
import com.seekerzhouk.accountbook.database.home.*
import com.seekerzhouk.accountbook.utils.ConsumptionUtil
import com.seekerzhouk.accountbook.utils.SharedPreferencesUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MyRepository(context: Context) {

    private val recordDao =
        RecordsDatabase.getDatabase(context.applicationContext).getRecordDao()

    private val incomeSectorDao =
        IncomeSectorsDatabase.getDatabase(context.applicationContext).getRecordDao()

    private val expendSectorDao =
        ExpendSectorsDatabase.getDatabase(context.applicationContext).getRecordDao()
    private val expendPillarDao =
        ExpendPillarDatabase.getDatabase(context.applicationContext).getPillarDao()
    private val incomePillarDao =
        IncomePillarDatabase.getDatabase(context.applicationContext).getPillarDao()

    init {
        if (!SharedPreferencesUtil.getOnceInsert(context)) {
            val arrIncomes = Array(ConsumptionUtil.incomeTypeList.size - 1) {
                IncomeSector(ConsumptionUtil.incomeTypeList[it + 1], 0.0)
            }
            val arrExpends = Array(ConsumptionUtil.expendTypeList.size - 1) {
                ExpendSector(ConsumptionUtil.expendTypeList[it + 1], 0.0)
            }
            val arrExpendPillars = Array(12) {
                ExpendPillar((it + 1).toString().plus("月"), 0F)
            }
            val arrIncomePillars = Array(12) {
                IncomePillar((it + 1).toString().plus("月"), 0F)
            }
            CoroutineScope(Dispatchers.IO).launch {
                launch {
                    incomeSectorDao.insertIncomeSectors(*arrIncomes)
                }
                launch {
                    expendSectorDao.insertExpendSectors(*arrExpends)
                }
                launch {
                    expendPillarDao.insertExpendPillar(*arrExpendPillars)
                }
                launch {
                    incomePillarDao.insertIncomePillar(*arrIncomePillars)
                }
            }
            SharedPreferencesUtil.saveOnceInsert(context, true)
        }
    }

    fun insertRecords(vararg records: Record) {
        // 先插入record
        recordDao.insertRecords(*records)

        // 再插入sector和 pillar
        val date = if (records[0].date.substring(5, 6) == "0") {
            records[0].date.substring(6, 7)
        } else {
            records[0].date.substring(5, 7)
        }
        if (records[0].incomeOrExpend == "收入") {
            Log.i("00000", "插入IncomeSector")
            val sector = IncomeSector(records[0].secondType, records[0].money)
            incomeSectorDao.updateIncomeSectors(sector.consumptionType, sector.moneySum)
            val pillar = IncomePillar(date.plus("月"), records[0].money.toFloat())
            incomePillarDao.updateIncomePillar(pillar.date, pillar.moneySum)
        } else {
            val sector = ExpendSector(records[0].secondType, records[0].money)
            expendSectorDao.updateExpendSectors(sector.consumptionType, sector.moneySum)
            val pillar = ExpendPillar(date.plus("月"), records[0].money.toFloat())
            expendPillarDao.updateExpendPillar(pillar.date, pillar.moneySum)
        }
    }

    fun deleteRecords(vararg records: Record) {
        recordDao.deleteRecords(*records)
    }

    fun updateRecords(vararg records: Record) {
        recordDao.updateRecords(*records)
    }

    fun loadAllRecords(): LiveData<List<Record>> {
        return recordDao.loadAllRecords()
    }

    fun findRecordsWithPatten(patten: String): LiveData<List<Record>> {
        return recordDao.findRecordWithPatten("%$patten%")
    }

    fun findIncomeRecords(): LiveData<List<Record>> {
        return recordDao.findIncomeRecords()
    }

    fun findExpendRecords(): LiveData<List<Record>> {
        return recordDao.findExpendRecords()
    }

    fun findIncomeRecordsWithPatten(patten: String): LiveData<List<Record>> {
        return recordDao.findIncomeRecordsWithPatten("%$patten%")
    }

    fun findExpendRecordsWithPatten(patten: String): LiveData<List<Record>> {
        return recordDao.findExpendRecordsWithPatten("%$patten%")
    }

    fun findIncomeRecordsBySelectedType(selectedType: String): LiveData<List<Record>> {
        return recordDao.findIncomeRecordsBySelectedType(selectedType)
    }

    fun findExpendRecordsBySelectedType(selectedType: String): LiveData<List<Record>> {
        return recordDao.findExpendRecordsBySelectedType(selectedType)
    }

    fun findIncomeRecordsBySelectedTypeWithPatten(
        selectedType: String,
        patten: String
    ): LiveData<List<Record>> {
        return recordDao.findIncomeRecordsBySelectedTypeWithPatten(selectedType, "%$patten%")
    }

    fun findExpendRecordsBySelectedTypeWithPatten(
        selectedType: String,
        patten: String
    ): LiveData<List<Record>> {
        return recordDao.findExpendRecordsBySelectedTypeWithPatten(selectedType, "%$patten%")
    }

    fun getIncomeSectors(): LiveData<List<IncomeSector>> {
        return incomeSectorDao.getIncomeSectors()
    }

    fun getExpendSectors(): LiveData<List<ExpendSector>> {
        return expendSectorDao.getExpendSectors()
    }

    fun getExpendPillars(): LiveData<List<ExpendPillar>> {
        return expendPillarDao.getExpendPillars()
    }

    fun getIncomePillars(): LiveData<List<IncomePillar>> {
        return incomePillarDao.getIncomePillars()
    }
}