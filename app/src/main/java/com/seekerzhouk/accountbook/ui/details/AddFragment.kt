package com.seekerzhouk.accountbook.ui.details

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.seekerzhouk.accountbook.R
import com.seekerzhouk.accountbook.databinding.AddFragmentBinding
import com.seekerzhouk.accountbook.room.details.Record
import com.seekerzhouk.accountbook.utils.ConsumptionUtil
import com.seekerzhouk.accountbook.utils.DateTimeUtil
import com.seekerzhouk.accountbook.utils.SharedPreferencesUtil
import com.seekerzhouk.accountbook.viewmodel.DetailsViewModel

class AddFragment : Fragment() {

    private lateinit var binding: AddFragmentBinding

    private val detailsViewModel: DetailsViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = AddFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setBackImageFun()
        setTextViews()
        setSpinners()
        setEditText()
        setButtons()
    }

    private fun setButtons() {
        binding.buttonCancel.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.buttonSubmit.setOnClickListener {
            val record = Record(
                SharedPreferencesUtil.getUserName(requireContext()),
                binding.firstSpinner.selectedItem.toString(),
                binding.secondSpinner.selectedItem.toString(),
                binding.editTextDescription.text.toString(),
                binding.chosenDate.text.toString().plus(" ${binding.chosenTime.text}"),
                binding.editTextSum.text.toString().toDouble()
            )
            detailsViewModel.insertRecords(record)
            findNavController().navigateUp()
        }
    }

    private fun setEditText() {
        //editText_sum 设置输入监听
        binding.editTextSum.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.buttonSubmit.isEnabled = binding.editTextSum.text.trim().isNotEmpty()
            }
        })
    }

    private fun setSpinners() {
        // 设置两个spinner
        val firstTypeList = ConsumptionUtil.fistTypeList.filterIndexed { index, _ -> index > 0 }
        binding.firstSpinner.adapter = getFistAdapter(firstTypeList)
        binding.firstSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val firstType = firstTypeList[position]
                binding.secondSpinner.adapter = getSecondAdapter(firstType)
            }
        }
    }

    private fun setTextViews() {
        binding.chosenDate.text = DateTimeUtil.getCurrentSpecificDate()
        binding.chosenDate.setOnClickListener {
            DatePickerDialog(
                requireContext(),
                { _, year, month, dayOfMonth ->
                    binding.chosenDate.text = DateTimeUtil.getPickerDate(year, month, dayOfMonth)
                }, 2020, 11, 0
            ).show()
        }
        binding.chosenTime.text = DateTimeUtil.getCurrentTime()
        binding.chosenTime.setOnClickListener {
            TimePickerDialog(
                requireContext(),
                { _, hourOfDay, minute ->
                    binding.chosenTime.text = DateTimeUtil.getPickerTime(hourOfDay, minute)
                }, 0, 0, true
            ).show()
        }
    }

    private fun setBackImageFun() {
        binding.back.setOnClickListener {
            findNavController().navigateUp()
        }
    }


    private fun getSecondAdapter(
        firstType: String
    ): ArrayAdapter<String> {
        val secondTypeList = if (firstType == ConsumptionUtil.INCOME) {
            ConsumptionUtil.incomeTypeList
        } else {
            ConsumptionUtil.expendTypeList
        }.filterIndexed { index, _ -> index > 0 }
        val secondAdapter =
            ArrayAdapter(requireContext(), R.layout.my_spinner_item, secondTypeList)
        secondAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        return secondAdapter
    }

    private fun getFistAdapter(
        firstTypeList: List<String>
    ): ArrayAdapter<String> {
        val fistAdapter =
            ArrayAdapter(
                requireContext(),
                R.layout.my_spinner_item,
                firstTypeList
            )
        fistAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        return fistAdapter
    }
}