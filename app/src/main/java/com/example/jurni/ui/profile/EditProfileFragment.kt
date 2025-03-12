package com.example.jurni.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.jurni.databinding.FragmentEditProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class EditProfileFragment : Fragment() {

    private var _binding: FragmentEditProfileBinding? = null
    private val binding get() = _binding!!

    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditProfileBinding.inflate(inflater, container, false)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().getReference("users")

        loadUserProfile()

        binding.btnSaveProfile.setOnClickListener {
            saveUserProfile()
        }

        return binding.root
    }

    private fun loadUserProfile() {
        val userId = auth.currentUser?.uid
        if (userId != null) {
            database.child(userId).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        // ✅ Name & Email cannot be edited
                        val fullName = snapshot.child("fullName").value?.toString() ?: "Unknown User"
                        val email = snapshot.child("email").value?.toString() ?: "No Email"

                        binding.tvFullName.text = fullName
                        binding.tvEmail.text = email

                        // Editable fields
                        binding.etDescription.setText(snapshot.child("description").value?.toString() ?: "")
                        binding.etFavoriteHoliday.setText(snapshot.child("favoriteHoliday").value?.toString() ?: "")
                        binding.etOtherInfo.setText(snapshot.child("otherInfo").value?.toString() ?: "")
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(requireContext(), "Failed to load profile", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }

    private fun saveUserProfile() {
        val userId = auth.currentUser?.uid
        if (userId != null) {
            val userUpdates = mapOf(
                "description" to binding.etDescription.text.toString(),
                "favoriteHoliday" to binding.etFavoriteHoliday.text.toString(),
                "otherInfo" to binding.etOtherInfo.text.toString()
            )

            database.child(userId).updateChildren(userUpdates).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(requireContext(), "Profile updated", Toast.LENGTH_SHORT).show()
                    requireActivity().onBackPressed() // Go back to ProfileFragment
                } else {
                    Toast.makeText(requireContext(), "Failed to update profile", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
