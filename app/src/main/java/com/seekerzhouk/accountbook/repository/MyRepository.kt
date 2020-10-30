package com.seekerzhouk.accountbook.repository

import android.content.Context
import androidx.lifecycle.LiveData
import cn.leancloud.AVObject
import cn.leancloud.AVQuery
import cn.leancloud.AVUser
import com.bumptech.glide.Glide
import com.seekerzhouk.accountbook.R
import com.seekerzhouk.accountbook.room.UserAddInfo
import com.seekerzhouk.accountbook.room.details.Record
import com.seekerzhouk.accountbook.room.details.RecordsDatabase
import com.seekerzhouk.accountbook.room.home.*
import com.seekerzhouk.accountbook.utils.ConsumptionUtil
import com.seekerzhouk.accountbook.utils.MyLog
import com.seekerzhouk.accountbook.utils.SharedPreferencesUtil
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException


class MyRepository private constructor(val context: Context) {
    private val tag = MyRepository::class.java.name

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
            // 登陆过,则用此用户向 LeanCloud云端插入Record
            LeanCloudOperation.uploadToCloud(*records)
        }
    }

    /**
     * 登陆之后需要进行的操作，初始化cloud的UserAddInfo和本地的用户数据表格
     */
    fun cloudAndLocalUserFormInit() = CoroutineScope(Dispatchers.IO).launch {
        val user = AVUser.getCurrentUser()
        val isLocalInit = async {
            // 1.检查具体用户本地数据是否初始化
            if (!SharedPreferencesUtil.getUserLocalFormStatus(context, user.username)) {
                // 2.初始化具体用户本地数据
                initLocalUserForm()
                SharedPreferencesUtil.saveUserLocalFormStatus(context, user.username, true)
            }
            //3.最终都返回true，代表本地初始化结束。
            return@async true
        }

        val isCloudInit = async {
            // 1.检查LeanCloud云端是否初始化UserAddInfo
            if (SharedPreferencesUtil.getUserAddInfoStatus(context, user.username)) {
                // 2.SharedPreferences记录过云端被初始化，返回true
                return@async true
            } else {
                // 2.SharedPreferences没有记录过云端被初始化，需要去查找云端
                val channel = Channel<Boolean>()
                LeanCloudOperation.cloudUserAddInfoHasInit(channel)
                val result = channel.receive()
                if (result) { // 3.查找的结果是已经初始化，return true
                    SharedPreferencesUtil.saveUserAddInfoStatus(context, user.username, true)
                    return@async true
                } else {
                    // 4.查找的结果是没有被初始化，则要初始化云端用户数据。返回false
                    LeanCloudOperation.initCloudUserAddInfo()
                    return@async false
                }
            }
        }

        //只有本地和云端都被初始化过，才需要进行同步。云端没被初始化过，没有数据，不需要同步。
        if (isLocalInit.await() && isCloudInit.await()) {
            SharedPreferencesUtil.saveIsNeedSync(context, true)
        }

    }

    /**
     * 本地要先初始化IncomeSector、ExpendSector、IncomePillar、ExpendPillar四张无主数据表格
     */
    private fun initLocalNoOwnerForm() {
        if (SharedPreferencesUtil.getNoOwnerFormHasInit(context)) {
            return
        }
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
        incomeSectorDao.insertIncomeSectors(*arrIncomes)
        expendSectorDao.insertExpendSectors(*arrExpends)
        expendPillarDao.insertExpendPillar(*arrExpendPillars)
        incomePillarDao.insertIncomePillar(*arrIncomePillars)
    }


    /**
     * 本地插入Record、更新本地四张数据表
     */
    private fun insertLocalData(vararg records: Record) {
        // 先插入record
        recordDao.insertRecords(*records)
        // 再更新sector和 pillar
        for (record in records) {
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
                incomeSectorDao.updateIncomeSectors(
                    userName,
                    sector.consumptionType,
                    sector.moneySum
                )
                val pillar = IncomePillar(userName, date, money.toFloat())
                incomePillarDao.updateIncomePillar(userName, pillar.date, pillar.moneySum)
            } else {
                val sector = ExpendSector(userName, secondType, money)
                expendSectorDao.updateExpendSectors(
                    userName,
                    sector.consumptionType,
                    sector.moneySum
                )
                val pillar = ExpendPillar(userName, date, money.toFloat())
                expendPillarDao.updateExpendPillar(userName, pillar.date, pillar.moneySum)
            }
        }
        MyLog.i(tag, "insertLocalData finished.")
    }

    // 清除/清零本地数据
    private fun localDataCleared() {
        val userName = SharedPreferencesUtil.getUserName(context)
        // 先清除Record
        clearUserRecords(userName)
        // 再清零四表格
        clearIncomeSectors(userName)
        clearIncomePillars(userName)
        clearExpendSectors(userName)
        clearExpendPillars(userName)
    }

    // 将云端数据同步到本地
    fun syncData() {
        AVQuery<AVObject>(Record::class.simpleName).apply {
            whereEqualTo("userName", SharedPreferencesUtil.getUserName(context))
        }.findInBackground().subscribe(object : Observer<List<AVObject>> {
            override fun onSubscribe(d: Disposable) {
                MyLog.i(tag, "syncData(): findInBackground onSubscribe")
            }

            override fun onNext(results: List<AVObject>) {
                MyLog.i(tag, "syncData(): findInBackground onNext")
                CoroutineScope(Dispatchers.IO).launch {
                    localDataCleared()
                    Array(results.size) { i ->
                        Record(
                            results[i].getString("userName"),
                            results[i].getString("income_or_expend"),
                            results[i].getString("consumptionType"),
                            results[i].getString("description"),
                            results[i].getString("date"),
                            results[i].getString("time"),
                            results[i].getDouble("money")
                        )
                    }.also { records ->
                        insertLocalData(*records)
                        MyLog.i(tag, "syncData(): syncData finished.")
                        delay(1_000)
                        SharedPreferencesUtil.saveHasSyncFinished(context, true)
                    }
                }
            }

            override fun onError(e: Throwable) {
                MyLog.i(tag, "syncData(): findInBackground onError ", e)
            }

            override fun onComplete() {
                MyLog.i(tag, "syncData(): findInBackground onComplete")
            }
        })
    }

    fun loadCloudPic() {
        loadBgPic()
        loadAvatar()
    }

    private fun loadAvatar() {
        AVQuery<AVObject>(UserAddInfo::class.java.simpleName).apply {
            whereEqualTo("userName", AVUser.getCurrentUser().username)
        }.firstInBackground.subscribe(object : Observer<AVObject> {
            override fun onSubscribe(d: Disposable) {
                MyLog.i(tag, "loadAvatar onSubscribe()")
            }

            override fun onNext(t: AVObject) {
                MyLog.i(tag, "loadAvatar onNext()")
                t.get("avatarUrl")?.let {
                    (it as String).also {
                        CoroutineScope(Dispatchers.IO).launch {
                            val file = Glide.with(context).downloadOnly().load(it).submit().get()
                            copyToLocal(
                                file, context.externalCacheDir?.absolutePath +
                                        "/${SharedPreferencesUtil.getUserName(context)}" + context.getString(
                                    R.string.avatar_pic_suffix
                                )
                            )

                        }
                    }
                }

            }

            override fun onError(e: Throwable) {
                MyLog.i(tag, "loadAvatar onError()", e)
            }

            override fun onComplete() {
                MyLog.i(tag, "loadAvatar onComplete()")
            }

        })
    }

    private fun loadBgPic() {
        AVQuery<AVObject>(UserAddInfo::class.java.simpleName).apply {
            whereEqualTo("userName", AVUser.getCurrentUser().username)
        }.firstInBackground.subscribe(object : Observer<AVObject> {
            override fun onSubscribe(d: Disposable) {
                MyLog.i(tag, "loadBgPic onSubscribe()")
            }

            override fun onNext(t: AVObject) {
                MyLog.i(tag, "loadBgPic onNext()")
                t.get("bgUrl")?.let {
                    (it as String).also {
                        CoroutineScope(Dispatchers.IO).launch {
                            val file = Glide.with(context).downloadOnly().load(it).submit().get()
                            copyToLocal(
                                file, context.externalCacheDir?.absolutePath +
                                        "/${SharedPreferencesUtil.getUserName(context)}" + context.getString(
                                    R.string.bg_pic_suffix
                                )
                            )

                        }
                    }
                }
            }

            override fun onError(e: Throwable) {
                MyLog.i(tag, "loadBgPic onError()", e)
            }

            override fun onComplete() {
                MyLog.i(tag, "loadBgPic onComplete()")
            }

        })

    }

    private fun copyToLocal(sourceFile: File, targetPath: String) {
        MyLog.i(tag, "copyToLocal $targetPath")
        val targetFile = File(targetPath)
        var fileInputStream: FileInputStream? = null
        var fileOutputStream: FileOutputStream? = null
        try {
            fileInputStream = FileInputStream(sourceFile)
            fileOutputStream = FileOutputStream(targetFile)
            val buffer = ByteArray(1024)
            while (fileInputStream.read(buffer) > 0) {
                fileOutputStream.write(buffer)
            }
        } catch (e: Exception) {
            MyLog.i(tag, "copyToLocal onError ", e)
        } finally {
            try {
                fileInputStream?.close()
                fileOutputStream?.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    private fun clearUserRecords(userName: String) {
        recordDao.clearUserRecords(userName)
    }

    fun updateRecords(vararg records: Record) {
        recordDao.updateRecords(*records)
    }

    fun deleteRecords(vararg records: Record) {
        recordDao.deleteRecords(*records)
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

    fun clearIncomeSectors(userName: String) {
        incomeSectorDao.clearIncomeSectors(userName)
    }

    fun clearIncomePillars(userName: String) {
        incomePillarDao.clearIncomePillars(userName)
    }

    fun clearExpendSectors(userName: String) {
        expendSectorDao.clearExpendSectors(userName)
    }

    fun clearExpendPillars(userName: String) {
        expendPillarDao.clearExpendPillars(userName)
    }

}