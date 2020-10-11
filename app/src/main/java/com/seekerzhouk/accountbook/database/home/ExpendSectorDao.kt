package com.seekerzhouk.accountbook.database.home

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface ExpendSectorDao {
    @Insert
    fun insertExpendSectors(vararg sectors: ExpendSector)

    @Query("update ExpendSector set expend_moneySum = expend_moneySum + :value WHERE userName is :userName and expend_consumptionType = :type")
    fun updateExpendSectors(userName: String, type: String, value: Double)

    @Delete
    fun deleteExpendSectors(vararg sectors: ExpendSector)

    @Query("select * from ExpendSector WHERE userName is :userName and expend_moneySum > 0.0")
    fun getExpendSectors(userName: String): LiveData<List<ExpendSector>>

    @Query("UPDATE ExpendSector SET expend_moneySum = 0.00 WHERE userName is :userName")
    fun clearExpendSectors(userName: String)
}