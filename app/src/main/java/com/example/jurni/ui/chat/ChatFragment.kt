package com.example.jurni.ui.chat

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.jurni.databinding.FragmentChatBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import java.util.*

class ChatFragment : Fragment() {

    private lateinit var binding: FragmentChatBinding
    private lateinit var database: DatabaseReference
    private lateinit var auth: FirebaseAuth
    private lateinit var postAdapter: PostAdapter
    private var postList = mutableListOf<Post>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentChatBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().getReference("posts")

        // 🔹 Initialize RecyclerView
        binding.recyclerViewPosts.layoutManager = LinearLayoutManager(requireContext())
        postAdapter = PostAdapter(postList) { post ->
            Toast.makeText(requireContext(), "Post Clicked: ${post.description}", Toast.LENGTH_SHORT).show()
        }
        binding.recyclerViewPosts.adapter = postAdapter

        // 🔹 Load posts from Firebase
        loadPosts()

        // 🔹 Add post on button click
        binding.btnAddPost.setOnClickListener {
            addPost()
        }
    }

    private fun loadPosts() {
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                postList.clear()
                for (postSnapshot in snapshot.children) {
                    val post = postSnapshot.getValue(Post::class.java)
                    post?.let { postList.add(it) }
                }
                postAdapter.notifyDataSetChanged()
                Log.d("ChatFragment", "Posts loaded: ${postList.size}")
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("ChatFragment", "Error loading posts: ${error.message}")
            }
        })
    }

    private fun addPost() {
        val description = binding.etDescription.text.toString().trim()
        if (description.isEmpty()) {
            Toast.makeText(requireContext(), "Please enter a description", Toast.LENGTH_SHORT).show()
            return
        }

        val userId = auth.currentUser?.uid ?: "unknown_user"
        val postId = database.push().key ?: UUID.randomUUID().toString()

        val defaultImageUrl = "https://via.placeholder.com/300" // Temporary image
        val newPost = Post(postId, defaultImageUrl, description, userId, System.currentTimeMillis())

        database.child(postId).setValue(newPost)
            .addOnSuccessListener {
                binding.etDescription.text.clear()
                Toast.makeText(requireContext(), "Post added!", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Log.e("ChatFragment", "Error adding post: ${e.message}")
            }
    }
}
