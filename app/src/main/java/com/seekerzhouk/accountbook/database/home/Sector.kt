package com.seekerzhouk.accountbook.database.home

abstract class Sector(
    open var consumptionType: String,
    open var moneySum: Double
) {
    var percentage: Float = 0F

    var color: Int = 0

    var angle: Float = 0F

}