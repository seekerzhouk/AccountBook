package com.seekerzhouk.accountbook.ui.customize

import android.content.DialogInterface
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.FragmentActivity

object CommonDialog {

    fun showDialog(
        mContext: FragmentActivity,
        title: CharSequence,
        message: CharSequence,
        block: () -> Unit
    ) {
        AlertDialog.Builder(mContext).apply {
            setTitle(title)
            setMessage(message)
            setPositiveButton("确定") { dialogInterface: DialogInterface, _: Int ->
                run(block)
                dialogInterface.dismiss()
            }
            setNegativeButton("取消") { dialogInterface: DialogInterface, _: Int ->
                dialogInterface.cancel()
            }
        }.create().show()
    }
}