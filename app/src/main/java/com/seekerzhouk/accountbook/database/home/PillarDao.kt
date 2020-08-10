package com.seekerzhouk.accountbook.database.home

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface PillarDao {
    @Insert
    fun insertPillar(vararg pillars: Pillar)

    @Query("UPDATE Pillar SET moneySum = moneySum + :value WHERE date = :date")
    fun updatePillar(date: String, value: Float)

    @Delete
    fun deletePillar(vararg pillars: Pillar)

    @Query("select * from Pillar order by id")
    fun getPillars(): LiveData<List<Pillar>>
}