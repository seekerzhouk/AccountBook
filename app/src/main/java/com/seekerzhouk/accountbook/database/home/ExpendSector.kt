package com.seekerzhouk.accountbook.database.home

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class ExpendSector(
    @ColumnInfo(name = "userName") var userName: String,
    @ColumnInfo(name = "expend_consumptionType") override var consumptionType: String,
    @ColumnInfo(name = "expend_moneySum") override var moneySum: Double
) :
    Sector(consumptionType, moneySum) {

    @PrimaryKey(autoGenerate = true)
    var id: Int = 0

//    @ColumnInfo(name = "expend_percentage")
//    override var percentage: Float = 0F
//
//    @ColumnInfo(name = "expend_color")
//    override var color: Long = 0
//
//    @ColumnInfo(name = "expend_angle")
//    override var angle: Float = 0F
}