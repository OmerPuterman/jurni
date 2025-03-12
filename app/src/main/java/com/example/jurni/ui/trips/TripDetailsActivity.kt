package com.example.jurni.ui.trips

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.jurni.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class TripDetailsActivity : AppCompatActivity() {

    private lateinit var tvTripDestination: TextView
    private lateinit var tvTripDates: TextView
    private lateinit var tvTotalBudget: TextView
    private lateinit var recyclerView: RecyclerView
    private lateinit var attractionsAdapter: AttractionsAdapter
    private lateinit var attractionsList: MutableList<Attraction>
    private lateinit var databaseRef: DatabaseReference
    private lateinit var btnEditTrip: Button
    private var tripId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_trip_details)

        // Initialize UI elements
        tvTripDestination = findViewById(R.id.tv_trip_destination)
        tvTripDates = findViewById(R.id.tv_trip_dates)
        tvTotalBudget = findViewById(R.id.tv_total_budget)
        recyclerView = findViewById(R.id.recycler_view_attractions)
        btnEditTrip = findViewById(R.id.btn_edit_trip) // Initialize the button

        recyclerView.layoutManager = LinearLayoutManager(this)
        attractionsList = mutableListOf()
        attractionsAdapter = AttractionsAdapter(attractionsList)
        recyclerView.adapter = attractionsAdapter

        // Retrieve tripId safely from Intent
        tripId = intent.getStringExtra("tripId")
        if (tripId == null) {
            Toast.makeText(this, "Trip ID is missing", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        fetchTripDetails(tripId!!)

        // Set click listener for edit button
        btnEditTrip.setOnClickListener {
            val intent = Intent(this, EditTripActivity::class.java)
            intent.putExtra("tripId", tripId) // Pass tripId
            startActivity(intent)
        }
    }

    private fun fetchTripDetails(tripId: String) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        databaseRef = FirebaseDatabase.getInstance().getReference("users").child(userId).child("futureTrips").child(tripId)

        databaseRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (!snapshot.exists()) {
                    Toast.makeText(this@TripDetailsActivity, "Trip not found", Toast.LENGTH_SHORT).show()
                    finish()
                    return
                }

                val destination = snapshot.child("destination").value?.toString() ?: "Unknown"
                val startDate = snapshot.child("startDate").value?.toString() ?: "N/A"
                val endDate = snapshot.child("endDate").value?.toString() ?: "N/A"
                val totalBudget = snapshot.child("totalBudget").getValue(Double::class.java) ?: 0.0

                tvTripDestination.text = "Destination: $destination"
                tvTripDates.text = "Dates: $startDate - $endDate"
                tvTotalBudget.text = "Total Budget: $$totalBudget"

                // Fetch attractions
                attractionsList.clear()
                val attractionsSnapshot = snapshot.child("attractions")
                for (attraction in attractionsSnapshot.children) {
                    val name = attraction.child("name").value?.toString() ?: "Unnamed"
                    val cost = attraction.child("cost").getValue(Double::class.java) ?: 0.0
                    attractionsList.add(Attraction(name, cost))
                }
                attractionsAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@TripDetailsActivity, "Failed to load trip details", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
