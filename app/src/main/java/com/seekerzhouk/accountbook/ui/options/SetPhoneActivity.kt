package com.seekerzhouk.accountbook.ui.options

import android.os.Bundle
import android.view.MenuItem
import androidx.navigation.findNavController
import com.seekerzhouk.accountbook.R
import com.seekerzhouk.accountbook.databinding.ActivitySetPhoneBinding

class SetPhoneActivity : OptionActivity() {
    private lateinit var binding: ActivitySetPhoneBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySetPhoneBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                val fragment = getFragment(PhoneFragment::class.java)
                if (fragment != null) {
                    finish()
                } else {
                    findNavController(R.id.nav_host_fragment_phone).navigateUp()
                }
            }
        }
        return false
    }
}