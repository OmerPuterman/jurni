package com.example.jurni.ui.chat

data class Post(
    val postId: String = "",
    val imageUrl: String = "",
    val description: String = "",
    val userId: String = "",
    val timestamp: Long = 0
)
