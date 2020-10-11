package com.seekerzhouk.accountbook.database.home

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface IncomePillarDao {
    @Insert
    fun insertIncomePillar(vararg incomePillars: IncomePillar)

    @Query("UPDATE IncomePillar SET income_moneySum = income_moneySum + :value WHERE userName is :userName and income_date = :date")
    fun updateIncomePillar(userName: String,date: String, value: Float)

    @Delete
    fun deleteIncomePillar(vararg incomePillars: IncomePillar)

    @Query("select * from IncomePillar WHERE userName is :userName order by id")
    fun getIncomePillars(userName: String): LiveData<List<IncomePillar>>

    @Query("UPDATE IncomePillar SET income_moneySum = 0.00 WHERE userName is :userName")
    fun clearIncomePillars(userName: String)
}