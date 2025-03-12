package com.example.jurni.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.example.jurni.databinding.FragmentProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().getReference("users")

        loadUserProfile()

        // Navigate to Edit Profile when "Edit Profile" button is clicked
        binding.btnEditProfile.setOnClickListener {
            navigateToEditProfile()
        }

        return binding.root
    }

    private fun loadUserProfile() {
        val userId = auth.currentUser?.uid
        if (userId != null) {
            database.child(userId).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        binding.tvUserName.text = snapshot.child("fullName").value?.toString() ?: "N/A"
                        binding.tvUserEmail.text = snapshot.child("email").value?.toString() ?: "N/A"
                        binding.tvDescription.text = snapshot.child("description").value?.toString() ?: "No description set"
                        binding.tvFavoriteHoliday.text = snapshot.child("favoriteHoliday").value?.toString() ?: "Not set"
                        binding.tvOtherInfo.text = snapshot.child("otherInfo").value?.toString() ?: "Not set"
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(requireContext(), "Failed to load profile", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }

    private fun navigateToEditProfile() {
        val action = ProfileFragmentDirections.actionNavProfileToEditProfileFragment()
        binding.root.findNavController().navigate(action)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
