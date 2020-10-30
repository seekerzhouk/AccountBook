package com.seekerzhouk.accountbook.ui.details

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.seekerzhouk.accountbook.R
import com.seekerzhouk.accountbook.room.details.Record
import com.seekerzhouk.accountbook.utils.ConsumptionUtil

class DetailsAdapter() :
    ListAdapter<Record, DetailsAdapter.MyViewHolder>(object : DiffUtil.ItemCallback<Record>() {
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
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.detail_item_view, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val record: Record = getItem(position)
        holder.imageConsumptionType.setImageResource(ConsumptionUtil.getIconType(record))
        holder.textConsumptionType.text = record.consumptionType
        holder.textDescription.text = record.description
        holder.textDateAndTime.text = record.dateTime
        var money = String.format("%.2f", record.money)
        if (record.incomeOrExpend == ConsumptionUtil.EXPEND && record.money > 0) {
            money = "-$money"
        }
        holder.textMoney.text = money

        holder.itemView.isLongClickable = true
        holder.itemView.setOnLongClickListener {
            mPosition = holder.adapterPosition
            false
        }
    }

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageConsumptionType: ImageView = itemView.findViewById(R.id.image_consumption_type)
        val textConsumptionType: TextView = itemView.findViewById(R.id.text_consumption_type)
        val textDescription: TextView = itemView.findViewById(R.id.text_consumption_description)
        val textDateAndTime: TextView = itemView.findViewById(R.id.text_date_time)
        val textMoney: TextView = itemView.findViewById(R.id.text_money)
    }
}