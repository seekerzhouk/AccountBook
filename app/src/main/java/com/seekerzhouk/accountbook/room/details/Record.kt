package com.seekerzhouk.accountbook.room.details

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
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Record

        if (userName != other.userName) return false
        if (incomeOrExpend != other.incomeOrExpend) return false
        if (secondType != other.secondType) return false
        if (description != other.description) return false
        if (date != other.date) return false
        if (time != other.time) return false
        if (money != other.money) return false
        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        var result = userName.hashCode()
        result = 31 * result + incomeOrExpend.hashCode()
        result = 31 * result + secondType.hashCode()
        result = 31 * result + description.hashCode()
        result = 31 * result + date.hashCode()
        result = 31 * result + time.hashCode()
        result = 31 * result + money.hashCode()
        result = 31 * result + id
        return result
    }


}