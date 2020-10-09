package com.seekerzhouk.accountbook.database.home

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class IncomeSector(
    @ColumnInfo(name = "userName") var userName: String,
    @ColumnInfo(name = "income_consumptionType") override var consumptionType: String,
    @ColumnInfo(name = "income_moneySum") override var moneySum: Double
) : Sector(consumptionType, moneySum) {

    @PrimaryKey(autoGenerate = true)
    var id: Int = 0

//    @ColumnInfo(name = "income_percentage")
//    override var percentage: Float = 0F
//
//    @ColumnInfo(name = "income_color")
//    override var color: Long = 0
//
//    @ColumnInfo(name = "income_angle")
//    override var angle: Float = 0F
}