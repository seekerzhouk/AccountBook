package com.seekerzhouk.accountbook.ui.customize

import android.app.DatePickerDialog
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.annotation.RequiresApi

@RequiresApi(Build.VERSION_CODES.N)
class YearPickerDialog(context: Context, themeResId: Int) : DatePickerDialog(context, themeResId) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val dayPickView = this.findViewById<View>(context.resources.getIdentifier("android:id/day", null, null))
        if (dayPickView != null) {
            dayPickView.visibility = View.GONE
        }
        val monthPickView = this.findViewById<View>(context.resources.getIdentifier("android:id/month", null, null))
        if (monthPickView != null) {
            monthPickView.visibility = View.GONE
        }
    }
}