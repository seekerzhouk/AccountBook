package com.seekerzhouk.accountbook.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.seekerzhouk.accountbook.databinding.FragmentHomeBinding
import com.seekerzhouk.accountbook.viewmodel.HomeViewModel
import java.util.*

class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private val homeViewModel: HomeViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
       binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        homeViewModel.getIncomeSectors().observe(requireActivity(), {
            binding.pieViewIncome.sectorList = it
        })
        homeViewModel.getExpendSectors().observe(requireActivity(), {
            binding.pieViewExpend.sectorList = it
        })
        homeViewModel.getExpendPillars().observe(requireActivity(), {
            binding.expendHistogram.pillarList = it
        })
        homeViewModel.getIncomePillars().observe(requireActivity(), {
            binding.incomeHistogram.pillarList = it
        })

        val month = Calendar.getInstance().get(Calendar.MONTH) + 1
        if (month > 6) {
            binding.expendHistogramScrollView.post {
                binding.expendHistogramScrollView.fullScroll(View.FOCUS_RIGHT)
            }
            binding.incomeHistogramScrollView.post {
                binding.incomeHistogramScrollView.fullScroll(View.FOCUS_RIGHT)
            }
        }
    }
}