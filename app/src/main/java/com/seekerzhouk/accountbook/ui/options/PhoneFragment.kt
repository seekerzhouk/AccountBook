package com.seekerzhouk.accountbook.ui.options

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import cn.leancloud.AVUser
import com.seekerzhouk.accountbook.R
import com.seekerzhouk.accountbook.databinding.FragmentPhoneBinding

class PhoneFragment : Fragment() {
    private lateinit var binding: FragmentPhoneBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPhoneBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.currentNumber.text =
            AVUser.getCurrentUser().mobilePhoneNumber ?: getString(R.string.not_bind)
        binding.bindPhoneButton.setOnClickListener {
            Toast.makeText(
                requireContext(),
                getString(R.string.not_yet_implement),
                Toast.LENGTH_SHORT
            ).show()
//            findNavController().navigate(R.id.action_phoneFragment_to_bindPhoneFragment)
        }
        binding.changePhoneButton.setOnClickListener {
            Toast.makeText(
                requireContext(),
                getString(R.string.not_yet_implement),
                Toast.LENGTH_SHORT
            ).show()
//            findNavController().navigate(R.id.action_phoneFragment_to_changePhoneFragment)
        }
    }

    override fun onResume() {
        super.onResume()
        activity?.title = getString(R.string.phone_number)
    }

}