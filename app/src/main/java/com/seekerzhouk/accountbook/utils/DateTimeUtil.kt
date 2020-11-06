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
     * 获取具体某个月有多少天
     */
    fun getDaysOfMonth(year: String, month: String): Int {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.YEAR, year.substring(0, year.length - 1).toInt())
        calendar.set(Calendar.MONTH, month.substring(0, month.length - 1).toInt() - 1)
        calendar.set(Calendar.DATE, 1)
        calendar.roll(Calendar.DATE, -1)
        return calendar.get(Calendar.DATE)
    }
}