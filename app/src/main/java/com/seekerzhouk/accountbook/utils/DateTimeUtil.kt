package com.seekerzhouk.accountbook.utils

import android.content.Context
import android.os.Build
import java.text.SimpleDateFormat
import java.util.*

object DateTimeUtil {
    fun getCurrentDateTime(context: Context): String {
        val date = Date(System.currentTimeMillis())
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            SimpleDateFormat(
                "yyyy年MM月dd日 HH:mm:ss",
                context.resources.configuration.locales.get(0)
            ).format(date)
        } else {
            SimpleDateFormat(
                "yyyy年MM月dd日 HH:mm:ss",
                context.resources.configuration.locale
            ).format(date)
        }
    }

    /**
     * 返回月份 yyyy年MM月
     */
    fun getSpecificMonth(): String {
        val calendar = Calendar.getInstance()
        return if (calendar.get(Calendar.MONTH) + 1 < 10) {
            calendar.get(Calendar.YEAR).toString().plus("年").plus(0)
                .plus(calendar.get(Calendar.MONTH) + 1).plus("月")
        } else {
            calendar.get(Calendar.YEAR).toString().plus("年").plus(calendar.get(Calendar.MONTH) + 1)
                .plus("月")
        }
    }

    /**
     * 返回月份数字
     */
    fun getCurrentMonth(): Int {
        val calendar = Calendar.getInstance()
        return calendar.get(Calendar.MONTH) + 1
    }

    /**
     * 返回日期数字
     */
    fun getCurrentDayOfMonth(): Int {
        val calendar = Calendar.getInstance()
        return calendar.get(Calendar.DAY_OF_MONTH)
    }

    /**
     * 返回年份 yyyy年
     */
    fun getCurrentYear(): String {
        val calendar = Calendar.getInstance()
        return calendar.get(Calendar.YEAR).toString().plus("年")
    }

    /**
     * @param pattern the pattern describing the date and time format.
     * must be "yyyy年MM月dd日 HH:mm:ss".
     */
    fun getYear(pattern: String): String {
        return pattern.substring(0, 5)
    }

    /**
     * @param pattern the pattern describing the date and time format.
     * must be "yyyy年MM月dd日 HH:mm:ss".
     */
    fun getMonth(pattern: String): String {
        val month = pattern.substring(5, 8)
        return if (month.substring(0, 1) == "0") month.removeRange(0, 1) else month
    }

    /**
     * @param pattern the pattern describing the date and time format.
     * must be "yyyy年MM月dd日 HH:mm:ss".
     */
    fun getDayOfMonth(pattern: String): String {
        val day = pattern.substring(8, 11)
        return if (day.substring(0, 1) == "0") day.removeRange(0, 1) else day
    }

    /**
     * @param pattern the pattern describing the date and time format.
     * must be "yyyy年MM月dd日 HH:mm:ss".
     */
    fun getTime(pattern: String): String {
        return pattern.substring(12)
    }

    /**
     * 获取具体某个月{yyyy年MM月}有多少天
     */
    fun getDaysOfMonth(specificMonth: String): Int {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.YEAR, specificMonth.substring(0, 4).toInt())
        calendar.set(Calendar.MONTH, specificMonth.substring(5, 7).toInt() - 1)
        calendar.set(Calendar.DATE, 1)
        calendar.roll(Calendar.DATE, -1)
        return calendar.get(Calendar.DATE)
    }
}