package com.seekerzhouk.accountbook.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.LiveData
import com.seekerzhouk.accountbook.R
import com.seekerzhouk.accountbook.databinding.FragmentHomeBinding
import com.seekerzhouk.accountbook.room.home.*
import com.seekerzhouk.accountbook.viewmodel.HomeViewModel
import kotlinx.android.synthetic.main.fragment_home.*
import java.util.*

class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private val homeViewModel: HomeViewModel by viewModels()

    private var incomeSectors: LiveData<List<Sector>>? = null
    private var expendSectors: LiveData<List<Sector>>? = null
    private var incomePillars: LiveData<List<Pillar>>? = null
    private var expendPillars: LiveData<List<Pillar>>? = null
    private var incomePoints: LiveData<List<Point>>? = null
    private var expendPoints: LiveData<List<Point>>? = null

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
        setYearSpinner()
    }

    private fun setYearSpinner() {
        binding.yearSpinner.adapter = ArrayAdapter(
            requireContext(),
            R.layout.my_spinner_item,
            List(5) {
                "${Calendar.getInstance().get(Calendar.YEAR) - it}年"
            }
        ).apply {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }
        binding.yearSpinner.setSelection(0)
        binding.yearSpinner.scrollBarSize = 5
        binding.yearSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                setMonthSpinner()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

        }
    }

    private fun setMonthSpinner() {
        val list = MutableList(12) {
            "${it + 1}月"
        }.apply {
            add(0, "全年")
        }
        var position = 0
        for (i in list.indices) {
            if ((Calendar.getInstance().get(Calendar.MONTH) + 1).toString().plus("月") == list[i]) {
                position = i
            }
        }
        binding.monthSpinner.adapter = ArrayAdapter(
            requireContext(),
            R.layout.my_spinner_item,
            list
        ).apply {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }

        binding.monthSpinner.setSelection(position)
        binding.monthSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                chooseDataToShow(
                    yearSpinner.selectedItem as String,
                    monthSpinner.selectedItem as String
                )
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

        }
    }

    private fun chooseDataToShow(year: String, month: String) {
        removeObservers()

        if (month == "全年") {
            binding.incomeChartCard.visibility = View.GONE
            binding.expendChartCard.visibility = View.GONE
            binding.expendHistogramCard.visibility = View.VISIBLE
            binding.incomeHistogramCard.visibility = View.VISIBLE
            binding.incomePieDate.text = year
            binding.expendPieDate.text = year
            binding.incomeHistogramDate.text = year
            binding.expendHistogramDate.text = year
            setHistogramViews(year, month)
            setPieViews(year)
        } else{
            binding.incomeChartCard.visibility = View.VISIBLE
            binding.expendChartCard.visibility = View.VISIBLE
            binding.expendHistogramCard.visibility = View.GONE
            binding.incomeHistogramCard.visibility = View.GONE
            binding.incomePieDate.text = year.plus(month)
            binding.expendPieDate.text = year.plus(month)
            binding.incomeChartDate.text = year.plus(month)
            binding.expendChartDate.text = year.plus(month)
            setPieViews(year, month)
            setLineChartViews(year, month)
        }
    }

    private fun setPieViews(year: String, month: String) {
        incomeSectors = homeViewModel.getIncomeSectorsByMonth(year + month)
        incomeSectors?.observe(this, {
            binding.pieViewIncome.sectorList = it
        })
        expendSectors = homeViewModel.getExpendSectorsByMonth(year + month)
        expendSectors?.observe(this, {
            binding.pieViewExpend.sectorList = it
        })
    }

    private fun setPieViews(year: String) {
        incomeSectors = homeViewModel.getIncomeSectorsByYear(year)
        incomeSectors?.observe(this, {
            binding.pieViewIncome.sectorList = it
        })
        expendSectors = homeViewModel.getExpendSectorsByYear(year)
        expendSectors?.observe(this, {
            binding.pieViewExpend.sectorList = it
        })
    }

    private fun setHistogramViews(year: String, month: String) {
        incomePillars = homeViewModel.getIncomePillarsByYear(year)
        incomePillars?.observe(this, {
            binding.incomeHistogram.pillarList = it
        })
        expendPillars = homeViewModel.getExpendPillarsByYear(year)
        expendPillars?.observe(this, {
            binding.expendHistogram.pillarList = it
        })

        if (month[0].toInt() > 6 || month.length == 3) {
            binding.expendHistogramScrollView.post {
                binding.expendHistogramScrollView.fullScroll(View.FOCUS_RIGHT)
            }
            binding.incomeHistogramScrollView.post {
                binding.incomeHistogramScrollView.fullScroll(View.FOCUS_RIGHT)
            }
        }
    }

    private fun setLineChartViews(year: String, month: String) {
        incomePoints = homeViewModel.getIncomePointsByMonth(year, month)
        incomePoints?.observe(this, {
            binding.incomeLineChart.daysList = it
        })
        expendPoints = homeViewModel.getExpendPointsByMonth(year, month)
        expendPoints?.observe(this, {
            binding.expendLineChart.daysList = it
        })
    }

    private fun removeObservers() {
        incomeSectors?.removeObservers(this)
        expendSectors?.removeObservers(this)
        incomePillars?.removeObservers(this)
        expendPillars?.removeObservers(this)
        incomePoints?.removeObservers(this)
        expendPoints?.removeObservers(this)
    }
}