package com.seekerzhouk.accountbook.repository

import cn.leancloud.AVObject
import cn.leancloud.AVQuery
import cn.leancloud.AVUser
import com.seekerzhouk.accountbook.room.UserAddInfo
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
     * 初始化云端用户的UserAddInfo
     */
    fun initCloudUserAddInfo() {
        val user = AVUser.getCurrentUser()
        CoroutineScope(Dispatchers.IO).launch {
            AVObject(UserAddInfo::class.simpleName).apply {
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
        val user = AVUser.getCurrentUser() ?: return
        if (user.username != record.userName) {
            throw Exception("userName Inconsistent!")
        }
        uploadRecord(record, user)
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
     * 检查LeanCloud云端是否初始化UserAddInfo
     */
    fun cloudUserAddInfoHasInit(channel: Channel<Boolean>) {
        val user = AVUser.getCurrentUser()
        AVQuery<AVObject>(UserAddInfo::class.simpleName).apply {
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