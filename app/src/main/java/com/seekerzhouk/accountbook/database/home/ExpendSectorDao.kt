package com.seekerzhouk.accountbook.database.home

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface ExpendSectorDao {
    @Insert
    fun insertExpendSectors(vararg sectors: ExpendSector)

    @Query("update ExpendSector set expend_moneySum = expend_moneySum + :value where expend_consumptionType = :type")
    fun updateExpendSectors(type: String, value: Double)

    @Delete
    fun deleteExpendSectors(vararg sectors: ExpendSector)

    @Query("select * from ExpendSector where expend_moneySum > 0.0")
    fun getExpendSectors(): LiveData<List<ExpendSector>>
}