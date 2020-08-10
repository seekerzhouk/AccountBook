package com.seekerzhouk.accountbook.database.home

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Pillar(
    @ColumnInfo(name = "date") val date: String,
    @ColumnInfo(name = "moneySum") val moneySum: Float
) {

    @PrimaryKey(autoGenerate = true)
    var id: Int = 0

}