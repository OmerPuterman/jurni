package com.example.jurni.ui.trips

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.jurni.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import java.util.*

class AddFutureTripActivity : AppCompatActivity() {

    private lateinit var etDestination: EditText
    private lateinit var etStartDate: EditText
    private lateinit var etEndDate: EditText
    private lateinit var etFlightCost: EditText
    private lateinit var etHotelCost: EditText
    private lateinit var tvTotalBudget: TextView
    private lateinit var attractionsContainer: LinearLayout
    private var attractionsList = mutableListOf<Pair<String, Double>>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_add_trip) // Keeping your name

        // Initialize UI components
        etDestination = findViewById(R.id.et_destination)
        etStartDate = findViewById(R.id.et_start_date)
        etEndDate = findViewById(R.id.et_end_date)
        etFlightCost = findViewById(R.id.et_flight_cost)
        etHotelCost = findViewById(R.id.et_hotel_cost)
        tvTotalBudget = findViewById(R.id.tv_total_budget)
        attractionsContainer = findViewById(R.id.attractions_container)

        // Date Pickers
        etStartDate.setOnClickListener { showDatePicker(etStartDate) }
        etEndDate.setOnClickListener { showDatePicker(etEndDate) }

        // Add Attraction Button
        findViewById<View>(R.id.btn_add_attraction).setOnClickListener {
            addAttractionField()
        }

        // Save Trip Button
        findViewById<View>(R.id.btn_save_trip).setOnClickListener {
            val destination = etDestination.text.toString()
            val startDate = etStartDate.text.toString()
            val endDate = etEndDate.text.toString()

            // Calculate total budget before saving
            calculateTotalBudget()
            val totalBudget = tvTotalBudget.text.toString().replace("Total Budget: $", "").toDoubleOrNull() ?: 0.0

            if (destination.isEmpty() || startDate.isEmpty() || endDate.isEmpty()) {
                Toast.makeText(this, "Please fill in all required fields", Toast.LENGTH_SHORT).show()
            } else {
                saveTripToFirebase(destination, startDate, endDate, totalBudget)
            }
        }
    }

    private fun showDatePicker(editText: EditText) {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePicker = DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
            val date = "$selectedYear-${selectedMonth + 1}-$selectedDay"
            editText.setText(date)
        }, year, month, day)

        datePicker.show()
    }

    private fun addAttractionField() {
        val attractionView = layoutInflater.inflate(R.layout.item_attraction, null)
        attractionsContainer.addView(attractionView)
    }

    private fun calculateTotalBudget() {
        val flightCost = etFlightCost.text.toString().toDoubleOrNull() ?: 0.0
        val hotelCost = etHotelCost.text.toString().toDoubleOrNull() ?: 0.0
        var attractionsCost = 0.0

        attractionsList.clear()
        for (i in 0 until attractionsContainer.childCount) {
            val view = attractionsContainer.getChildAt(i)
            val etAttractionName = view.findViewById<EditText>(R.id.et_attraction_name)
            val etAttractionCost = view.findViewById<EditText>(R.id.et_attraction_cost)
            val name = etAttractionName.text.toString()
            val cost = etAttractionCost.text.toString().toDoubleOrNull() ?: 0.0

            if (name.isNotEmpty()) {
                attractionsList.add(Pair(name, cost))
                attractionsCost += cost
            }
        }

        val total = flightCost + hotelCost + attractionsCost
        tvTotalBudget.text = "Total Budget: $$total"
    }

    private fun saveTripToFirebase(destination: String, startDate: String, endDate: String, totalBudget: Double) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val databaseRef = FirebaseDatabase.getInstance().getReference("users").child(userId).child("futureTrips")

        val tripId = databaseRef.push().key ?: return
        val timestamp = System.currentTimeMillis() // Save the current timestamp

        val trip = mapOf(
            "destination" to destination,
            "startDate" to startDate,
            "endDate" to endDate,
            "totalBudget" to totalBudget,
            "timestamp" to timestamp // 🔥 Save when the trip was added
        )

        databaseRef.child(tripId).setValue(trip).addOnSuccessListener {
            Toast.makeText(this, "Trip added successfully!", Toast.LENGTH_SHORT).show()
            finish()
        }.addOnFailureListener {
            Toast.makeText(this, "Failed to add trip", Toast.LENGTH_SHORT).show()
        }
    }

}