package com.compose.chatting.data

data class Message(
    val text: String = "",
    val senderEmail: String = "",
    val timestamp: Long = System.currentTimeMillis()
)