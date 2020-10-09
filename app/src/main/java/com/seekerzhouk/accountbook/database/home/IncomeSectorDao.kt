package com.seekerzhouk.accountbook.database.home

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface IncomeSectorDao {
    @Insert
    fun insertIncomeSectors(vararg sectors: IncomeSector)

    @Query("update IncomeSector set income_moneySum = income_moneySum + :value WHERE userName is :userName and income_consumptionType = :type")
    fun updateIncomeSectors(userName: String, type: String, value: Double)

    @Delete
    fun deleteIncomeSectors(vararg sectors: IncomeSector)

    @Query("select * from IncomeSector WHERE userName is :userName and income_moneySum > 0.0")
    fun getIncomeSectors(userName: String): LiveData<List<IncomeSector>>
}