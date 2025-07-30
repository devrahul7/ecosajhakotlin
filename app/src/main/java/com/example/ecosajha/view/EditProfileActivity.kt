package com.example.ecosajha.view

import android.app.Activity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.animation.core.*
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
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import com.example.ecosajha.repository.UserRepositoryImpl
import com.example.ecosajha.viewmodel.UserViewModel

// Modern vibrant color palette
private val VibrantGreen = Color(0xFF00E676)
private val DeepGreen = Color(0xFF00C853)
private val LightGreen = Color(0xFF69F0AE)
private val AccentBlue = Color(0xFF00B0FF)
private val AccentPurple = Color(0xFF7C4DFF)
private val AccentOrange = Color(0xFFFF6D00)
private val AccentPink = Color(0xFFE91E63)
private val AccentYellow = Color(0xFFFFD600)
private val BackgroundGradient = listOf(Color(0xFFF0FFF0), Color(0xFFE8F5E8), Color(0xFFF3E5F5))
private val SurfaceWhite = Color(0xFFFFFFFF)

class EditProfileActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MaterialTheme {
                EditProfileScreen()
            }
        }
    }
}

@Composable
fun FloatingUserIcon() {
    val infiniteTransition = rememberInfiniteTransition()

    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(15000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
    )

    val scale by infiniteTransition.animateFloat(
        initialValue = 0.9f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    Icon(
        imageVector = Icons.Default.AccountCircle,
        contentDescription = "Floating User",
        tint = VibrantGreen.copy(alpha = 0.7f),
        modifier = Modifier
            .size(24.dp)
            .rotate(rotation)
            .scale(scale)
    )
}

@Composable
fun AnimatedProfileAvatar(name: String) {
    val infiniteTransition = rememberInfiniteTransition()

    val borderRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(8000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
    )

    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    Box(
        modifier = Modifier
            .size(100.dp)
            .scale(scale),
        contentAlignment = Alignment.Center
    ) {
        // Animated border
        Box(
            modifier = Modifier
                .size(100.dp)
                .rotate(borderRotation)
                .background(
                    brush = Brush.sweepGradient(
                        colors = listOf(
                            VibrantGreen,
                            AccentBlue,
                            AccentPurple,
                            AccentOrange,
                            AccentPink,
                            VibrantGreen
                        )
                    ),
                    shape = CircleShape
                )
        )

        // Inner avatar
        Box(
            modifier = Modifier
                .size(88.dp)
                .clip(CircleShape)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            VibrantGreen.copy(alpha = 0.9f),
                            DeepGreen
                        )
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = name.take(2).uppercase().ifEmpty { "👤" },
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
    }
}

@Composable
fun EditProfileScreen() {
    val context = LocalContext.current
    val activity = context as? Activity

    val userRepo = remember { UserRepositoryImpl() }
    val userViewModel = remember { UserViewModel(userRepo) }

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
        currentAddress = currentAddress,
        userViewModel = userViewModel
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileBody(
    userID: String,
    currentName: String,
    currentEmail: String,
    currentPhone: String,
    currentAddress: String,
    userViewModel: UserViewModel? = null
) {
    var fullName by remember { mutableStateOf(currentName) }
    var email by remember { mutableStateOf(currentEmail) }
    var phoneNumber by remember { mutableStateOf(currentPhone) }
    var address by remember { mutableStateOf(currentAddress) }
    var isUpdating by remember { mutableStateOf(false) }
    var isVisible by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val activity = context as? Activity

    // Animate content appearance
    LaunchedEffect(Unit) {
        delay(300)
        isVisible = true
    }

    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        FloatingUserIcon()
                        AnimatedVisibility(
                            visible = isVisible,
                            enter = slideInHorizontally(
                                initialOffsetX = { it },
                                animationSpec = tween(800, easing = FastOutSlowInEasing)
                            )
                        ) {
                            Text(
                                "✏️ Edit Profile",
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                fontSize = 18.sp
                            )
                        }
                    }
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
                    containerColor = Color.Transparent
                ),
                modifier = Modifier
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(VibrantGreen, AccentBlue, AccentPurple)
                        )
                    )
                    .shadow(8.dp)
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(BackgroundGradient)
                )
        ) {
            AnimatedVisibility(
                visible = isVisible,
                enter = fadeIn(
                    animationSpec = tween(1000, easing = FastOutSlowInEasing)
                )
            ) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(16.dp)
                ) {
                    item {
                        // Animated Profile Picture Section
                        AnimatedVisibility(
                            visible = isVisible,
                            enter = slideInVertically(
                                initialOffsetY = { -it },
                                animationSpec = tween(800, delayMillis = 200)
                            )
                        ) {
                            ProfilePictureCard(fullName = fullName)
                        }
                    }

                    item {
                        // Animated Edit Form
                        AnimatedVisibility(
                            visible = isVisible,
                            enter = slideInHorizontally(
                                initialOffsetX = { -it },
                                animationSpec = tween(800, delayMillis = 400)
                            )
                        ) {
                            EditFormCard(
                                fullName = fullName,
                                email = email,
                                phoneNumber = phoneNumber,
                                address = address,
                                isUpdating = isUpdating,
                                onNameChange = { fullName = it },
                                onEmailChange = { email = it },
                                onPhoneChange = { phoneNumber = it },
                                onAddressChange = { address = it }
                            )
                        }
                    }

                    item {
                        // Animated Save Button
                        AnimatedVisibility(
                            visible = isVisible,
                            enter = slideInVertically(
                                initialOffsetY = { it },
                                animationSpec = tween(800, delayMillis = 600)
                            )
                        ) {
                            SaveButton(
                                isUpdating = isUpdating,
                                onClick = {
                                    when {
                                        fullName.isBlank() -> {
                                            Toast.makeText(context, "Please enter your full name", Toast.LENGTH_SHORT).show()
                                        }
                                        email.isBlank() -> {
                                            Toast.makeText(context, "Please enter your email", Toast.LENGTH_SHORT).show()
                                        }
                                        else -> {
                                            if (userViewModel != null) {
                                                isUpdating = true

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
                                            } else {
                                                Toast.makeText(context, "✅ Profile updated successfully!", Toast.LENGTH_SHORT).show()
                                            }
                                        }
                                    }
                                }
                            )
                        }
                    }

                    item {
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun ProfilePictureCard(fullName: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(8.dp, RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            VibrantGreen.copy(alpha = 0.05f)
                        )
                    )
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                AnimatedProfileAvatar(name = fullName)

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(text = "📸", fontSize = 20.sp)
                    Text(
                        text = "Profile Picture",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = DeepGreen
                    )
                }

                Text(
                    text = "Your profile avatar is generated from your name",
                    fontSize = 14.sp,
                    color = Color.Gray,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
fun EditFormCard(
    fullName: String,
    email: String,
    phoneNumber: String,
    address: String,
    isUpdating: Boolean,
    onNameChange: (String) -> Unit,
    onEmailChange: (String) -> Unit,
    onPhoneChange: (String) -> Unit,
    onAddressChange: (String) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(8.dp, RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(text = "👤", fontSize = 24.sp)
                Text(
                    text = "Personal Information",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = DeepGreen
                )
            }

            // Full Name Field
            AnimatedTextField(
                value = fullName,
                onValueChange = onNameChange,
                label = "👤 Full Name",
                placeholder = "Enter your full name",
                icon = Icons.Default.Person,
                iconColor = VibrantGreen,
                enabled = !isUpdating,
                delay = 0
            )

            // Email Field
            AnimatedTextField(
                value = email,
                onValueChange = onEmailChange,
                label = "📧 Email Address",
                placeholder = "Enter your email address",
                icon = Icons.Default.Email,
                iconColor = AccentBlue,
                enabled = !isUpdating,
                delay = 200
            )

            // Phone Number Field
            AnimatedTextField(
                value = phoneNumber,
                onValueChange = onPhoneChange,
                label = "📱 Phone Number",
                placeholder = "Enter your phone number",
                icon = Icons.Default.Phone,
                iconColor = AccentOrange,
                enabled = !isUpdating,
                delay = 400
            )

            // Address Field
            AnimatedTextField(
                value = address,
                onValueChange = onAddressChange,
                label = "🏠 Address",
                placeholder = "Enter your complete address",
                icon = Icons.Default.LocationOn,
                iconColor = AccentPurple,
                enabled = !isUpdating,
                minLines = 2,
                delay = 600
            )
        }
    }
}

@Composable
fun AnimatedTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconColor: Color,
    enabled: Boolean,
    minLines: Int = 1,
    delay: Int
) {
    var isVisible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(delay.toLong())
        isVisible = true
    }

    AnimatedVisibility(
        visible = isVisible,
        enter = slideInVertically(
            initialOffsetY = { it },
            animationSpec = tween(500, easing = FastOutSlowInEasing)
        ) + fadeIn()
    ) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            label = { Text(label, fontSize = 14.sp) },
            placeholder = { Text(placeholder, fontSize = 14.sp) },
            leadingIcon = {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    tint = iconColor
                )
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = enabled,
            minLines = minLines,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = iconColor,
                focusedLabelColor = iconColor,
                cursorColor = iconColor,
                focusedLeadingIconColor = iconColor
            ),
            shape = RoundedCornerShape(12.dp)
        )
    }
}

@Composable
fun SaveButton(
    isUpdating: Boolean,
    onClick: () -> Unit
) {
    var isPressed by remember { mutableStateOf(false) }
    val infiniteTransition = rememberInfiniteTransition()

    val gradientShift by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)
    )

    Button(
        onClick = {
            isPressed = true
            onClick()
        },
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .scale(scale)
            .shadow(8.dp, RoundedCornerShape(16.dp)),
        enabled = !isUpdating,
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent
        ),
        shape = RoundedCornerShape(16.dp),
        contentPadding = PaddingValues(0.dp)
    ) {
        LaunchedEffect(isPressed) {
            if (isPressed) {
                delay(200)
                isPressed = false
            }
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = if (isUpdating) {
                        Brush.horizontalGradient(
                            colors = listOf(
                                Color.Gray.copy(alpha = 0.7f),
                                Color.Gray.copy(alpha = 0.5f)
                            )
                        )
                    } else {
                        Brush.linearGradient(
                            colors = listOf(
                                VibrantGreen,
                                AccentBlue,
                                AccentPurple
                            ),
                            start = androidx.compose.ui.geometry.Offset(
                                gradientShift * 1000f,
                                0f
                            ),
                            end = androidx.compose.ui.geometry.Offset(
                                1000f + gradientShift * 1000f,
                                1000f
                            )
                        )
                    }
                ),
            contentAlignment = Alignment.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                if (isUpdating) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .background(
                                Color.White.copy(alpha = 0.3f),
                                CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            color = Color.White,
                            strokeWidth = 2.dp
                        )
                    }
                } else {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .background(
                                Color.White.copy(alpha = 0.3f),
                                CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Save",
                            modifier = Modifier.size(18.dp),
                            tint = Color.White
                        )
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                Text(
                    text = if (isUpdating) "💾 Saving..." else "💾 Save Changes",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}

// Preview Functions
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun EditProfileScreenPreview() {
    MaterialTheme {
        EditProfileBody(
            userID = "sample_user_id",
            currentName = "Sita Shah",
            currentEmail = "sita@gmail.com",
            currentPhone = "+977-9742869215",
            currentAddress = "Kathmandu, Nepal"
        )
    }
}

@Preview(showBackground = true, showSystemUi = true, name = "Empty Fields")
@Composable
fun EditProfileScreenEmptyPreview() {
    MaterialTheme {
        EditProfileBody(
            userID = "sample_user_id",
            currentName = "",
            currentEmail = "",
            currentPhone = "",
            currentAddress = ""
        )
    }
}

@Preview(showBackground = true, showSystemUi = true, name = "Long Name")
@Composable
fun EditProfileLongNamePreview() {
    MaterialTheme {
        EditProfileBody(
            userID = "sample_user_id",
            currentName = "Rohit Kumar Shah Thakuri",
            currentEmail = "rohitshah.thakuri@gmail.com",
            currentPhone = "+977-9863481707",
            currentAddress = "Pokhara Metropolitan City, Gandaki Province, Nepal"
        )
    }
}
