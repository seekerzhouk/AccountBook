package com.seekerzhouk.accountbook.room.home

data class IncomeSector(
override var consumptionType: String,
override var moneySum: Double
) : Sector(consumptionType, moneySum) {
}