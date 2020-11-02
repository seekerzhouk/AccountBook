package com.seekerzhouk.accountbook.ui.options

import android.os.Bundle
import com.seekerzhouk.accountbook.databinding.ActivityAboutBinding

class AboutActivity : OptionActivity() {
    private lateinit var binding: ActivityAboutBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAboutBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.version.text = packageManager.getPackageInfo(packageName, 0).versionName
    }
}