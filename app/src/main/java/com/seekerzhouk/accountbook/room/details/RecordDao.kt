package com.seekerzhouk.accountbook.room.details

import androidx.lifecycle.LiveData
import androidx.paging.DataSource
import androidx.room.*

@Dao
interface RecordDao {

    @Insert
    fun insertRecords(vararg records: Record)

    @Update
    fun updateRecords(vararg records: Record)

    @Delete
    fun deleteRecords(vararg records: Record)

    @Query("DELETE FROM Record WHERE userName is :userName")
    fun clearUserRecords(userName: String)

    /**
     * 查找 所有记录
     */
    @Query("select * from Record where userName is :userName order by dateTime desc")
    fun loadAllRecords(userName: String): DataSource.Factory<Int, Record>

    /**
     * 关键字 查找记录
     */
    @Query("select * from Record where userName is :userName and description like :patten order by dateTime desc")
    fun findRecordWithPatten(userName: String, patten: String): DataSource.Factory<Int, Record>

    /**
     * 查找所有 收入记录
     */
    @Query("select * from Record where userName is :userName and incomeOrExpend is '收入' order by dateTime desc")
    fun findIncomeRecords(userName: String): DataSource.Factory<Int, Record>

    /**
     * 查找所有 支出记录
     */
    @Query("select * from Record where userName is :userName and incomeOrExpend is '支出' order by dateTime desc")
    fun findExpendRecords(userName: String): DataSource.Factory<Int, Record>

    /**
     * 关键字 查找 收入记录
     */
    @Query("select * from record where userName is :userName and incomeOrExpend is '收入' and description like :patten order by dateTime desc")
    fun findIncomeRecordsWithPatten(
        userName: String,
        patten: String
    ): DataSource.Factory<Int, Record>

    /**
     * 关键字 查找 支出记录
     */
    @Query("select * from record where userName is :userName and incomeOrExpend is '支出' and description like :patten order by dateTime desc")
    fun findExpendRecordsWithPatten(
        userName: String,
        patten: String
    ): DataSource.Factory<Int, Record>

    /**
     * 根据 类型 查找 收入记录
     */
    @Query("select * from record where userName is :userName and incomeOrExpend is '收入' and consumptionType is :selectedType order by dateTime desc")
    fun findIncomeRecordsBySelectedType(
        userName: String,
        selectedType: String
    ): DataSource.Factory<Int, Record>

    /**
     * 根据 类型 查找 支出记录
     */
    @Query("select * from record where userName is :userName and incomeOrExpend is '支出' and consumptionType is :selectedType order by dateTime desc")
    fun findExpendRecordsBySelectedType(
        userName: String,
        selectedType: String
    ): DataSource.Factory<Int, Record>

    /**
     * 根据 类型 和 关键字 查找 收入记录
     */
    @Query("select * from record where userName is :userName and incomeOrExpend is '收入' and consumptionType is :selectedType and description like :patten order by dateTime desc")
    fun findIncomeRecordsBySelectedTypeWithPatten(
        userName: String,
        selectedType: String,
        patten: String
    ): DataSource.Factory<Int, Record>

    /**
     * 根据 类型 和关键字 查找 收入记录
     */
    @Query("select * from record where userName is :userName and incomeOrExpend is '支出' and consumptionType is :selectedType and description like :patten order by dateTime desc")
    fun findExpendRecordsBySelectedTypeWithPatten(
        userName: String,
        selectedType: String,
        patten: String
    ): DataSource.Factory<Int, Record>

    /**
     * 查找所有 收入记录
     */
    @Query("select * from Record where userName is :userName and incomeOrExpend is '收入'")
    fun loadIncomeRecords(userName: String): LiveData<List<Record>>

    /**
     * 查找所有 支出记录
     */
    @Query("select * from Record where userName is :userName and incomeOrExpend is '支出'")
    fun loadExpendRecords(userName: String): LiveData<List<Record>>

    /**
     * 查找具体月份 收入记录
     */
    @Query("select * from Record where userName is :userName and incomeOrExpend is '收入' AND substr(dateTime,1,8) = :specificMonth")
    fun loadIncomeRecordsByMonth(userName: String, specificMonth: String): LiveData<List<Record>>

    /**
     * 查找具体月份 支出记录
     */
    @Query("select * from Record where userName is :userName and incomeOrExpend is '支出' AND substr(dateTime,1,8) = :specificMonth")
    fun loadExpendRecordsByMonth(userName: String,  specificMonth: String): LiveData<List<Record>>

    /**
     * 查找具体年份 收入记录
     */
    @Query("select * from Record where userName is :userName and incomeOrExpend is '收入' AND substr(dateTime,1,5) = :year")
    fun loadIncomeRecordsByYear(userName: String, year: String): LiveData<List<Record>>

    /**
     * 查找具体年份 支出记录
     */
    @Query("select * from Record where userName is :userName and incomeOrExpend is '支出' AND substr(dateTime,1,5) = :year")
    fun loadExpendRecordsByYear(userName: String, year: String): LiveData<List<Record>>
}