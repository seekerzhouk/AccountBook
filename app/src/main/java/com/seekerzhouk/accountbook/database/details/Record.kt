package com.seekerzhouk.accountbook.database.details

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Record(
    @ColumnInfo(name = "userName") var userName: String,
    @ColumnInfo(name = "income_or_expend") var incomeOrExpend: String,
    @ColumnInfo(name = "consumptionType") val secondType: String,
    @ColumnInfo(name = "description") val description: String,
    @ColumnInfo(name = "date") val date: String,
    @ColumnInfo(name = "time") val time: String,
    @ColumnInfo(name = "money") val money: Double
) {

    @PrimaryKey(autoGenerate = true)
    var id: Int = 0

}