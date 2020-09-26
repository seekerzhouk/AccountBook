package com.seekerzhouk.accountbook.database.home

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface ExpendPillarDao {
    @Insert
    fun insertExpendPillar(vararg expendPillars: ExpendPillar)

    @Query("UPDATE ExpendPillar SET expend_moneySum = expend_moneySum + :value WHERE expend_date = :date")
    fun updateExpendPillar(date: String, value: Float)

    @Delete
    fun deleteExpendPillar(vararg expendPillars: ExpendPillar)

    @Query("select * from ExpendPillar order by id")
    fun getExpendPillars(): LiveData<List<ExpendPillar>>
}