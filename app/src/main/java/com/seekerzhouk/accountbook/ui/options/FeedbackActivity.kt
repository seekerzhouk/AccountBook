package com.seekerzhouk.accountbook.ui.options

import android.os.Bundle
import android.widget.Toast
import cn.leancloud.AVObject
import cn.leancloud.AVUser
import com.seekerzhouk.accountbook.R
import com.seekerzhouk.accountbook.room.Feedback
import kotlinx.android.synthetic.main.activity_feed_back.*

class FeedbackActivity : OptionActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_feed_back)
        feedback_submit.setOnClickListener {
            if (editTextTextMultiLine.text.toString().isEmpty()) {
                Toast.makeText(
                    this,
                    getString(R.string.feedback_without_description),
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }
            submitFeedback()
            Toast.makeText(this, getString(R.string.bg_submit), Toast.LENGTH_SHORT).show()
        }
    }

    private fun submitFeedback() {
        AVObject(Feedback::class.simpleName).apply {
            put("user", AVUser.getCurrentUser())
            put("userName", AVUser.getCurrentUser().username)
            put("description", editTextTextMultiLine.text.toString())
            saveEventually()
        }
    }
}