package com.example.jurni.ui.trips
import android.content.Intent
import com.example.jurni.R

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.jurni.ui.trips.Trip
import com.example.jurni.ui.trips.TripAdapter
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class TripsFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var tripsAdapter: TripAdapter
    private lateinit var tripsList: MutableList<Trip>
    private lateinit var databaseRef: DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_trips, container, false)

        recyclerView = view.findViewById(R.id.recycler_view_trips)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        tripsList = mutableListOf()
        tripsAdapter = TripAdapter(tripsList) { tripId -> deleteTrip(tripId) }
        recyclerView.adapter = tripsAdapter

        fetchTripsFromFirebase()

        val fabAddTrip: FloatingActionButton = view.findViewById(R.id.fab_add_trip)
        fabAddTrip.setOnClickListener {
            val intent = Intent(requireContext(), AddFutureTripActivity::class.java)
            startActivity(intent)
        }

        return view
    }

    private fun fetchTripsFromFirebase() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        databaseRef = FirebaseDatabase.getInstance().getReference("users").child(userId).child("futureTrips")

        databaseRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                tripsList.clear()
                for (tripSnapshot in snapshot.children) {
                    val tripId = tripSnapshot.key ?: ""
                    val trip = tripSnapshot.getValue(Trip::class.java)?.copy(tripId = tripId)
                    if (trip != null) tripsList.add(trip)
                }
                tripsAdapter.updateTrips(tripsList)
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(requireContext(), "Failed to load trips", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun deleteTrip(tripId: String) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val tripRef = FirebaseDatabase.getInstance().getReference("users").child(userId).child("futureTrips").child(tripId)

        tripRef.removeValue().addOnSuccessListener {
            Toast.makeText(requireContext(), "Trip deleted", Toast.LENGTH_SHORT).show()
        }.addOnFailureListener {
            Toast.makeText(requireContext(), "Failed to delete trip", Toast.LENGTH_SHORT).show()
        }
    }

}
