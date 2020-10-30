package com.seekerzhouk.accountbook.ui.details

import android.os.Bundle
import android.view.*
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.seekerzhouk.accountbook.R
import com.seekerzhouk.accountbook.room.details.Record
import com.seekerzhouk.accountbook.ui.customize.AddDialog
import com.seekerzhouk.accountbook.utils.ConsumptionUtil
import com.seekerzhouk.accountbook.utils.SharedPreferencesUtil
import com.seekerzhouk.accountbook.viewmodel.DetailsViewModel
import kotlinx.android.synthetic.main.fragment_details.*

class DetailsFragment : Fragment(), LifecycleObserver {

    private lateinit var myAdapter: DetailsAdapter

    private val detailsViewModel: DetailsViewModel by viewModels()

    private var records: LiveData<List<Record>>? = null

    private var firstPosition: Int = 0
    private var secondPosition: Int = 0
    private var firstTag: String = ConsumptionUtil.ALL
    private var secondTag: String = ConsumptionUtil.ALL

    private var oldCount = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        //recyclerView
        recyclerview_details.layoutManager = LinearLayoutManager(context)
        myAdapter = DetailsAdapter()
        recyclerview_details.adapter = myAdapter
        // 上下文菜单
        registerForContextMenu(recyclerview_details)
//        setItemTouchHelper()

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

    override fun onCreateContextMenu(
        menu: ContextMenu,
        v: View,
        menuInfo: ContextMenu.ContextMenuInfo?
    ) {
        super.onCreateContextMenu(menu, v, menuInfo)
        activity?.menuInflater?.inflate(R.menu.recyclerview_menu, menu)
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.delete_item) {
            val recordToDelete = records?.value?.get(myAdapter.getPosition())
            if (recordToDelete != null) {
                detailsViewModel.deleteRecords(recordToDelete)
                Snackbar.make(
                    requireView(),
                    getString(R.string.deleted_a_record),
                    Snackbar.LENGTH_SHORT
                ).apply {
                    setAction(getString(R.string.revoke)) {
                        detailsViewModel.insertRecords(recordToDelete)
                    }
                }.show()
            }
        }
        return super.onContextItemSelected(item)
    }

    /**
     * 实现侧滑删除
     */
    private fun setItemTouchHelper() {
        ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.END) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val recordToDelete = records?.value?.get(viewHolder.adapterPosition)
                if (recordToDelete != null) {
                    detailsViewModel.deleteRecords(recordToDelete)
                    Snackbar.make(
                        requireView(),
                        getString(R.string.deleted_a_record),
                        Snackbar.LENGTH_SHORT
                    ).apply {
                        setAction(getString(R.string.revoke)) {
                            detailsViewModel.insertRecords(recordToDelete)
                        }
                    }.show()
                }
            }

        }).attachToRecyclerView(recyclerview_details)
    }

    /**
     * 设置 searchView
     */
    private fun setSearchView() {
        search_view.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                newText?.let {
                    val patten = newText.trim()
                    setSearchRecords(patten)
                }
                return true
            }
        })
        search_view.setOnClickListener {
            search_view.isIconified = false
        }
        search_view.setOnCloseListener {
            records?.removeObservers(this)
            false
        }
    }

    private fun setSearchRecords(patten: String) {
        records?.removeObservers(this)
        records = when (firstTag) {
            ConsumptionUtil.ALL -> detailsViewModel.findRecordWithPatten(patten)

            ConsumptionUtil.INCOME -> {
                if (secondTag == ConsumptionUtil.ALL) {
                    detailsViewModel.findIncomeRecordsWithPatten(patten)
                } else {
                    detailsViewModel.findIncomeRecordsBySelectedTypeWithPatten(secondTag, patten)
                }
            }

            ConsumptionUtil.EXPEND -> {
                if (secondTag == ConsumptionUtil.ALL) {
                    detailsViewModel.findExpendRecordsWithPatten(patten)
                } else {
                    detailsViewModel.findExpendRecordsBySelectedTypeWithPatten(secondTag, patten)
                }
            }

            else -> null
        }

        records?.observe(this, Observer {
            myAdapter.submitList(it)
        })
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
                if (firstPosition != position) {
                    // 手动选择spinner，第二个spinner的位置初始化为0
                    firstPosition = position
                    secondPosition = 0
                }
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
                // 调用两次，达到关闭searchView的目的
                repeat(2) {
                    search_view.isIconified = true
                }
                secondTag = secondTypeList[position]
                secondPosition = position
                saveSpinnerPosition()
                setSpinnerRecords()
            }
        }
    }

    private fun setSpinnerRecords() {
        records?.removeObservers(this)
        records = when (firstTag) {
            ConsumptionUtil.ALL -> detailsViewModel.loadAllRecords()

            ConsumptionUtil.INCOME -> {
                if (secondTag == ConsumptionUtil.ALL) {
                    detailsViewModel.findIncomeRecords()
                } else {
                    detailsViewModel.findIncomeRecordsBySelectedType(secondTag)
                }
            }

            ConsumptionUtil.EXPEND -> {
                if (secondTag == ConsumptionUtil.ALL) {
                    detailsViewModel.findExpendRecords()
                } else {
                    detailsViewModel.findExpendRecordsBySelectedType(secondTag)
                }
            }

            else -> null
        }

        records?.observe(this, Observer {
            myAdapter.submitList(it)
            scrollRecyclerView()
        })
    }

    override fun onPause() {
        super.onPause()
        saveSpinnerPosition()
        records?.removeObservers(this)
    }

    private fun saveSpinnerPosition() {
        SharedPreferencesUtil.saveFirstPosition(requireActivity(), firstPosition)
        SharedPreferencesUtil.saveSecondPosition(requireActivity(), secondPosition)
    }

    private fun scrollRecyclerView() {
        if (myAdapter.itemCount - oldCount == 1) {
            recyclerview_details.getChildAt(0)?.height?.let {
                recyclerview_details.smoothScrollBy(0, -it)
            }
        }
        oldCount = myAdapter.itemCount
    }
}