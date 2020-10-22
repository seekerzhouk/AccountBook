package com.seekerzhouk.accountbook.ui.customize

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import androidx.cardview.widget.CardView
import com.seekerzhouk.accountbook.R
import kotlinx.android.synthetic.main.my_progress_bar.view.*
import kotlinx.coroutines.*

class MyProgressBar : CardView {

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        LayoutInflater.from(context).inflate(R.layout.my_progress_bar, this)
        imageView_inside.layoutParams.apply {
            width = progressBar_inside.width
            height = progressBar_inside.height
        }
        this.visibility = View.INVISIBLE
    }

    fun onJobFinished(finishDescription: String): MyProgressBar {
        imageView_inside.setImageResource(R.drawable.ic_finish)
        imageView_inside.visibility = View.VISIBLE
        progressBar_inside.visibility = View.INVISIBLE
        setDescription(finishDescription)
        return this
    }

    fun onJobError(errorDescription: String): MyProgressBar {
        imageView_inside.setImageResource(R.drawable.ic_fail)
        imageView_inside.visibility = View.VISIBLE
        progressBar_inside.visibility = View.INVISIBLE
        setDescription(errorDescription)
        return this
    }

    private fun setDescription(string: String) {
        textView_inside.text = string
    }

    fun show(startDescription: String) {
        setDescription(startDescription)
        imageView_inside.visibility = View.INVISIBLE
        progressBar_inside.visibility = View.VISIBLE
        this.visibility = View.VISIBLE
    }

    fun dismiss() {
        this.visibility = View.INVISIBLE
    }

    fun laterDismiss(timeMillis: Long, block: () -> Unit) {
        CoroutineScope(Dispatchers.Default).launch {
            delay(timeMillis)
            withContext(Dispatchers.Main) {
                this@MyProgressBar.dismiss()
                run(block)
            }
        }
    }

}