package com.abhishek.weighttrack.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.abhishek.weighttrack.R
import com.abhishek.weighttrack.listeners.ItemSelectionListener
import com.abhishek.weighttrack.model.WeightEntry
import com.abhishek.weighttrack.utility.ApplicationUtils
import com.google.android.material.textview.MaterialTextView
import java.util.Date

class WeightHistoryAdapter(val weightEntries: List<WeightEntry>, val itemSelectionListener: ItemSelectionListener): RecyclerView.Adapter<WeightHistoryAdapter.WeightHistoryHolder>() {
    private var applicationUtils: ApplicationUtils = ApplicationUtils()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): WeightHistoryHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.weight_list_item, parent, false)
        return WeightHistoryHolder(itemView)
    }

    class WeightHistoryHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val lblWeightEntryDate: MaterialTextView = itemView.findViewById(R.id.lblWeightEntryDate)
        val lblWeightEntry: MaterialTextView = itemView.findViewById(R.id.lblWeightEntry)
        val lblWeightDetails: MaterialTextView = itemView.findViewById(R.id.lblWeightDetails)
    }

    override fun onBindViewHolder(holder: WeightHistoryHolder, position: Int) {
        val weightEntry = weightEntries[position]
        holder.lblWeightEntryDate.text = applicationUtils.getStringFromDate(Date(weightEntry.date),"MM/dd/yyyy")
        holder.lblWeightEntry.text = weightEntry.weight.toString()
        holder.lblWeightDetails.setOnClickListener {
            itemSelectionListener.onItemSelect(weightEntry.id)
        }
    }

    override fun getItemCount(): Int {
        return weightEntries.size
    }
}