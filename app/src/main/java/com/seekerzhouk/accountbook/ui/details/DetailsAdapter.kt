package com.seekerzhouk.accountbook.ui.details

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.seekerzhouk.accountbook.databinding.DetailItemViewBinding
import com.seekerzhouk.accountbook.room.details.Record
import com.seekerzhouk.accountbook.utils.ConsumptionUtil

class DetailsAdapter() :
    PagedListAdapter<Record, DetailsAdapter.MyViewHolder>(object : DiffUtil.ItemCallback<Record>() {
        override fun areItemsTheSame(oldItem: Record, newItem: Record): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Record, newItem: Record): Boolean {
            return oldItem == newItem
        }

    }) {

    private var mPosition = -1
    fun getPosition(): Int {
        return mPosition
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding =
            DetailItemViewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val record: Record? = getItem(position)
        record?.let {
            holder.imageConsumptionType.setImageResource(ConsumptionUtil.getIconType(it))
            holder.textConsumptionType.text = it.consumptionType
            holder.textDescription.text = it.description
            holder.textDateAndTime.text = it.dateTime
            var money = String.format("%.2f", it.money)
            if (it.incomeOrExpend == ConsumptionUtil.EXPEND && it.money > 0) {
                money = "-$money"
            }
            holder.textMoney.text = money
        }

        holder.itemView.isLongClickable = true
        holder.itemView.setOnLongClickListener {
            mPosition = holder.adapterPosition
            false
        }
    }

    inner class MyViewHolder(binding: DetailItemViewBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val imageConsumptionType: ImageView = binding.imageConsumptionType
        val textConsumptionType: TextView = binding.textConsumptionType
        val textDescription: TextView = binding.textDescription
        val textDateAndTime: TextView = binding.textDateTime
        val textMoney: TextView = binding.textMoney
    }
}