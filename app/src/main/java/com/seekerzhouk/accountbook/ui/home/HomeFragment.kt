package com.seekerzhouk.accountbook.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.HorizontalScrollView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.seekerzhouk.accountbook.R
import kotlinx.android.synthetic.main.fragment_home.*
import java.util.*

class HomeFragment : Fragment() {

    private lateinit var homeViewModel: HomeViewModel
    private lateinit var scrollView: HorizontalScrollView
    private lateinit var pieViewIncome: PieView
    private lateinit var pieViewExpend: PieView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        homeViewModel = ViewModelProvider(this).get(HomeViewModel::class.java)

        val root = inflater.inflate(R.layout.fragment_home, container, false)

        pieViewIncome = root.findViewById(R.id.pie_view_income)
        homeViewModel.getIncomeSectors().observe(requireActivity(), Observer {
            pieViewIncome.sectorList = it
        })

        pieViewExpend = root.findViewById(R.id.pie_view_expend)
        homeViewModel.getExpendSectors().observe(requireActivity(), Observer {
            pieViewExpend.sectorList = it
        })

        val expendHistogram: HistogramView = root.findViewById(R.id.histogram_view_expend_monthly)
        homeViewModel.getExpendPillars().observe(requireActivity(), Observer {
            expendHistogram.pillarList = it
        })

        val incomeHistogram: HistogramView = root.findViewById(R.id.histogram_view_income_monthly)
        homeViewModel.getIncomePillars().observe(requireActivity(), Observer {
            incomeHistogram.pillarList = it
        })

        scrollView = root.findViewById(R.id.horizontalScrollView_expend_monthly)

        return root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val month = Calendar.getInstance().get(Calendar.MONTH) + 1
        if (month > 6) {
            scrollView.post {
                scrollView.fullScroll(View.FOCUS_RIGHT)
            }
            horizontalScrollView_income_monthly.post {
                horizontalScrollView_income_monthly.fullScroll(View.FOCUS_RIGHT)
            }
        }
    }
}