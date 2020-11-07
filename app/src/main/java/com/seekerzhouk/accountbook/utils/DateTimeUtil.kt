package com.seekerzhouk.accountbook.utils

import android.content.Context
import android.os.Build
import java.text.SimpleDateFormat
import java.util.*

object DateTimeUtil {
    /**
     * 返回当前月份 yyyy年MM月
     */
    fun getCurrentSpecificMonth(): String {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        return year.toString().plus("年").plus(if (month + 1 < 10) "0" else "").plus(month + 1)
            .plus("月")
    }

    /**
     * 返回当前日期 yyyy年MM月dd日
     */
    fun getCurrentSpecificDate(): String {
        val calendar = Calendar.getInstance()
        val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)
        return getCurrentSpecificMonth().plus(if (dayOfMonth < 10) "0" else "").plus(dayOfMonth).plus("日")
    }

    /**
     * 返回当前时间 HH:mm:ss
     */
    fun getCurrentTime():String {
        return SimpleDateFormat.getTimeInstance(2).format(System.currentTimeMillis())
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

    /**
     * 返回"yyyy年mm月dd日"格式的日期
     * @return "yyyy年mm月dd日"格式的日期
     */
    fun getPickerDate(year: Int, month: Int, dayOfMonth: Int): String {
        return year.toString().plus("年")
            .plus(if (month + 1 < 10) 0 else "").plus(month + 1).plus("月")
            .plus(if (dayOfMonth < 10) 0 else "").plus(dayOfMonth).plus("日")
    }

    /**
     * 返回"hh:mm:00"格式的时间
     * @return "hh:mm:00"格式的时间
     */
    fun getPickerTime(hourOfDay: Int, minute: Int): String {
        return (if (hourOfDay < 10) "0" else "").plus(hourOfDay)
            .plus(":")
            .plus(if (minute < 10) 0 else "").plus(minute).plus(":00")
    }
}