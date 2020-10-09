package com.seekerzhouk.accountbook.database

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import cn.leancloud.AVObject
import cn.leancloud.AVUser
import com.seekerzhouk.accountbook.database.details.Record
import com.seekerzhouk.accountbook.database.details.RecordsDatabase
import com.seekerzhouk.accountbook.database.home.*
import com.seekerzhouk.accountbook.utils.ConsumptionUtil
import com.seekerzhouk.accountbook.utils.SharedPreferencesUtil
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MyRepository private constructor(val context: Context) {

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
        // 用户从未登陆过，创建本地无主数据表格
        if (!SharedPreferencesUtil.getUnregisteredInsert(context)) {
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
            SharedPreferencesUtil.saveUnregisteredInsert(context, true)
        }
    }

    fun insertLoginUserDataForm() {
        // 用户登陆过，创建用户本地数据表格
        if (!SharedPreferencesUtil.getLoginInsert(context)) {
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
            SharedPreferencesUtil.saveLoginInsert(context, true)
        }
    }

    private fun insertLocal(vararg records: Record) {
        val userName = SharedPreferencesUtil.getUserName(context)
        // 先插入record
        recordDao.insertRecords(*records)
        // 再更新sector和 pillar
        val date = if (records[0].date.substring(5, 6) == "0") {
            records[0].date.substring(6, 7)
        } else {
            records[0].date.substring(5, 7)
        }
        if (records[0].incomeOrExpend == "收入") {
            Log.i("00000", "插入IncomeSector")
            val sector = IncomeSector(userName, records[0].secondType, records[0].money)
            incomeSectorDao.updateIncomeSectors(userName, sector.consumptionType, sector.moneySum)
            val pillar = IncomePillar(userName, date.plus("月"), records[0].money.toFloat())
            incomePillarDao.updateIncomePillar(userName, pillar.date, pillar.moneySum)
        } else {
            val sector = ExpendSector(userName, records[0].secondType, records[0].money)
            expendSectorDao.updateExpendSectors(userName, sector.consumptionType, sector.moneySum)
            val pillar = ExpendPillar(userName, date.plus("月"), records[0].money.toFloat())
            expendPillarDao.updateExpendPillar(userName, pillar.date, pillar.moneySum)
        }
    }

    private fun upload(vararg records: Record) {
        // testLeanCloud start
        val TAG = "MyRepository.upload"
        val leanCloudRecord = AVObject(Record::class.simpleName)
        val user = AVUser.getCurrentUser()
        if (user == null) {
            Log.i(TAG, "user == null")
            return
        }
        if (user.username != records[0].userName) {
            throw Exception("userName Inconsistent!")
        }
        leanCloudRecord.put("user", user)
        leanCloudRecord.put("userName", records[0].userName)
        leanCloudRecord.put("income_or_expend", records[0].incomeOrExpend)
        leanCloudRecord.put("consumptionType", records[0].secondType)
        leanCloudRecord.put("description", records[0].description)
        leanCloudRecord.put("date", records[0].date)
        leanCloudRecord.put("time", records[0].time)
        leanCloudRecord.put("money", records[0].money)
        leanCloudRecord.saveInBackground().subscribe(object : Observer<AVObject> {
            override fun onSubscribe(d: Disposable) {
                Log.i(TAG, "onSubscribe")
            }

            override fun onNext(t: AVObject) {
                Log.i(TAG, "存储成功")
            }

            override fun onError(e: Throwable) {
                e.printStackTrace()
                Log.i(TAG, "onError")
            }

            override fun onComplete() {
                Log.i(TAG, "onComplete")
            }

        })
    }

    fun insertRecords(vararg records: Record) {
        // 先插入本地
        insertLocal(*records)
        // 再上传云端
        upload(*records)
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