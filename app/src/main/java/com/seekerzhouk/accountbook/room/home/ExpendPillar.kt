package com.seekerzhouk.accountbook.room.home

data class ExpendPillar(
    override var month: String,
    override var moneySum: Float
) : Pillar(month, moneySum) {

}