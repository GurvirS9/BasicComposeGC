package com.compose.chatting.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.compose.chatting.navigation.Screen
import com.google.firebase.auth.FirebaseAuth

@Composable
fun SignupScreen(navController: NavController) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val auth = FirebaseAuth.getInstance()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "Register here", fontSize = 32.sp, color = Color.Black)

        Spacer(modifier = Modifier.height(16.dp))

        TextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth(),
            colors = TextFieldDefaults.colors(
                unfocusedContainerColor = Color(0xFFF5F5F5),
                focusedContainerColor = Color(0xFFF5F5F5),
                focusedIndicatorColor = Color(0xFFABD6FF),
                unfocusedLabelColor = Color.DarkGray,
                focusedLabelColor = Color.Black,
                unfocusedTextColor = Color.Black,
                focusedTextColor = Color.Black,
                cursorColor = Color(0xFFABD6FF)
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        TextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            modifier = Modifier.fillMaxWidth(),
            colors = TextFieldDefaults.colors(
                unfocusedContainerColor = Color(0xFFF5F5F5),
                focusedContainerColor = Color(0xFFF5F5F5),
                focusedIndicatorColor = Color(0xFFABD6FF),
                unfocusedLabelColor = Color.DarkGray,
                focusedLabelColor = Color.Black,
                unfocusedTextColor = Color.Black,
                focusedTextColor = Color.Black,
                cursorColor = Color(0xFFABD6FF)
            ),
            visualTransformation = PasswordVisualTransformation()
        )

        Spacer(modifier = Modifier.height(16.dp))

        TextField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            label = { Text("Confirm Password") },
            modifier = Modifier.fillMaxWidth(),
            colors = TextFieldDefaults.colors(
                unfocusedContainerColor = Color(0xFFF5F5F5),
                focusedContainerColor = Color(0xFFF5F5F5),
                focusedIndicatorColor = Color(0xFFABD6FF),
                unfocusedLabelColor = Color.DarkGray,
                focusedLabelColor = Color.Black,
                unfocusedTextColor = Color.Black,
                focusedTextColor = Color.Black,
                cursorColor = Color(0xFFABD6FF)
            ),
            visualTransformation = PasswordVisualTransformation()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                if (password == confirmPassword) {
                    auth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                navController.navigate(Screen.Login.route) {
                                    popUpTo(Screen.Signup.route) { inclusive = true }
                                }
                            } else {
                                errorMessage = task.exception?.message
                            }
                        }
                } else {
                    errorMessage = "Passwords do not match"
                }
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF114AA3))
        ) {
            Text(text = "Register")
        }

        if (errorMessage != null) {
            Text(text = errorMessage!!, color = Color.Red)
        }

        Spacer(modifier = Modifier.height(16.dp))

        TextButton(
            onClick = { navController.navigate(Screen.Login.route) }
        ) {
            Text(text = "Already have an account? Login here", color = Color(0xFF416EB4))
        }
    }
}