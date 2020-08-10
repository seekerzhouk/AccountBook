package com.seekerzhouk.accountbook.database

import android.content.Context
import android.os.AsyncTask
import android.util.Log
import androidx.lifecycle.LiveData
import com.seekerzhouk.accountbook.database.details.Record
import com.seekerzhouk.accountbook.database.details.RecordDao
import com.seekerzhouk.accountbook.database.details.RecordsDatabase
import com.seekerzhouk.accountbook.database.home.*
import com.seekerzhouk.accountbook.utils.ConsumptionUtil
import com.seekerzhouk.accountbook.utils.SharedPreferencesUtil

class MyRepository(context: Context) {

    private val recordDao =
        RecordsDatabase.getDatabase(context.applicationContext).getRecordDao()

    private val incomeSectorDao =
        IncomeSectorsDatabase.getDatabase(context.applicationContext).getRecordDao()

    private val expendSectorDao =
        ExpendSectorsDatabase.getDatabase(context.applicationContext).getRecordDao()
    private val pillarDao = PillarDatabase.getDatabase(context.applicationContext).getPillarDao()

    init {
        if (!SharedPreferencesUtil.getOnceInsert(context)) {
            val arrIncomes = Array(ConsumptionUtil.incomeTypeList.size - 1) {
                IncomeSector(ConsumptionUtil.incomeTypeList[it + 1], 0.0)
            }
            InsertIncomeTask(incomeSectorDao).execute(*arrIncomes)

            val arrExpends = Array(ConsumptionUtil.expendTypeList.size - 1) {
                ExpendSector(ConsumptionUtil.expendTypeList[it + 1], 0.0)
            }
            InsertExpendTask(expendSectorDao).execute(*arrExpends)

            val arrPillars = Array(12) {
                Pillar((it + 1).toString().plus("月"), 55F)
            }
            InsertPillarsTask(pillarDao).execute(*arrPillars)

            SharedPreferencesUtil.saveOnceInsert(context, true)
        }
    }

    fun insertRecords(vararg records: Record) {
        // 先插入record
        InsertRecordsAsyncTask(recordDao).execute(*records)
        // 再插入sector
        if (records[0].incomeOrExpend == "收入") {
            Log.i("00000", "插入IncomeSector")
            val sector = IncomeSector(records[0].secondType, records[0].money)
            IncomeUpdateTask(incomeSectorDao).execute(sector)

            val date = if (records[0].date.substring(5, 6) == "0") {
                records[0].date.substring(6, 7)
            } else {
                records[0].date.substring(5, 7)
            }
            val pillar = Pillar(date.plus("月"), records[0].money.toFloat())
            Log.i("HistogramView002", date.plus("月"))
            PillarUpdateTask(pillarDao).execute(pillar)
        } else {
            val sector = ExpendSector(records[0].secondType, records[0].money)
            ExpendUpdateTask(expendSectorDao).execute(sector)
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

    fun getPillars(): LiveData<List<Pillar>> {
        return pillarDao.getPillars()
    }

    class InsertRecordsAsyncTask(private val recordDao: RecordDao) :
        AsyncTask<Record, Unit, Unit>() {
        override fun doInBackground(vararg params: Record) {
            recordDao.insertRecords(*params)
        }
    }

    class InsertIncomeTask(private val incomeSectorDao: IncomeSectorDao) :
        AsyncTask<IncomeSector, Unit, Unit>() {
        override fun doInBackground(vararg params: IncomeSector) {
            incomeSectorDao.insertIncomeSectors(*params)
        }
    }

    class InsertExpendTask(private val expendSectorDao: ExpendSectorDao) :
        AsyncTask<ExpendSector, Unit, Unit>() {
        override fun doInBackground(vararg params: ExpendSector) {
            expendSectorDao.insertExpendSectors(*params)
        }
    }

    class IncomeUpdateTask(private val incomeSectorDao: IncomeSectorDao) :
        AsyncTask<IncomeSector, Unit, Unit>() {
        override fun doInBackground(vararg params: IncomeSector) {
            incomeSectorDao.updateIncomeSectors(params[0].consumptionType, params[0].moneySum)
        }
    }

    class ExpendUpdateTask(private val expendSectorDao: ExpendSectorDao) :
        AsyncTask<ExpendSector, Unit, Unit>() {
        override fun doInBackground(vararg params: ExpendSector) {
            expendSectorDao.updateExpendSectors(params[0].consumptionType, params[0].moneySum)
        }
    }

    class InsertPillarsTask(private val pillarDao: PillarDao) : AsyncTask<Pillar, Unit, Unit>() {
        override fun doInBackground(vararg params: Pillar) {
            pillarDao.insertPillar(*params)
        }
    }

    class PillarUpdateTask(private val pillarDao: PillarDao) : AsyncTask<Pillar, Unit, Unit>() {
        override fun doInBackground(vararg params: Pillar) {
            pillarDao.updatePillar(params[0].date, params[0].moneySum)
        }

    }
}