package com.seekerzhouk.accountbook.ui.details

import android.os.Bundle
import android.view.*
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.SearchView.OnQueryTextListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.seekerzhouk.accountbook.R
import com.seekerzhouk.accountbook.room.details.Record
import com.seekerzhouk.accountbook.ui.customize.AddDialog
import com.seekerzhouk.accountbook.utils.ConsumptionUtil
import com.seekerzhouk.accountbook.utils.SharedPreferencesUtil
import com.seekerzhouk.accountbook.viewmodel.DetailsViewModel
import kotlinx.android.synthetic.main.fragment_details.*

class DetailsFragment : Fragment() {

    private val LOG_TAG = "DetailsFragment"

    private lateinit var myAdapter: DetailsAdapter

    private lateinit var detailsViewModel: DetailsViewModel

    private lateinit var allRecords: LiveData<List<Record>>
    private lateinit var pattenRecords: LiveData<List<Record>>
    private lateinit var incomeRecords: LiveData<List<Record>>
    private lateinit var expendRecords: LiveData<List<Record>>

    private var firstPosition: Int = 0
    private var secondPosition: Int = 0
    private var firstTag: String = ConsumptionUtil.ALL
    private var secondTag: String = ConsumptionUtil.ALL

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        //recyclerView
        val layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(context)
        recyclerview_details.layoutManager = layoutManager
        val records: ArrayList<Record> = ArrayList()
        myAdapter = DetailsAdapter(records)
        recyclerview_details.adapter = myAdapter

        //viewModel
        detailsViewModel = ViewModelProvider(this).get(DetailsViewModel::class.java)

        //获取上一次选定的类型
        firstPosition = SharedPreferencesUtil.getFirstPosition(requireActivity())
        secondPosition = SharedPreferencesUtil.getSecondPosition(requireActivity())

        //悬浮按钮
        floatingActionButton.setOnClickListener {
            AddDialog.show(requireActivity())
        }

        setFirstTypeSpinner()

        setSearchView()
    }

    /**
     * 设置 searchView
     */
    private fun setSearchView() {
        search_view.setOnQueryTextListener(object : OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                newText?.let {
                    val patten = newText.trim()
                    if (firstTag == ConsumptionUtil.ALL) {
                        pattenRecords = detailsViewModel.findRecordWithPatten(patten)
                    }
                    if (firstTag == ConsumptionUtil.INCOME) {
                        pattenRecords = when (secondTag) {
                            ConsumptionUtil.ALL -> detailsViewModel.findIncomeRecordsWithPatten(
                                patten
                            )
                            else -> detailsViewModel.findIncomeRecordsBySelectedTypeWithPatten(
                                secondTag,
                                patten
                            )
                        }
                    }
                    if (firstTag == ConsumptionUtil.EXPEND) {
                        pattenRecords = when (secondTag) {
                            ConsumptionUtil.ALL -> detailsViewModel.findExpendRecordsWithPatten(
                                patten
                            )
                            else -> detailsViewModel.findExpendRecordsBySelectedTypeWithPatten(
                                secondTag,
                                patten
                            )
                        }
                    }
                    pattenRecords.observe(requireActivity(), Observer {
                        myAdapter.recordList = it
                        myAdapter.notifyDataSetChanged()
                    })
                }
                return true
            }
        })
        search_view.setOnClickListener {
            search_view.isIconified = false
        }
    }

    //设置first_type_spinner
    private fun setFirstTypeSpinner() {
        val recordTypeAdapter =
            ArrayAdapter(
                requireActivity(),
                R.layout.my_spinner_item,
                ConsumptionUtil.fistTypeList
            )
        recordTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        first_type_spinner.adapter = recordTypeAdapter
        first_type_spinner.setSelection(firstPosition)
        first_type_spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                firstTag = ConsumptionUtil.fistTypeList[position]
                // 首次初始化（fragment被创建）的时候，第二个spinner的position就是上次SharedPreferences保存的位置
                if (firstPosition == position) {
                    setSecondTypeSpinner()
                    return
                }
                // 手动选择spinner，第二个spinner的位置初始化为0
                firstPosition = position
                secondPosition = 0
                secondTag = ConsumptionUtil.ALL
                setSecondTypeSpinner()
            }
        }
    }

    //设置second_type_spinner
    private fun setSecondTypeSpinner() {
        val secondTypeList: ArrayList<String> =
            when (firstTag) {
                ConsumptionUtil.INCOME -> ConsumptionUtil.incomeTypeList
                ConsumptionUtil.EXPEND -> ConsumptionUtil.expendTypeList
                else -> ConsumptionUtil.tagAllList
            }

        val secondTypeAdapter =
            ArrayAdapter(requireActivity(), R.layout.my_spinner_item, secondTypeList)
        secondTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        second_type_spinner.adapter = secondTypeAdapter
        second_type_spinner.setSelection(secondPosition)
        second_type_spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                secondPosition = position
                if (firstTag == ConsumptionUtil.ALL) {
                    secondTag = ConsumptionUtil.ALL
                    showAllRecords()
                }
                if (firstTag == ConsumptionUtil.INCOME) {
                    secondTag = ConsumptionUtil.incomeTypeList[position]
                    if (secondTag == ConsumptionUtil.ALL) {
                        showAllIncomeRecords()
                    } else {
                        showIncomeRecordsBySecondType(secondTag)
                    }
                }
                if (firstTag == ConsumptionUtil.EXPEND) {
                    secondTag = ConsumptionUtil.expendTypeList[position]
                    if (secondTag == ConsumptionUtil.ALL) {
                        showAllExpendRecords()
                    } else {
                        showExpendRecordsBySecondType(secondTag)
                    }
                }
            }
        }
    }

    private fun showAllRecords() {
        allRecords = detailsViewModel.loadAllRecords()
        allRecords.observe(requireActivity(), Observer {
            myAdapter.recordList = it
            myAdapter.notifyDataSetChanged()
        })
    }

    private fun showAllExpendRecords() {
        expendRecords = detailsViewModel.findExpendRecords()
        expendRecords.observe(requireActivity(), Observer {
            myAdapter.recordList = it
            myAdapter.notifyDataSetChanged()
        })
    }

    private fun showAllIncomeRecords() {
        incomeRecords = detailsViewModel.findIncomeRecords()
        incomeRecords.observe(requireActivity(), Observer {
            myAdapter.recordList = it
            myAdapter.notifyDataSetChanged()
        })
    }

    private fun showIncomeRecordsBySecondType(secondTag: String) {
        val selectedIncomeRecords = detailsViewModel.findIncomeRecordsBySelectedType(secondTag)
        selectedIncomeRecords.observe(requireActivity(), Observer {
            myAdapter.recordList = it
            myAdapter.notifyDataSetChanged()
        })
    }

    private fun showExpendRecordsBySecondType(secondTag: String) {
        val selectedExpendRecords = detailsViewModel.findExpendRecordsBySelectedType(secondTag)
        selectedExpendRecords.observe(requireActivity(), Observer {
            myAdapter.recordList = it
            myAdapter.notifyDataSetChanged()
        })
    }

    override fun onPause() {
        super.onPause()
        SharedPreferencesUtil.saveFirstPosition(requireActivity(), firstPosition)
        SharedPreferencesUtil.saveSecondPosition(requireActivity(), secondPosition)
    }
}