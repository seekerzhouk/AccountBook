package com.seekerzhouk.accountbook.ui.customize

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import androidx.cardview.widget.CardView
import com.seekerzhouk.accountbook.R
import com.seekerzhouk.accountbook.databinding.MyProgressBarBinding
import kotlinx.coroutines.*

class MyProgressBar : CardView {
    private var binding: MyProgressBarBinding =
        MyProgressBarBinding.inflate(LayoutInflater.from(context),this,true)

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        binding.stateImage.layoutParams.apply {
            width = binding.progressBar.width
            height = binding.progressBar.height
        }
        this.visibility = View.INVISIBLE
    }

    fun onJobFinished(finishDescription: String): MyProgressBar {
        binding.stateImage.setImageResource(R.drawable.ic_finish)
        binding.stateImage.visibility = View.VISIBLE
        binding.progressBar.visibility = View.INVISIBLE
        setDescription(finishDescription)
        return this
    }

    fun onJobError(errorDescription: String): MyProgressBar {
        binding.stateImage.setImageResource(R.drawable.ic_fail)
        binding.stateImage.visibility = View.VISIBLE
        binding.progressBar.visibility = View.INVISIBLE
        setDescription(errorDescription)
        return this
    }

    private fun setDescription(string: String) {
        binding.description.text = string
    }

    fun show(startDescription: String) {
        setDescription(startDescription)
        binding.stateImage.visibility = View.INVISIBLE
        binding.progressBar.visibility = View.VISIBLE
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