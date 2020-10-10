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
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch

class MyRepository private constructor(val context: Context) {
    private val TAG = MyRepository::class.simpleName

    companion object {
        private var instance: MyRepository? = null

        @Synchronized
        fun getInstance(context: Context): MyRepository {
            return instance ?: MyRepository(context).also {
                instance = it
            }
        }
    }

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
        // 初始化本地无主数据表格
        initLocalNoOwnerForm()
    }

    /**
     * 确认添加一条记录，需要进行的操作
     */
    fun insertRecords(vararg records: Record) {
        // 本地插入Record，更新IncomeSector、ExpendSector、IncomePillar、ExpendPillar四个数据表
        insertLocalData(*records)
        // 检查用户是否登陆过
        if (SharedPreferencesUtil.getUserName(context).isNotEmpty()) {
            // 登陆过,则用此用户向 LeanCloud云端插入Record，更新IncomeSector、ExpendSector、IncomePillar、ExpendPillar四个数据表
            LeanCloudOperation.uploadToCloud(*records)
        }
    }

    /**
     * 登陆之后需要进行的操作，初始化cloud和本地的用户数据表格
     */
    fun cloudAndLocalUserFormInit() {
        // 1.检查用户本地数据是否初始化
        if (!SharedPreferencesUtil.getLoginUserFormHasInit(context)) {
            // 2.初始化用户本地数据
            initLocalUserForm()
        }

        // 1.检查LeanCloud云端是否初始化用户数据
        if (SharedPreferencesUtil.getHasCloudFormInit(context)) {
            return
        }
        val channel = Channel<Boolean>()
        LeanCloudOperation.cloudUserFormHasInit(channel)
        CoroutineScope(Dispatchers.IO).launch {
            val result = channel.receive()
            if (result) { // 已经初始化，return
                Log.i(TAG, "---channel.receive()$result")
                SharedPreferencesUtil.saveHasCloudFormInit(context, true)
            } else {
                // 2.初始化云端用户数据
                LeanCloudOperation.initCloudUserForm()
            }
        }
    }

    /**
     * 本地要先初始化IncomeSector、ExpendSector、IncomePillar、ExpendPillar四张无主数据表格
     */
    private fun initLocalNoOwnerForm() {
        val userName = SharedPreferencesUtil.getUserName(context)
        val arrIncomes = Array(ConsumptionUtil.incomeTypeList.size - 1) {
            IncomeSector(userName, ConsumptionUtil.incomeTypeList[it + 1], 0.0)
        }
        val arrExpends = Array(ConsumptionUtil.expendTypeList.size - 1) {
            ExpendSector(userName, ConsumptionUtil.expendTypeList[it + 1], 0.0)
        }
        val arrExpendPillars = Array(12) {
            ExpendPillar(userName, (it + 1).toString().plus("月"), 0F)
        }
        val arrIncomePillars = Array(12) {
            IncomePillar(userName, (it + 1).toString().plus("月"), 0F)
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
        SharedPreferencesUtil.saveNoOwnerFormHasInit(context, true)
    }

    /**
     * 登陆后，本地初始化IncomeSector、ExpendSector、IncomePillar、ExpendPillar四张 用户 数据表格
     */
    private fun initLocalUserForm() {
        val userName = SharedPreferencesUtil.getUserName(context)
        val arrIncomes = Array(ConsumptionUtil.incomeTypeList.size - 1) {
            IncomeSector(userName, ConsumptionUtil.incomeTypeList[it + 1], 0.0)
        }
        val arrExpends = Array(ConsumptionUtil.expendTypeList.size - 1) {
            ExpendSector(userName, ConsumptionUtil.expendTypeList[it + 1], 0.0)
        }
        val arrExpendPillars = Array(12) {
            ExpendPillar(userName, (it + 1).toString().plus("月"), 0F)
        }
        val arrIncomePillars = Array(12) {
            IncomePillar(userName, (it + 1).toString().plus("月"), 0F)
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
        SharedPreferencesUtil.saveLoginUserFormHasInit(context, true)
    }


    /**
     * 本地插入Record、更新本地四张数据表
     */
    private fun insertLocalData(vararg records: Record) {
        val record = records[0]
        // 先插入record
        recordDao.insertRecords(record)
        // 再更新sector和 pillar
        val userName = record.userName
        val secondType = record.secondType
        val money = record.money
        val date = if (record.date.substring(5, 6) == "0") {
            record.date.substring(6, 7).plus("月")
        } else {
            record.date.substring(5, 7).plus("月")
        }
        if (record.incomeOrExpend == ConsumptionUtil.INCOME) {
            val sector = IncomeSector(userName, secondType, money)
            incomeSectorDao.updateIncomeSectors(userName, sector.consumptionType, sector.moneySum)
            val pillar = IncomePillar(userName, date, money.toFloat())
            incomePillarDao.updateIncomePillar(userName, pillar.date, pillar.moneySum)
        } else {
            val sector = ExpendSector(userName, secondType, money)
            expendSectorDao.updateExpendSectors(userName, sector.consumptionType, sector.moneySum)
            val pillar = ExpendPillar(userName, date, money.toFloat())
            expendPillarDao.updateExpendPillar(userName, pillar.date, pillar.moneySum)
        }
    }


    fun deleteRecords(vararg records: Record) {
        recordDao.deleteRecords(*records)
    }

    fun updateRecords(vararg records: Record) {
        recordDao.updateRecords(*records)
    }

    fun loadAllRecords(): LiveData<List<Record>> {
        return recordDao.loadAllRecords(SharedPreferencesUtil.getUserName(context))
    }

    fun findRecordsWithPatten(patten: String): LiveData<List<Record>> {
        return recordDao.findRecordWithPatten(
            SharedPreferencesUtil.getUserName(context),
            "%$patten%"
        )
    }

    fun findIncomeRecords(): LiveData<List<Record>> {
        return recordDao.findIncomeRecords(SharedPreferencesUtil.getUserName(context))
    }

    fun findExpendRecords(): LiveData<List<Record>> {
        return recordDao.findExpendRecords(SharedPreferencesUtil.getUserName(context))
    }

    fun findIncomeRecordsWithPatten(patten: String): LiveData<List<Record>> {
        return recordDao.findIncomeRecordsWithPatten(
            SharedPreferencesUtil.getUserName(context),
            "%$patten%"
        )
    }

    fun findExpendRecordsWithPatten(patten: String): LiveData<List<Record>> {
        return recordDao.findExpendRecordsWithPatten(
            SharedPreferencesUtil.getUserName(context),
            "%$patten%"
        )
    }

    fun findIncomeRecordsBySelectedType(selectedType: String): LiveData<List<Record>> {
        return recordDao.findIncomeRecordsBySelectedType(
            SharedPreferencesUtil.getUserName(context),
            selectedType
        )
    }

    fun findExpendRecordsBySelectedType(selectedType: String): LiveData<List<Record>> {
        return recordDao.findExpendRecordsBySelectedType(
            SharedPreferencesUtil.getUserName(context),
            selectedType
        )
    }

    fun findIncomeRecordsBySelectedTypeWithPatten(
        selectedType: String,
        patten: String
    ): LiveData<List<Record>> {
        return recordDao.findIncomeRecordsBySelectedTypeWithPatten(
            SharedPreferencesUtil.getUserName(
                context
            ), selectedType, "%$patten%"
        )
    }

    fun findExpendRecordsBySelectedTypeWithPatten(
        selectedType: String,
        patten: String
    ): LiveData<List<Record>> {
        return recordDao.findExpendRecordsBySelectedTypeWithPatten(
            SharedPreferencesUtil.getUserName(
                context
            ), selectedType, "%$patten%"
        )
    }

    fun getIncomeSectors(): LiveData<List<IncomeSector>> {
        return incomeSectorDao.getIncomeSectors(SharedPreferencesUtil.getUserName(context))
    }

    fun getExpendSectors(): LiveData<List<ExpendSector>> {
        return expendSectorDao.getExpendSectors(SharedPreferencesUtil.getUserName(context))
    }

    fun getExpendPillars(): LiveData<List<ExpendPillar>> {
        return expendPillarDao.getExpendPillars(SharedPreferencesUtil.getUserName(context))
    }

    fun getIncomePillars(): LiveData<List<IncomePillar>> {
        return incomePillarDao.getIncomePillars(SharedPreferencesUtil.getUserName(context))
    }


}