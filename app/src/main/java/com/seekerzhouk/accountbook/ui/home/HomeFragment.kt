package com.seekerzhouk.accountbook.ui.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.HorizontalScrollView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.seekerzhouk.accountbook.R
import com.seekerzhouk.accountbook.database.home.Pillar
import com.seekerzhouk.accountbook.database.home.Sector

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

        val histogramView: HistogramView = root.findViewById(R.id.histogram_view_expend_monthly)
        homeViewModel.getPillars().observe(requireActivity(), Observer {
            histogramView.pillarList = it
        })

        scrollView = root.findViewById(R.id.horizontalScrollView_expend_monthly)

        return root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        scrollView.post {
            scrollView.fullScroll(View.FOCUS_RIGHT)
        }
    }
}