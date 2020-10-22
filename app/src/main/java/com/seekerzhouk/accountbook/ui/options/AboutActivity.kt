package com.seekerzhouk.accountbook.ui.options

import android.os.Bundle
import com.seekerzhouk.accountbook.R
import kotlinx.android.synthetic.main.activity_about.*

class AboutActivity : OptionActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)
        version.text = packageManager.getPackageInfo(packageName, 0).versionName
    }
}