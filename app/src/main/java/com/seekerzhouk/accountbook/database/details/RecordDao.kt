package com.seekerzhouk.accountbook.database.details

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface RecordDao {

    @Insert
    fun insertRecords(vararg records: Record)

    @Update
    fun updateRecords(vararg records: Record)

    @Delete
    fun deleteRecords(vararg records: Record)

    /**
     * 查找 所有记录
     */
    @Query("select * from Record where userName is :userName order by id desc")
    fun loadAllRecords(userName: String): LiveData<List<Record>>

    /**
     * 关键字 查找记录
     */
    @Query("select * from Record where userName is :userName and description like :patten order by id desc")
    fun findRecordWithPatten(userName: String, patten: String): LiveData<List<Record>>

    /**
     * 查找所有 收入记录
     */
    @Query("select * from Record where userName is :userName and income_or_expend is '收入' order by id desc")
    fun findIncomeRecords(userName: String): LiveData<List<Record>>

    /**
     * 查找所有 支出记录
     */
    @Query("select * from Record where userName is :userName and income_or_expend is '支出' order by id desc")
    fun findExpendRecords(userName: String): LiveData<List<Record>>

    /**
     * 关键字 查找 收入记录
     */
    @Query("select * from record where userName is :userName and income_or_expend is '收入' and description like :patten order by id desc")
    fun findIncomeRecordsWithPatten(userName: String, patten: String): LiveData<List<Record>>

    /**
     * 关键字 查找 支出记录
     */
    @Query("select * from record where userName is :userName and income_or_expend is '支出' and description like :patten order by id desc")
    fun findExpendRecordsWithPatten(userName: String, patten: String): LiveData<List<Record>>

    /**
     * 根据 类型 查找 收入记录
     */
    @Query("select * from record where userName is :userName and income_or_expend is '收入' and consumptionType is :selectedType order by id desc")
    fun findIncomeRecordsBySelectedType(
        userName: String,
        selectedType: String
    ): LiveData<List<Record>>

    /**
     * 根据 类型 查找 支出记录
     */
    @Query("select * from record where userName is :userName and income_or_expend is '支出' and consumptionType is :selectedType order by id desc")
    fun findExpendRecordsBySelectedType(
        userName: String,
        selectedType: String
    ): LiveData<List<Record>>

    /**
     * 根据 类型 和 关键字 查找 收入记录
     */
    @Query("select * from record where userName is :userName and income_or_expend is '收入' and consumptionType is :selectedType and description like :patten order by id desc")
    fun findIncomeRecordsBySelectedTypeWithPatten(
        userName: String,
        selectedType: String,
        patten: String
    ): LiveData<List<Record>>

    /**
     * 根据 类型 和关键字 查找 收入记录
     */
    @Query("select * from record where userName is :userName and income_or_expend is '支出' and consumptionType is :selectedType and description like :patten order by id desc")
    fun findExpendRecordsBySelectedTypeWithPatten(
        userName: String,
        selectedType: String,
        patten: String
    ): LiveData<List<Record>>

}