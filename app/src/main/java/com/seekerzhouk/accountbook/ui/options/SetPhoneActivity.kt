package com.seekerzhouk.accountbook.ui.options

import android.os.Bundle
import android.view.MenuItem
import androidx.navigation.findNavController
import com.seekerzhouk.accountbook.R

class SetPhoneActivity : OptionActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_set_phone)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                val fragment = getFragment(PhoneFragment::class.java)
                if (fragment != null) {
                    finish()
                }else{
                    findNavController(R.id.nav_host_fragment_phone).navigateUp()
                }
            }
        }
        return false
    }
}