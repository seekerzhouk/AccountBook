package com.seekerzhouk.accountbook.database.home

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class IncomePillar(
    @ColumnInfo(name = "income_date") override var date: String,
    @ColumnInfo(name = "income_moneySum") override var moneySum: Float
) : Pillar(date, moneySum) {

    @PrimaryKey(autoGenerate = true)
    var id: Int = 0

}