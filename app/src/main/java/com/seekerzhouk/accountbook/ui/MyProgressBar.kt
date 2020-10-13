package com.seekerzhouk.accountbook.ui

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
    }

    fun onJobFinished(finishDescription:String) {
        imageView_inside.visibility = View.VISIBLE
        progressBar_inside.visibility = View.INVISIBLE
        setDescription(finishDescription)
        CoroutineScope(Dispatchers.IO).launch {
            delay(1000)
            withContext(Dispatchers.Main){
               this@MyProgressBar. visibility = View.INVISIBLE
            }
        }
    }

    private fun setDescription(string: String) {
        textView_inside.text = string
    }

    fun show(startDescription:String) {
        setDescription(startDescription)
        imageView_inside.visibility = View.INVISIBLE
        progressBar_inside.visibility = View.VISIBLE
        this.visibility = View.VISIBLE
    }

}