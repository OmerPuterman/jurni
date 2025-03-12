package com.example.jurni.ui.trips

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.jurni.R

class AttractionsAdapter(private val attractions: List<Attraction>) :
    RecyclerView.Adapter<AttractionsAdapter.AttractionViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AttractionViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_attraction_detail, parent, false)
        return AttractionViewHolder(view)
    }

    override fun onBindViewHolder(holder: AttractionViewHolder, position: Int) {
        val attraction = attractions[position]
        holder.tvAttractionName.text = attraction.name
        holder.tvAttractionCost.text = "Cost: $${attraction.cost}"
    }

    override fun getItemCount(): Int = attractions.size

    class AttractionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvAttractionName: TextView = itemView.findViewById(R.id.tv_attraction_name)
        val tvAttractionCost: TextView = itemView.findViewById(R.id.tv_attraction_cost)
    }
}
