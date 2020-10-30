package com.seekerzhouk.accountbook.room.home

data class ExpendSector(
override var consumptionType: String,
override var moneySum: Double
) :
    Sector(consumptionType, moneySum) {
}