package com.seekerzhouk.accountbook.room.home

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class IncomePillar(
    @ColumnInfo(name = "userName") var userName: String,
    @ColumnInfo(name = "income_date") override var date: String,
    @ColumnInfo(name = "income_moneySum") override var moneySum: Float
) : Pillar(date, moneySum) {

    @PrimaryKey(autoGenerate = true)
    var id: Int = 0

}