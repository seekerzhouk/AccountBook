package com.seekerzhouk.accountbook.ui.details

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.seekerzhouk.accountbook.R
import com.seekerzhouk.accountbook.room.details.Record
import com.seekerzhouk.accountbook.utils.ConsumptionUtil

class DetailsAdapter(var recordList: List<Record>) :
    RecyclerView.Adapter<DetailsAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.detail_item_view, parent, false)
        return MyViewHolder(view)
    }

    override fun getItemCount(): Int {
        return recordList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val record: Record = recordList[position]
        holder.imageConsumptionType.setImageResource(ConsumptionUtil.getIconType(record))
        holder.textConsumptionType.text = record.secondType
        holder.textDescription.text = record.description
        holder.textDateAndTime.text = record.date.plus(" ").plus(record.time)
        var money = String.format("%.2f", record.money)
        if (record.incomeOrExpend == ConsumptionUtil.EXPEND && record.money > 0){
            money = "-$money"
        } 
        holder.textMoney.text = money

    }

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageConsumptionType: ImageView = itemView.findViewById(R.id.image_consumption_type)
        val textConsumptionType: TextView = itemView.findViewById(R.id.text_consumption_type)
        val textDescription: TextView = itemView.findViewById(R.id.text_consumption_description)
        val textDateAndTime: TextView = itemView.findViewById(R.id.text_date_time)
        val textMoney: TextView = itemView.findViewById(R.id.text_money)
    }
}