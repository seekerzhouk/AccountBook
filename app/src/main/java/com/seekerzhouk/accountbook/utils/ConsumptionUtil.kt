package com.seekerzhouk.accountbook.utils

import com.seekerzhouk.accountbook.R
import com.seekerzhouk.accountbook.room.details.Record

object ConsumptionUtil {
    const val ALL = "全部"

    const val INCOME = "收入"
    const val EXPEND = "支出"

    private const val SALARY = "薪水"
    private const val BONUS = "奖金"
    private const val INVESTMENT = "投资"
    private const val SIDELINE = "副业"

    private const val CLOTHES = "衣服"
    private const val FOOD = "饮食"
    private const val PHONE = "手机"
    private const val ENTERTAINMENT = "娱乐"
    private const val SMOKE_WINE = "烟酒"
    private const val STUDY = "学习"
    private const val TOUR = "旅游"
    private const val TRAFFIC = "交通"
    private const val HOUSE_ROOM = "居住"
    private const val FUEL = "燃油"
    private const val MAKEUP = "化妆"
    private const val MEDICINE = "医疗"
    private const val DONATE = "捐赠"
    private const val OTHER = "其它"

    val tagAllList = ArrayList<String>()
    val fistTypeList = ArrayList<String>()
    val incomeTypeList = ArrayList<String>()
    val expendTypeList = ArrayList<String>()

    init {
        tagAllList.add(
            ALL
        )

        with(fistTypeList) {
            add(ALL)
            add(INCOME)
            add(EXPEND)
        }

        with(incomeTypeList) {
            add(ALL)
            add(SALARY)
            add(BONUS)
            add(INVESTMENT)
            add(SIDELINE)
        }
        with(expendTypeList) {
            add(ALL)
            add(CLOTHES)
            add(FOOD)
            add(PHONE)
            add(ENTERTAINMENT)
            add(SMOKE_WINE)
            add(STUDY)
            add(TOUR)
            add(TRAFFIC)
            add(HOUSE_ROOM)
            add(FUEL)
            add(MAKEUP)
            add(MEDICINE)
            add(DONATE)
            add(OTHER)
        }
    }

    fun getIconType(record: Record): Int {
        return when (record.secondType) {
            SALARY -> R.drawable.ic_salary
            BONUS -> R.drawable.ic_bonus
            INVESTMENT -> R.drawable.ic_investment
            SIDELINE -> R.drawable.ic_sideline
            CLOTHES -> R.drawable.ic_clothes
            FOOD -> R.drawable.ic_food
            PHONE -> R.drawable.ic_phone
            ENTERTAINMENT -> R.drawable.ic_entertainment
            SMOKE_WINE -> R.drawable.ic_smoke_wine
            STUDY -> R.drawable.ic_study
            TOUR -> R.drawable.ic_tour
            TRAFFIC -> R.drawable.ic_traffic
            HOUSE_ROOM -> R.drawable.ic_house_room
            FUEL -> R.drawable.ic_fuel
            MAKEUP -> R.drawable.ic_makeup
            MEDICINE -> R.drawable.ic_medicine
            DONATE -> R.drawable.ic_donate
            OTHER -> R.drawable.ic_other
            else -> R.drawable.ic_other
        }
    }
}