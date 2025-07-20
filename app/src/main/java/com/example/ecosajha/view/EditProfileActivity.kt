package com.example.ecosajha.view

import android.app.Activity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ecosajha.model.UserModel
import com.example.ecosajha.repository.UserRepositoryImpl
import com.example.ecosajha.viewmodel.UserViewModel

// Use the same color scheme
private val EcoGreen = Color(0xFF4CAF50)
private val EcoGreenDark = Color(0xFF388E3C)
private val EcoGreenLight = Color(0xFFC8E6C9)
private val EcoBackground = Color(0xFFF1F8E9)

class EditProfileActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            EditProfileScreen()
        }
    }
}

@Composable
fun EditProfileScreen() {
    val context = LocalContext.current
    val activity = context as? Activity

    // Get user data from intent
    val userID = activity?.intent?.getStringExtra("userID") ?: ""
    val currentName = activity?.intent?.getStringExtra("fullName") ?: ""
    val currentEmail = activity?.intent?.getStringExtra("email") ?: ""
    val currentPhone = activity?.intent?.getStringExtra("phoneNumber") ?: ""
    val currentAddress = activity?.intent?.getStringExtra("address") ?: ""

    EditProfileBody(
        userID = userID,
        currentName = currentName,
        currentEmail = currentEmail,
        currentPhone = currentPhone,
        currentAddress = currentAddress
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileBody(
    userID: String,
    currentName: String,
    currentEmail: String,
    currentPhone: String,
    currentAddress: String
) {
    var fullName by remember { mutableStateOf(currentName) }
    var email by remember { mutableStateOf(currentEmail) }
    var phoneNumber by remember { mutableStateOf(currentPhone) }
    var address by remember { mutableStateOf(currentAddress) }
    var isUpdating by remember { mutableStateOf(false) }

    val userRepo = remember { UserRepositoryImpl() }
    val userViewModel = remember { UserViewModel(userRepo) }

    val context = LocalContext.current
    val activity = context as? Activity

    Scaffold(
        containerColor = EcoBackground,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Edit Profile",
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { activity?.finish() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = EcoGreen
                )
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            EcoBackground,
                            Color(0xFFE8F5E8)
                        )
                    )
                ),
            verticalArrangement = Arrangement.spacedBy(20.dp),
            contentPadding = PaddingValues(16.dp)
        ) {
            item {
                // Profile Picture Section
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(100.dp)
                                .clip(CircleShape)
                                .background(
                                    Brush.radialGradient(
                                        colors = listOf(EcoGreen, EcoGreenDark)
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = fullName.take(2).uppercase().ifEmpty { "U" },
                                fontSize = 36.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "Profile Picture",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            color = EcoGreenDark
                        )
                    }
                }
            }

            item {
                // Edit Form
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text(
                            text = "Personal Information",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = EcoGreenDark
                        )

                        // Full Name Field
                        OutlinedTextField(
                            value = fullName,
                            onValueChange = { fullName = it },
                            label = { Text("Full Name") },
                            placeholder = { Text("Enter your full name") },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = "Name",
                                    tint = EcoGreen
                                )
                            },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = !isUpdating,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = EcoGreen,
                                focusedLabelColor = EcoGreen,
                                cursorColor = EcoGreen
                            ),
                            shape = RoundedCornerShape(12.dp)
                        )

                        // Email Field
                        OutlinedTextField(
                            value = email,
                            onValueChange = { email = it },
                            label = { Text("Email Address") },
                            placeholder = { Text("Enter your email") },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Email,
                                    contentDescription = "Email",
                                    tint = EcoGreen
                                )
                            },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = !isUpdating,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = EcoGreen,
                                focusedLabelColor = EcoGreen,
                                cursorColor = EcoGreen
                            ),
                            shape = RoundedCornerShape(12.dp)
                        )

                        // Phone Number Field
                        OutlinedTextField(
                            value = phoneNumber,
                            onValueChange = { phoneNumber = it },
                            label = { Text("Phone Number") },
                            placeholder = { Text("Enter your phone number") },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Phone,
                                    contentDescription = "Phone",
                                    tint = EcoGreen
                                )
                            },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = !isUpdating,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = EcoGreen,
                                focusedLabelColor = EcoGreen,
                                cursorColor = EcoGreen
                            ),
                            shape = RoundedCornerShape(12.dp)
                        )

                        // Address Field
                        OutlinedTextField(
                            value = address,
                            onValueChange = { address = it },
                            label = { Text("Address") },
                            placeholder = { Text("Enter your address") },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.LocationOn,
                                    contentDescription = "Address",
                                    tint = EcoGreen
                                )
                            },
                            modifier = Modifier.fillMaxWidth(),
                            minLines = 2,
                            enabled = !isUpdating,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = EcoGreen,
                                focusedLabelColor = EcoGreen,
                                cursorColor = EcoGreen
                            ),
                            shape = RoundedCornerShape(12.dp)
                        )
                    }
                }
            }

            item {
                // Save Button
                Button(
                    onClick = {
                        when {
                            fullName.isBlank() -> {
                                Toast.makeText(context, "Please enter your full name", Toast.LENGTH_SHORT).show()
                            }
                            email.isBlank() -> {
                                Toast.makeText(context, "Please enter your email", Toast.LENGTH_SHORT).show()
                            }
                            else -> {
                                isUpdating = true

                                // Create update data map
                                val updateData = mutableMapOf<String, Any?>(
                                    "fullName" to fullName,
                                    "email" to email,
                                    "phoneNumber" to phoneNumber,
                                    "address" to address
                                )

                                userViewModel.updateProfile(userID, updateData) { success, message ->
                                    isUpdating = false
                                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                                    if (success) {
                                        activity?.finish()
                                    }
                                }
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    enabled = !isUpdating,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = EcoGreen,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(16.dp),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 6.dp)
                ) {
                    if (isUpdating) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                    } else {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Save",
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                    }
                    Text(
                        if (isUpdating) "Saving..." else "Save Changes",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}
