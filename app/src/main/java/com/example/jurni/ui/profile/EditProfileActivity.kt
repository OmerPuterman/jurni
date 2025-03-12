//package com.example.jurni.ui.profile
//
//import android.os.Bundle
//import android.widget.Toast
//import androidx.appcompat.app.AppCompatActivity
//import com.example.jurni.databinding.ActivityEditProfileBinding
//import com.google.firebase.auth.FirebaseAuth
//import com.google.firebase.database.DatabaseReference
//import com.google.firebase.database.FirebaseDatabase
//
//class EditProfileActivity : AppCompatActivity() {
//
//    private lateinit var binding: ActivityEditProfileBinding
//    private lateinit var auth: FirebaseAuth
//    private lateinit var database: DatabaseReference
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        binding = ActivityEditProfileBinding.inflate(layoutInflater)
//        setContentView(binding.root)
//
//        auth = FirebaseAuth.getInstance()
//        database = FirebaseDatabase.getInstance().getReference("Users")
//
//        loadUserData()
//
//        binding.btnSave.setOnClickListener {
//            saveUserData()
//        }
//    }
//
//    private fun loadUserData() {
//        val userId = auth.currentUser?.uid
//        if (userId != null) {
//            database.child(userId).get().addOnSuccessListener { snapshot ->
//                if (snapshot.exists()) {
//                    binding.etFullName.setText(snapshot.child("fullName").getValue(String::class.java))
//                    binding.etDescription.setText(snapshot.child("description").getValue(String::class.java))
//                    binding.etFavoriteHoliday.setText(snapshot.child("favoriteHoliday").getValue(String::class.java))
//                    binding.etOtherInfo.setText(snapshot.child("otherInfo").getValue(String::class.java))
//                }
//            }
//        }
//    }
//
//    private fun saveUserData() {
//        val userId = auth.currentUser?.uid
//        if (userId != null) {
//            val userUpdates = mapOf(
//                "fullName" to binding.etFullName.text.toString(),
//                "description" to binding.etDescription.text.toString(),
//                "favoriteHoliday" to binding.etFavoriteHoliday.text.toString(),
//                "otherInfo" to binding.etOtherInfo.text.toString()
//            )
//
//            database.child(userId).updateChildren(userUpdates).addOnSuccessListener {
//                Toast.makeText(this, "Profile Updated!", Toast.LENGTH_SHORT).show()
//                finish()
//            }.addOnFailureListener {
//                Toast.makeText(this, "Failed to update profile!", Toast.LENGTH_SHORT).show()
//            }
//        }
//    }
//}
