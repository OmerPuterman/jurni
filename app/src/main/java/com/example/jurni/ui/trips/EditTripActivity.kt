package com.example.jurni.ui.trips

import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.jurni.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.util.Calendar

class EditTripActivity : AppCompatActivity() {

    private lateinit var etDestination: EditText
    private lateinit var tvStartDate: TextView
    private lateinit var tvEndDate: TextView
    private lateinit var etTotalBudget: EditText
    private lateinit var btnSaveChanges: Button
    private var tripId: String? = null
    private lateinit var databaseRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_trip)

        // Initialize UI elements matching XML
        etDestination = findViewById(R.id.et_edit_destination)
        tvStartDate = findViewById(R.id.tv_edit_start_date)
        tvEndDate = findViewById(R.id.tv_edit_end_date)
        etTotalBudget = findViewById(R.id.et_edit_budget)
        btnSaveChanges = findViewById(R.id.btn_save_trip)

        // Get Trip ID from Intent
        tripId = intent.getStringExtra("tripId")
        if (tripId == null) {
            Toast.makeText(this, "Error: Missing trip ID", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        databaseRef = FirebaseDatabase.getInstance().getReference("users").child(userId).child("futureTrips").child(tripId!!)

        // Load trip details from Firebase
        loadTripDetails()

        // Set up date picker dialogs
        tvStartDate.setOnClickListener { openDatePicker(tvStartDate) }
        tvEndDate.setOnClickListener { openDatePicker(tvEndDate) }

        // Save changes button
        btnSaveChanges.setOnClickListener {
            saveTripChanges()
        }
    }

    private fun loadTripDetails() {
        databaseRef.get().addOnSuccessListener { snapshot ->
            etDestination.setText(snapshot.child("destination").value.toString())
            tvStartDate.text = snapshot.child("startDate").value.toString()
            tvEndDate.text = snapshot.child("endDate").value.toString()
            etTotalBudget.setText(snapshot.child("totalBudget").getValue(Double::class.java)?.toString() ?: "0.0")
        }.addOnFailureListener {
            Toast.makeText(this, "Failed to load trip details", Toast.LENGTH_SHORT).show()
        }
    }

    private fun openDatePicker(targetTextView: TextView) {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
            val formattedDate = "$selectedYear-${selectedMonth + 1}-$selectedDay"
            targetTextView.text = formattedDate
        }, year, month, day)

        datePickerDialog.show()
    }

    private fun saveTripChanges() {
        val destination = etDestination.text.toString()
        val startDate = tvStartDate.text.toString()
        val endDate = tvEndDate.text.toString()
        val totalBudget = etTotalBudget.text.toString().toDoubleOrNull() ?: 0.0

        val tripUpdates = mapOf(
            "destination" to destination,
            "startDate" to startDate,
            "endDate" to endDate,
            "totalBudget" to totalBudget
        )

        databaseRef.updateChildren(tripUpdates).addOnSuccessListener {
            Toast.makeText(this, "Trip updated successfully", Toast.LENGTH_SHORT).show()
            finish()
        }.addOnFailureListener {
            Toast.makeText(this, "Failed to update trip", Toast.LENGTH_SHORT).show()
        }
    }
}
