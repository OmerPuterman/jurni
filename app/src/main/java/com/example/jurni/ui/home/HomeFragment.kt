package com.example.jurni.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.jurni.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import java.text.SimpleDateFormat
import java.util.*

class HomeFragment : Fragment() {

    private lateinit var tvClosestTrip: TextView
    private lateinit var tvRecentTrip: TextView
    private lateinit var databaseRef: DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        // Initialize UI elements
        tvClosestTrip = view.findViewById(R.id.tv_closest_trip)
        tvRecentTrip = view.findViewById(R.id.tv_recent_trip)

        // Fetch trips from Firebase
        fetchTrips()

        return view
    }

    private fun fetchTrips() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        databaseRef = FirebaseDatabase.getInstance().getReference("users").child(userId).child("futureTrips")

        databaseRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (!snapshot.exists()) {
                    tvClosestTrip.text = "No upcoming trips"
                    tvRecentTrip.text = "No recently added trips"
                    return
                }

                var closestTrip: DataSnapshot? = null
                var recentTrip: DataSnapshot? = null

                val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                val today = Calendar.getInstance().time

                for (trip in snapshot.children) {
                    val tripDateStr = trip.child("startDate").value.toString()
                    val timestamp = trip.child("timestamp").getValue(Long::class.java) ?: 0L

                    try {
                        val tripDate = dateFormat.parse(tripDateStr)
                        if (tripDate != null) {
                            // Find the closest upcoming trip
                            if ((closestTrip == null || tripDate.before(dateFormat.parse(closestTrip.child("startDate").value.toString()))) && tripDate.after(today)) {
                                closestTrip = trip
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }

                    // Find the most recently added trip
                    if (recentTrip == null || timestamp > (recentTrip.child("timestamp").getValue(Long::class.java) ?: 0L)) {
                        recentTrip = trip
                    }
                }

                // Update UI
                closestTrip?.let {
                    val destination = it.child("destination").value.toString()
                    val startDate = it.child("startDate").value.toString()
                    tvClosestTrip.text = "Destination: $destination\nDate: $startDate"
                } ?: run {
                    tvClosestTrip.text = "No upcoming trips"
                }

                recentTrip?.let {
                    val destination = it.child("destination").value.toString()
                    val startDate = it.child("startDate").value.toString()
                    tvRecentTrip.text = "Destination: $destination\nAdded: $startDate"
                } ?: run {
                    tvRecentTrip.text = "No recently added trips"
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(requireContext(), "Failed to load trips", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
