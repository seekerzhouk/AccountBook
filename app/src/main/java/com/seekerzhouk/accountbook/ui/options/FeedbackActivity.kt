package com.seekerzhouk.accountbook.ui.options

import android.os.Bundle
import android.widget.Toast
import cn.leancloud.AVObject
import cn.leancloud.AVUser
import com.seekerzhouk.accountbook.R
import com.seekerzhouk.accountbook.databinding.ActivityFeedBackBinding
import com.seekerzhouk.accountbook.room.Feedback

class FeedbackActivity : OptionActivity() {
    private lateinit var binding: ActivityFeedBackBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFeedBackBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.feedbackSubmit.setOnClickListener {
            if (binding.feedbackText.text.toString().isEmpty()) {
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
            put("description", binding.feedbackText.text.toString())
            saveEventually()
        }
    }
}