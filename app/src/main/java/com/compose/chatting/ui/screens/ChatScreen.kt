package com.compose.chatting.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.compose.chatting.data.Message
import com.compose.chatting.navigation.Screen
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(navController: NavController) {
    var messageText by remember { mutableStateOf("") }
    var messages by remember { mutableStateOf(listOf<Message>()) }
    val auth = FirebaseAuth.getInstance()
    val currentUserEmail = auth.currentUser?.email ?: "Unknown"
    val firestore = Firebase.firestore

    LaunchedEffect(Unit) {
        firestore.collection("messages")
            .orderBy("timestamp", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, _ ->
                if (snapshot != null) {
                    messages = snapshot.documents.mapNotNull { doc ->
                        doc.toObject(Message::class.java)
                    }
                }
            }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("One-To-Many Group Chat") },
                actions = {
                    IconButton(onClick = {
                        auth.signOut()
                        navController.navigate(Screen.Login.route) {
                            popUpTo(Screen.Chat.route) { inclusive = true }
                        }
                    }) {
                        Icon(Icons.Default.ExitToApp, contentDescription = "Logout")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                items(messages) { message ->
                    MessageBubble(message)
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = messageText,
                    onValueChange = { messageText = it },
                    modifier = Modifier
                        .weight(1f)
                        .border(1.dp, Color(0xFF2B8CD9))
                    ,
                    placeholder = { Text("Type a message") },
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        cursorColor = Color(0xFF2B8CD9)
                    )
                )

                Spacer(modifier = Modifier.width(8.dp))

                IconButton(
                    onClick = {
                        if (messageText.isNotBlank()) {
                            val message = Message(
                                text = messageText,
                                senderEmail = currentUserEmail,
                                timestamp = System.currentTimeMillis()
                            )
                            firestore.collection("messages")
                                .add(message)
                                .addOnSuccessListener {
                                    messageText = ""
                                }
                        }
                    },
                    modifier = Modifier
                        .background(Color(0xFF2B8CD9), shape = CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.Send,
                        contentDescription = "Send message",
                        tint = Color.White
                    )
                }
            }
        }
    }
}

@Composable
fun MessageBubble(message: Message) {
    val currentUserEmail = FirebaseAuth.getInstance().currentUser?.email
    val isCurrentUser = message.senderEmail == currentUserEmail

    val dateFormat = remember { SimpleDateFormat("HH:mm", Locale.getDefault()) }
    val timeString = remember(message.timestamp) {
        dateFormat.format(Date(message.timestamp))
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalAlignment = if (isCurrentUser) Alignment.End else Alignment.Start
    ) {
        Surface(
            color = if (isCurrentUser) Color(0xFF4CA9F4) else Color(0xFFA0A1A1),
            shape = MaterialTheme.shapes.medium,
            modifier = Modifier
                .widthIn(max = 340.dp)
                .padding(horizontal = 8.dp)
        ) {
            Column(
                modifier = Modifier.padding(
                    start = 12.dp,
                    end = 12.dp,
                    top = 8.dp,
                    bottom = 8.dp
                )
            ) {
                Text(
                    text = message.text,
                    color = Color.White,
                )

                Spacer(modifier = Modifier.height(2.dp))

                Row(
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.wrapContentWidth()
                ) {
                    Text(
                        text = timeString,
                        color = Color.White,
                        style = MaterialTheme.typography.bodySmall
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = message.senderEmail.substringBefore('@'), // chop off post @ part
                        color = Color.White,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
}