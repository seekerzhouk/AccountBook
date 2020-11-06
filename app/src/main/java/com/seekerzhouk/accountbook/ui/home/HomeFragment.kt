package com.seekerzhouk.accountbook.ui.home

import android.app.AlertDialog
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.LiveData
import com.seekerzhouk.accountbook.R
import com.seekerzhouk.accountbook.databinding.FragmentHomeBinding
import com.seekerzhouk.accountbook.room.home.Pillar
import com.seekerzhouk.accountbook.room.home.Point
import com.seekerzhouk.accountbook.room.home.Sector
import com.seekerzhouk.accountbook.ui.customize.MonthPickerDialog
import com.seekerzhouk.accountbook.ui.customize.YearPickerDialog
import com.seekerzhouk.accountbook.utils.DateTimeUtil
import com.seekerzhouk.accountbook.utils.MyLog
import com.seekerzhouk.accountbook.viewmodel.HomeViewModel
import com.seekerzhouk.accountbook.viewmodel.Switch

class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private val homeViewModel: HomeViewModel by viewModels()

    private var incomeSectors: LiveData<List<Sector>>? = null
    private var expendSectors: LiveData<List<Sector>>? = null
    private var incomePillars: LiveData<List<Pillar>>? = null
    private var expendPillars: LiveData<List<Pillar>>? = null
    private var incomePoints: LiveData<List<Point>>? = null
    private var expendPoints: LiveData<List<Point>>? = null

    private lateinit var year: String
    private lateinit var specificMonth: String

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        year = DateTimeUtil.getCurrentYear()
        specificMonth = DateTimeUtil.getSpecificMonth()
        binding.homeTitleBar.switchYearOrMonth.setOnClickListener {

            year = DateTimeUtil.getCurrentYear()
            specificMonth = DateTimeUtil.getSpecificMonth()
            homeViewModel.turnTitleSwitch()
        }
        homeViewModel.titleSwitch.observe(viewLifecycleOwner, {
            when (it) {
                Switch.ByYear -> {
                    setDataByYear()
                }
                Switch.ByMonth -> {
                    setDataByMonth()
                }
                else -> {

                }
            }
        })
        binding.homeTitleBar.setSpecificMonth.setOnClickListener {
            when (homeViewModel.titleSwitch.value) {
                Switch.ByYear -> {
                    YearPickerDialog(requireContext(), AlertDialog.THEME_HOLO_LIGHT).apply {
                        setOnDateSetListener { _, year, _, _ ->
                            this@HomeFragment.year = year.toString().plus("年")
                            setDataByYear()
                        }
                    }.show()
                }
                Switch.ByMonth -> {
                    MonthPickerDialog(requireContext(), AlertDialog.THEME_HOLO_LIGHT).apply {
                        setOnDateSetListener { _, year, month, _ ->
                            specificMonth = if (month + 1 < 10) {
                                year.toString().plus("年").plus(0).plus(month + 1).plus("月")
                            } else {
                                year.toString().plus("年").plus(month + 1).plus("月")
                            }
                            setDataByMonth()
                        }
                    }.show()
                }

            }

        }
    }

    private fun setDataByYear() {
        binding.incomeChartCard.visibility = View.GONE
        binding.expendChartCard.visibility = View.GONE
        binding.expendHistogramCard.visibility = View.VISIBLE
        binding.incomeHistogramCard.visibility = View.VISIBLE
        binding.incomePieDate.text = year
        binding.expendPieDate.text = year
        binding.incomeHistogramDate.text = year
        binding.expendHistogramDate.text = year
        binding.homeTitleBar.titleDate.text = year
        binding.homeTitleBar.titleSwitchText.text = getString(R.string.count_by_year)
        setHistogramViews()
        setPieViewsByYear()
    }

    private fun setDataByMonth() {
        binding.incomeChartCard.visibility = View.VISIBLE
        binding.expendChartCard.visibility = View.VISIBLE
        binding.expendHistogramCard.visibility = View.GONE
        binding.incomeHistogramCard.visibility = View.GONE
        binding.incomePieDate.text = specificMonth
        binding.expendPieDate.text = specificMonth
        binding.incomeChartDate.text = specificMonth
        binding.expendChartDate.text = specificMonth
        binding.homeTitleBar.titleDate.text = specificMonth
        binding.homeTitleBar.titleSwitchText.text = getString(R.string.count_by_month)
        setPieViewsByMonth()
        setLineChartViews()
    }

    private fun setPieViewsByMonth() {
        incomeSectors = homeViewModel.getIncomeSectorsByMonth(specificMonth)
        incomeSectors?.observe(viewLifecycleOwner, {
            MyLog.i("HomeFragment", "setPieViewsByMonth()")
            binding.pieViewIncome.sectorList = it
        })
        expendSectors = homeViewModel.getExpendSectorsByMonth(specificMonth)
        expendSectors?.observe(viewLifecycleOwner, {
            binding.pieViewExpend.sectorList = it
        })
    }

    private fun setPieViewsByYear() {
        incomeSectors = homeViewModel.getIncomeSectorsByYear(year)
        incomeSectors?.observe(viewLifecycleOwner, {
            MyLog.i("HomeFragment", "setPieViewsByYear()")
            binding.pieViewIncome.sectorList = it
        })
        expendSectors = homeViewModel.getExpendSectorsByYear(year)
        expendSectors?.observe(viewLifecycleOwner, {
            binding.pieViewExpend.sectorList = it
        })
    }

    private fun setHistogramViews() {
        incomePillars = homeViewModel.getIncomePillarsByYear(year)
        incomePillars?.observe(viewLifecycleOwner, {
            binding.incomeHistogram.pillarList = it
        })
        expendPillars = homeViewModel.getExpendPillarsByYear(year)
        expendPillars?.observe(viewLifecycleOwner, {
            binding.expendHistogram.pillarList = it
        })

        val currentMonth = DateTimeUtil.getCurrentMonth()
        binding.expendHistogramScrollView.apply {
            post {
                smoothScrollTo(width * currentMonth / 12, 0)
            }
        }
        binding.incomeHistogramScrollView.apply {
            post {
                smoothScrollTo(width * currentMonth / 12, 0)
            }
        }
    }

    private fun setLineChartViews() {
        incomePoints = homeViewModel.getIncomePointsByMonth(specificMonth)
        incomePoints?.observe(viewLifecycleOwner, {
            binding.incomeLineChart.daysList = it
        })
        expendPoints = homeViewModel.getExpendPointsByMonth(specificMonth)
        expendPoints?.observe(viewLifecycleOwner, {
            binding.expendLineChart.daysList = it
        })
        val dayOfMonth = DateTimeUtil.getCurrentDayOfMonth()
        binding.expendChartScroll.apply {
            post {
                if (dayOfMonth > 25) {
                    fullScroll(View.FOCUS_RIGHT)
                } else {
                    smoothScrollTo(width * dayOfMonth / 30, 0)
                }

            }
        }
        binding.incomeChartScroll.apply {
            post {
                if (dayOfMonth > 25) {
                    fullScroll(View.FOCUS_RIGHT)
                } else {
                    smoothScrollTo(width * dayOfMonth / 30, 0)
                }
            }
        }
    }
}