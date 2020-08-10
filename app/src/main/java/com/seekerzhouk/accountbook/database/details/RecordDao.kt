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
    @Query("select * from Record order by id desc")
    fun loadAllRecords(): LiveData<List<Record>>

    /**
     * 关键字 查找记录
     */
    @Query("select * from Record where description like :patten order by id desc")
    fun findRecordWithPatten(patten: String): LiveData<List<Record>>

    /**
     * 查找所有 收入记录
     */
    @Query("select * from Record where income_or_expend is '收入' order by id desc")
    fun findIncomeRecords(): LiveData<List<Record>>

    /**
     * 查找所有 支出记录
     */
    @Query("select * from Record where income_or_expend is '支出' order by id desc")
    fun findExpendRecords(): LiveData<List<Record>>

    /**
     * 关键字 查找 收入记录
     */
    @Query("select * from record where income_or_expend is '收入' and description like :patten order by id desc")
    fun findIncomeRecordsWithPatten(patten: String): LiveData<List<Record>>

    /**
     * 关键字 查找 支出记录
     */
    @Query("select * from record where income_or_expend is '支出' and description like :patten order by id desc")
    fun findExpendRecordsWithPatten(patten: String): LiveData<List<Record>>

    /**
     * 根据 类型 查找 收入记录
     */
    @Query("select * from record where income_or_expend is '收入' and consumptionType is :selectedType order by id desc")
    fun findIncomeRecordsBySelectedType(selectedType: String): LiveData<List<Record>>

    /**
     * 根据 类型 查找 支出记录
     */
    @Query("select * from record where income_or_expend is '支出' and consumptionType is :selectedType order by id desc")
    fun findExpendRecordsBySelectedType(selectedType: String): LiveData<List<Record>>

    /**
     * 根据 类型 和 关键字 查找 收入记录
     */
    @Query("select * from record where income_or_expend is '收入' and consumptionType is :selectedType and description like :patten order by id desc")
    fun findIncomeRecordsBySelectedTypeWithPatten(
        selectedType: String,
        patten: String
    ): LiveData<List<Record>>

    /**
     * 根据 类型 和关键字 查找 收入记录
     */
    @Query("select * from record where income_or_expend is '支出' and consumptionType is :selectedType and description like :patten order by id desc")
    fun findExpendRecordsBySelectedTypeWithPatten(
        selectedType: String,
        patten: String
    ): LiveData<List<Record>>

}