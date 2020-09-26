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

    @Query("UPDATE IncomePillar SET income_moneySum = income_moneySum + :value WHERE income_date = :date")
    fun updateIncomePillar(date: String, value: Float)

    @Delete
    fun deleteIncomePillar(vararg incomePillars: IncomePillar)

    @Query("select * from IncomePillar order by id")
    fun getIncomePillars(): LiveData<List<IncomePillar>>
}