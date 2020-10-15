package com.seekerzhouk.accountbook.repository

import cn.leancloud.AVObject
import cn.leancloud.AVQuery
import cn.leancloud.AVUser
import com.seekerzhouk.accountbook.room.CloudFormStatus
import com.seekerzhouk.accountbook.room.details.Record
import com.seekerzhouk.accountbook.room.home.ExpendPillar
import com.seekerzhouk.accountbook.room.home.ExpendSector
import com.seekerzhouk.accountbook.room.home.IncomePillar
import com.seekerzhouk.accountbook.room.home.IncomeSector
import com.seekerzhouk.accountbook.utils.ConsumptionUtil
import com.seekerzhouk.accountbook.utils.MyLog
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch

object LeanCloudOperation {

    private val tag = LeanCloudOperation::class.java.name

    /**
     * 初始化云端用户数据
     */
    fun initCloudUserForm() {
        val user = AVUser.getCurrentUser()
        CoroutineScope(Dispatchers.IO).launch {
            List(ConsumptionUtil.incomeTypeList.size - 1) {
                AVObject(IncomeSector::class.simpleName).apply {
                    put("user", user)
                    put("userName", user.username)
                    put("income_consumptionType", ConsumptionUtil.incomeTypeList[it + 1])
                    put("income_moneySum", 0F)
                }
            }.also {
                AVObject.saveAll(it)
                AVObject.saveAllInBackground(it)
            }

            List(ConsumptionUtil.expendTypeList.size - 1) {
                AVObject(ExpendSector::class.simpleName).apply {
                    put("user", user)
                    put("userName", user.username)
                    put("expend_consumptionType", ConsumptionUtil.expendTypeList[it + 1])
                    put("expend_moneySum", 0F)
                }
            }.also {
                AVObject.saveAll(it)
                AVObject.saveAllInBackground(it)
            }

            List(12) {
                AVObject(IncomePillar::class.simpleName).apply {
                    put("user", user)
                    put("userName", user.username)
                    put("income_date", (it + 1).toString().plus("月"))
                    put("income_moneySum", 0F)
                }
            }.also {
                AVObject.saveAll(it)
                AVObject.saveAllInBackground(it)
            }

            List(12) {
                AVObject(ExpendPillar::class.simpleName).apply {
                    put("user", user)
                    put("userName", user.username)
                    put("expend_date", (it + 1).toString().plus("月"))
                    put("expend_moneySum", 0F)
                }
            }.also {
                AVObject.saveAll(it)
                AVObject.saveAllInBackground(it)
            }

            AVObject(CloudFormStatus::class.simpleName).apply {
                put("user", user)
                put("userName", user.username)
                put("hasInit", true)
            }.also {
                it.save()
                it.saveInBackground()
            }

        }
    }

    /**
     * 上传/更新数据到LeanCloud
     */
    fun uploadToCloud(vararg records: Record) {
        val record = records[0]
        val date = if (records[0].date.substring(5, 6) == "0") {
            records[0].date.substring(6, 7).plus("月")
        } else {
            records[0].date.substring(5, 7).plus("月")
        }
        val user = AVUser.getCurrentUser() ?: return
        if (user.username != record.userName) {
            throw Exception("userName Inconsistent!")
        }
        uploadRecord(record, user)
        if (record.incomeOrExpend == ConsumptionUtil.INCOME) {
            updateIncomeSector(record)
            updateIncomePillar(record, date)
        } else {
            updateExpendSector(record)
            updateExpendPillar(record, date)
        }
    }

    /**
     * 更新ExpendPillar
     */
    private fun updateExpendPillar(record: Record, date: String) {
        // 先查询
        AVQuery<AVObject>(ExpendPillar::class.simpleName).apply {
            whereEqualTo("userName", record.userName)
            whereEqualTo("expend_date", date)
        }.also {
            it.firstInBackground.subscribe(object : Observer<AVObject> {
                override fun onSubscribe(d: Disposable) {
                    MyLog.i(tag, "updateExpendPillar firstInBackground onSubscribe")
                }

                override fun onNext(t: AVObject) {
                    MyLog.i(tag, "updateExpendPillar firstInBackground onNext")
                    CoroutineScope(Dispatchers.IO).launch {
                        // 再更新
                        AVObject.createWithoutData(ExpendPillar::class.simpleName, t.objectId)
                            .apply {
                                put(
                                    "expend_moneySum",
                                    t.getNumber("expend_moneySum").toFloat() + record.money
                                )
                            }.saveInBackground().subscribe(object : Observer<AVObject> {
                                override fun onSubscribe(d: Disposable) {
                                    MyLog.i(tag, "updateExpendPillar---update--onSubscribe")
                                }

                                override fun onNext(t: AVObject) {
                                    MyLog.i(tag, "updateExpendPillar---update--onNext")
                                }

                                override fun onError(e: Throwable) {
                                    MyLog.i(tag, "updateExpendPillar---update--onError", e)
                                }

                                override fun onComplete() {
                                    MyLog.i(tag, "updateExpendPillar---update--onComplete")
                                }

                            })
                    }

                }

                override fun onError(e: Throwable) {
                    MyLog.i(tag, "updateExpendPillar firstInBackground onError ", e)
                }

                override fun onComplete() {
                    MyLog.i(tag, "updateExpendPillar firstInBackground onComplete")
                }

            })
        }
    }

    /**
     * 更新IncomePillar
     */
    private fun updateIncomePillar(record: Record, date: String) {
        // 先查询
        AVQuery<AVObject>(IncomePillar::class.simpleName).apply {
            whereEqualTo("userName", record.userName)
            whereEqualTo("income_date", date)
        }.also {
            it.firstInBackground.subscribe(object : Observer<AVObject> {
                override fun onSubscribe(d: Disposable) {
                    MyLog.i(tag, "updateIncomePillar firstInBackground onSubscribe")
                }

                override fun onNext(t: AVObject) {
                    MyLog.i(tag, "updateIncomePillar firstInBackground onNext")
                    CoroutineScope(Dispatchers.IO).launch {
                        // 再更新
                        AVObject.createWithoutData(IncomePillar::class.simpleName, t.objectId)
                            .apply {
                                put(
                                    "income_moneySum",
                                    t.getNumber("income_moneySum").toFloat() + record.money
                                )
                            }.saveInBackground().subscribe(object : Observer<AVObject> {
                                override fun onSubscribe(d: Disposable) {
                                    MyLog.i(tag, "updateIncomePillar---update--onSubscribe")
                                }

                                override fun onNext(t: AVObject) {
                                    MyLog.i(tag, "updateIncomePillar---update--onNext")
                                }

                                override fun onError(e: Throwable) {
                                    MyLog.i(tag, "updateIncomePillar---update--onError", e)
                                }

                                override fun onComplete() {
                                    MyLog.i(tag, "updateIncomePillar---update--onComplete")
                                }

                            })
                    }

                }

                override fun onError(e: Throwable) {
                    MyLog.i(tag, "updateIncomePillar firstInBackground onError", e)
                }

                override fun onComplete() {
                    MyLog.i(tag, "updateIncomePillar firstInBackground onComplete")
                }

            })
        }
    }

    /**
     * 更新ExpendSector
     */
    private fun updateExpendSector(record: Record) {
        // 先查询
        AVQuery<AVObject>(ExpendSector::class.simpleName).apply {
            whereEqualTo("userName", record.userName)
            whereEqualTo("expend_consumptionType", record.secondType)
        }.also {
            it.firstInBackground.subscribe(object : Observer<AVObject> {
                override fun onSubscribe(d: Disposable) {
                    MyLog.i(tag, "updateExpendSector firstInBackground onSubscribe")
                }

                override fun onNext(t: AVObject) {
                    MyLog.i(tag, "updateExpendSector firstInBackground onNext")
                    CoroutineScope(Dispatchers.IO).launch {
                        // 再更新
                        AVObject.createWithoutData(ExpendSector::class.simpleName, t.objectId)
                            .apply {
                                put(
                                    "expend_moneySum",
                                    t.getNumber("expend_moneySum").toFloat() + record.money
                                )
                            }.saveInBackground().subscribe(object : Observer<AVObject> {
                                override fun onSubscribe(d: Disposable) {
                                    MyLog.i(tag, "updateExpendSector---update--onSubscribe")
                                }

                                override fun onNext(t: AVObject) {
                                    MyLog.i(tag, "updateExpendSector---update--onNext")
                                }

                                override fun onError(e: Throwable) {
                                    MyLog.i(tag, "updateExpendSector---update--onError", e)
                                }

                                override fun onComplete() {
                                    MyLog.i(tag, "updateExpendSector---update--onComplete")
                                }

                            })
                    }

                }

                override fun onError(e: Throwable) {
                    MyLog.i(tag, "updateExpendSector firstInBackground onError", e)
                }

                override fun onComplete() {
                    MyLog.i(tag, "updateExpendSector firstInBackground onComplete")
                }

            })
        }
    }

    /**
     * 更新IncomeSector
     */
    private fun updateIncomeSector(record: Record) {
        // 先查询
        AVQuery<AVObject>(IncomeSector::class.simpleName).apply {
            whereEqualTo("userName", record.userName)
            whereEqualTo("income_consumptionType", record.secondType)
        }.also {
            it.firstInBackground.subscribe(object : Observer<AVObject> {
                override fun onSubscribe(d: Disposable) {
                    MyLog.i(tag, "updateIncomeSector firstInBackground onSubscribe")
                }

                override fun onNext(t: AVObject) {
                    MyLog.i(tag, "updateIncomeSector firstInBackground onNext")
                    CoroutineScope(Dispatchers.IO).launch {
                        // 再更新
                        AVObject.createWithoutData(IncomeSector::class.simpleName, t.objectId)
                            .apply {
                                put(
                                    "income_moneySum",
                                    t.getNumber("income_moneySum").toFloat() + record.money
                                )
                            }.saveInBackground().subscribe(object : Observer<AVObject> {
                                override fun onSubscribe(d: Disposable) {
                                    MyLog.i(tag, "updateIncomeSector---update--onSubscribe")
                                }

                                override fun onNext(t: AVObject) {
                                    MyLog.i(tag, "updateIncomeSector---update--onNext")
                                }

                                override fun onError(e: Throwable) {
                                    MyLog.i(tag, "updateIncomeSector---update--onError", e)
                                }

                                override fun onComplete() {
                                    MyLog.i(tag, "updateIncomeSector---update--onComplete")
                                }

                            })

                    }

                }

                override fun onError(e: Throwable) {
                    MyLog.i(tag, "updateIncomeSector firstInBackground onError", e)
                }

                override fun onComplete() {
                    MyLog.i(tag, "updateIncomeSector firstInBackground onComplete")
                }

            })
        }
    }

    /**
     * 上传Record
     */
    private fun uploadRecord(record: Record, user: AVUser) {
        AVObject(Record::class.simpleName).apply {
            put("user", user)
            put("userName", record.userName)
            put("income_or_expend", record.incomeOrExpend)
            put("consumptionType", record.secondType)
            put("description", record.description)
            put("date", record.date)
            put("time", record.time)
            put("money", record.money)
        }.also {
            it.saveInBackground().subscribe(object : Observer<AVObject> {
                override fun onSubscribe(d: Disposable) {
                    MyLog.i(tag, "uploadRecord onSubscribe")
                }

                override fun onNext(t: AVObject) {
                    MyLog.i(tag, "uploadRecord onNext")
                }

                override fun onError(e: Throwable) {
                    e.printStackTrace()
                    MyLog.i(tag, "uploadRecord onError", e)
                }

                override fun onComplete() {
                    MyLog.i(tag, "uploadRecord onComplete")
                }

            })
        }
    }

    /**
     * 检查LeanCloud云端是否初始化用户数据
     */
    fun cloudUserFormHasInit(channel: Channel<Boolean>) {
        val user = AVUser.getCurrentUser()
        AVQuery<AVObject>(CloudFormStatus::class.simpleName).apply {
            whereEqualTo("userName", user.username)
        }.findInBackground().subscribe(object : Observer<List<AVObject>> {
            override fun onSubscribe(d: Disposable) {
                MyLog.i(tag, "cloudUserFormHasInit---onSubscribe")
            }

            override fun onNext(t: List<AVObject>) {
                MyLog.i(tag, "cloudUserFormHasInit---onNext")
                CoroutineScope(Dispatchers.IO).launch {
                    if (t.isEmpty()) {
                        channel.send(false)
                    } else {
                        channel.send(t[0].getBoolean("hasInit"))
                    }
                    channel.close()
                }
            }

            override fun onError(e: Throwable) {
                MyLog.i(tag, "cloudUserFormHasInit---onError", e)
                CoroutineScope(Dispatchers.IO).launch {
                    channel.send(false)
                    channel.close()
                }
            }

            override fun onComplete() {
                MyLog.i(tag, "cloudUserFormHasInit---onComplete")
            }

        })
    }
}