package com.seekerzhouk.accountbook.room.home

data class IncomePillar(
    override var month: String,
    override var moneySum: Float
) : Pillar(month, moneySum) {
}