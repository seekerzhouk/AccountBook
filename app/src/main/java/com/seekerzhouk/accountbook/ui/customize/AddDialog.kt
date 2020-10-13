package com.seekerzhouk.accountbook.ui.customize

import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import com.seekerzhouk.accountbook.R
import com.seekerzhouk.accountbook.room.details.Record
import com.seekerzhouk.accountbook.utils.ConsumptionUtil
import com.seekerzhouk.accountbook.utils.SharedPreferencesUtil
import com.seekerzhouk.accountbook.viewmodel.DetailsViewModel
import java.util.*
import kotlin.collections.ArrayList

object AddDialog {

    fun show(mContext: FragmentActivity) {
        val builder = AlertDialog.Builder(mContext)
        val view = View.inflate(mContext, R.layout.add_dialog, null)
        builder.setView(view)
        val dialog = builder.create()
        dialog.setCanceledOnTouchOutside(false)

        val firstSpinner: Spinner = view.findViewById(R.id.first_spinner)
        val secondSpinner: Spinner = view.findViewById(R.id.second_spinner)
        val editTextDescription: EditText = view.findViewById(R.id.editText_description)
        val editTextSum: EditText = view.findViewById(R.id.editText_sum)
        val buttonCancel: Button = view.findViewById(R.id.button_cancel)
        val buttonSubmit: Button = view.findViewById(R.id.button_submit)

        // 设置两个spinner
        val firstTypeList = ArrayList<String>()
        for (i in ConsumptionUtil.fistTypeList) {
            firstTypeList.add(i)
        }
        firstTypeList.remove(ConsumptionUtil.ALL)
        firstSpinner.adapter = getFistAdapter(mContext, firstTypeList)
        firstSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val firstType = firstTypeList[position]
                secondSpinner.adapter = getSecondAdapter(mContext, firstType)
            }
        }

        //editText_sum 设置输入监听
        editTextSum.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                buttonSubmit.isEnabled = editTextSum.text.trim().isNotEmpty()
            }
        })

        buttonCancel.setOnClickListener {
            dialog.cancel()
        }

        buttonSubmit.setOnClickListener {
            val detailsViewModel = ViewModelProvider(mContext).get(DetailsViewModel::class.java)
            val record = Record(
                SharedPreferencesUtil.getUserName(mContext),
                firstSpinner.selectedItem.toString(),
                secondSpinner.selectedItem.toString(),
                editTextDescription.text.toString(),
                getCalendarDate(),
                getCalendarTime(),
                editTextSum.text.toString().toDouble()
            )
            detailsViewModel.insertRecords(record)
            dialog.dismiss()
        }
        dialog.show()
    }

    private fun getSecondAdapter(
        context: FragmentActivity,
        firstType: String
    ): ArrayAdapter<String> {
        val secondTypeList = ArrayList<String>()
        if (firstType == ConsumptionUtil.INCOME) {
            for (string in ConsumptionUtil.incomeTypeList) {
                secondTypeList.add(string)
            }
        } else {
            for (string in ConsumptionUtil.expendTypeList) {
                secondTypeList.add(string)
            }
        }
        secondTypeList.remove(ConsumptionUtil.ALL)
        val secondAdapter =
            ArrayAdapter(context, R.layout.my_spinner_item, secondTypeList)
        secondAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        return secondAdapter
    }

    private fun getFistAdapter(
        context: FragmentActivity,
        firstTypeList: ArrayList<String>
    ): ArrayAdapter<String> {
        val fistAdapter =
            ArrayAdapter(
                context,
                R.layout.my_spinner_item,
                firstTypeList
            )
        fistAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        return fistAdapter
    }

    private fun getCalendarDate(): String {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR).toString()
        var month = (calendar.get(Calendar.MONTH) + 1).toString()
        val day = calendar.get(Calendar.DAY_OF_MONTH).toString()
        if (month.length == 1) {
            month = "0$month"
        }
        return "$year-$month-$day"
    }

    private fun getCalendarTime(): String {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY).toString()
        var minute = calendar.get(Calendar.MINUTE).toString()
        val second = calendar.get(Calendar.SECOND).toString()
        if (minute.length == 1) {
            minute = "0$minute"
        }
        return "$hour:$minute:$second"
    }
}


