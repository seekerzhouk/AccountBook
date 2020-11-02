package com.seekerzhouk.accountbook.ui.options

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.seekerzhouk.accountbook.R
import com.seekerzhouk.accountbook.databinding.FragmentBindPhoneBinding

class BindPhoneFragment : Fragment() {
    private lateinit var binding: FragmentBindPhoneBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentBindPhoneBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity?.title = getString(R.string.bind_phone_number)
    }
}