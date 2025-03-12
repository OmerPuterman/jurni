package com.example.jurni.ui.trips

import android.content.Intent
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.jurni.R
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.auth.FirebaseAuth



class TripAdapter(private var tripList: List<Trip>, private val onDelete: (String) -> Unit) : RecyclerView.Adapter<TripAdapter.TripViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TripViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_trip, parent, false)
        return TripViewHolder(view)
    }

    override fun onBindViewHolder(holder: TripViewHolder, position: Int) {
        val trip = tripList[position]
        holder.tvDestination.text = trip.destination
        holder.tvDates.text = "${trip.startDate} - ${trip.endDate}"

        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val tripRef = FirebaseDatabase.getInstance().getReference("users").child(userId).child("futureTrips").child(trip.tripId)

        // Fetch and update budget
        tripRef.child("totalBudget").get().addOnSuccessListener { snapshot ->
            val updatedBudget = snapshot.getValue(Double::class.java) ?: 0.0
            holder.tvBudget.text = "Total Budget: $${updatedBudget}"
        }

        // Set click listener to open TripDetailsActivity
        holder.itemView.setOnClickListener {
            val intent = Intent(holder.itemView.context, TripDetailsActivity::class.java)
            intent.putExtra("tripId", trip.tripId)

            // Fetch attractions before starting the activity
            tripRef.child("attractions").get().addOnSuccessListener { snapshot ->
                val attractionsList = mutableListOf<Attraction>()
                for (attraction in snapshot.children) {
                    val name = attraction.child("name").value.toString()
                    val cost = attraction.child("cost").getValue(Double::class.java) ?: 0.0
                    attractionsList.add(Attraction(name, cost))
                }

                intent.putExtra("attractions", ArrayList(attractionsList))
                holder.itemView.context.startActivity(intent)
            }
        }

        holder.btnDelete.setOnClickListener {
            tripRef.removeValue().addOnSuccessListener {
                onDelete(trip.tripId)
            }
        }
    }


    override fun getItemCount(): Int = tripList.size

    class TripViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvDestination: TextView = itemView.findViewById(R.id.tv_trip_destination)
        val tvDates: TextView = itemView.findViewById(R.id.tv_trip_dates)
        val tvBudget: TextView = itemView.findViewById(R.id.tv_trip_budget)
        val btnDelete: Button = itemView.findViewById(R.id.btn_delete_trip)
    }

    fun updateTrips(newTrips: List<Trip>) {
        tripList = newTrips
        notifyDataSetChanged()
    }
}
