package com.example.ecosajha.view

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import com.example.ecosajha.R
import com.example.ecosajha.repository.AuthRepositoryImpl
import com.example.ecosajha.viewmodel.AuthViewModel
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : ComponentActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Scaffold { innerPadding ->
                LoginBody(
                    innerPaddingValues = innerPadding,
                    onNavigateToResetPassword = {
                        startActivity(Intent(this, ResetPasswordActivity::class.java))
                    },
                    onNavigateToRegistration = {
                        startActivity(Intent(this, RegistrationActivity::class.java))
                        finish()
                    },
                    onLoginSuccess = {
                        startActivity(Intent(this, DashboardActivity::class.java))
                        finish()
                    }
                )
            }
        }
    }
}

@Composable
fun LoginBody(
    innerPaddingValues: PaddingValues,
    onNavigateToResetPassword: () -> Unit,
    onNavigateToRegistration: () -> Unit,
    onLoginSuccess: () -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var emailError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }
    var passwordVisibility by remember { mutableStateOf(false) }
    var rememberMe by remember { mutableStateOf(false) }

    val authViewModel = remember { AuthViewModel(AuthRepositoryImpl(FirebaseAuth.getInstance())) }
    // Collect states from AuthViewModel
    val isLoading by authViewModel.isLoading.collectAsState()
    val errorMessage by authViewModel.errorMessage.collectAsState()
    val successMessage by authViewModel.successMessage.collectAsState()

    // SharedPreferences for Remember Me functionality
    val sharedPreferences = context.getSharedPreferences("EcoSajhaUser", Context.MODE_PRIVATE)
    val editor = sharedPreferences.edit()

    // Load saved credentials if available
    LaunchedEffect(Unit) {
        val savedEmail = sharedPreferences.getString("saved_email", "") ?: ""
        val savedPassword = sharedPreferences.getString("saved_password", "") ?: ""
        val wasRemembered = sharedPreferences.getBoolean("remember_me", false)

        if (savedEmail.isNotEmpty() && wasRemembered) {
            email = savedEmail
            password = savedPassword
            rememberMe = true
        }
    }

    // Handle success messages
    LaunchedEffect(successMessage) {
        successMessage?.let { message ->
            if (message.contains("Login successful", ignoreCase = true) ||
                message.contains("success", ignoreCase = true)) {
                Toast.makeText(context, message, Toast.LENGTH_LONG).show()
                onLoginSuccess()
            }
            authViewModel.clearSuccessMessage()
        }
    }

    // Handle error messages
    LaunchedEffect(errorMessage) {
        errorMessage?.let { message ->
            snackbarHostState.showSnackbar(message)
            authViewModel.clearErrorMessage()
        }
    }

    // Enhanced email validation
    fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    // Validation function
    fun validateInputs(): Boolean {
        var isValid = true

        if (email.trim().isEmpty()) {
            emailError = "Email is required"
            isValid = false
        } else if (!isValidEmail(email.trim())) {
            emailError = "Please enter a valid email address"
            isValid = false
        } else {
            emailError = null
        }

        if (password.isEmpty()) {
            passwordError = "Password is required"
            isValid = false
        } else if (password.length < 6) {
            passwordError = "Password must be at least 6 characters"
            isValid = false
        } else {
            passwordError = null
        }

        return isValid
    }

    // Modern color scheme
    val primaryColor = Color(0xFF4CAF50) // Green theme for eco app
    val backgroundColor = Color(0xFFf8f9fa)
    val cardColor = Color.White
    val textColor = Color(0xFF212529)
    val placeholderColor = Color(0xFF6C757D)
    val errorColor = Color(0xFFDC3545)

    Scaffold(
        snackbarHost = {
            SnackbarHost(
                hostState = snackbarHostState,
                snackbar = { data ->
                    Snackbar(
                        snackbarData = data,
                        containerColor = errorColor,
                        contentColor = Color.White,
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            backgroundColor,
                            Color(0xFFe9ecef)
                        )
                    )
                )
        ) {
            Column(
                modifier = Modifier
                    .padding(innerPaddingValues)
                    .padding(24.dp)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(40.dp))

                // App Header
                Text(
                    text = "Welcome to Ecosajha Recycle",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = textColor,
                    textAlign = TextAlign.Center
                )

                Text(
                    text = "helping to keep nature clean",
                    fontSize = 16.sp,
                    color = placeholderColor,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(30.dp))

                Image(
                    painter = painterResource(R.drawable.loginimg),
                    contentDescription = "EcoSajha Logo",
                    modifier = Modifier
                        .height(200.dp)
                        .width(200.dp)
                )

                Spacer(modifier = Modifier.height(30.dp))

                // Login Card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(
                            elevation = 8.dp,
                            shape = RoundedCornerShape(16.dp),
                            ambientColor = Color.Black.copy(alpha = 0.1f)
                        ),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = cardColor)
                ) {
                    Column(
                        modifier = Modifier
                            .padding(24.dp)
                            .fillMaxWidth()
                    ) {
                        Text(
                            text = "Sign In",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = textColor,
                            modifier = Modifier.padding(bottom = 24.dp)
                        )

                        // Email Field
                        OutlinedTextField(
                            value = email,
                            onValueChange = {
                                email = it
                                emailError = null
                            },
                            label = { Text("Email") },
                            placeholder = { Text("abc@gmail.com", color = placeholderColor) },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Email,
                                    contentDescription = null,
                                    tint = primaryColor
                                )
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = primaryColor,
                                unfocusedBorderColor = Color(0xFFDEE2E6),
                                focusedLabelColor = primaryColor,
                                errorBorderColor = errorColor,
                                errorLabelColor = errorColor
                            ),
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Email
                            ),
                            isError = emailError != null,
                            supportingText = emailError?.let {
                                { Text(it, color = errorColor) }
                            },
                            singleLine = true
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Password Field
                        OutlinedTextField(
                            value = password,
                            onValueChange = {
                                password = it
                                passwordError = null
                            },
                            label = { Text("Password") },
                            placeholder = { Text("*******", color = placeholderColor) },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Lock,
                                    contentDescription = null,
                                    tint = primaryColor
                                )
                            },
                            trailingIcon = {
                                IconButton(
                                    onClick = { passwordVisibility = !passwordVisibility }
                                ) {
                                    Icon(
                                        painter = painterResource(
                                            if (passwordVisibility)
                                                R.drawable.baseline_visibility_24
                                            else
                                                R.drawable.baseline_visibility_off_24
                                        ),
                                        contentDescription = if (passwordVisibility) "Hide password" else "Show password",
                                        tint = primaryColor
                                    )
                                }
                            },
                            visualTransformation = if (passwordVisibility)
                                VisualTransformation.None
                            else
                                PasswordVisualTransformation(),
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = primaryColor,
                                unfocusedBorderColor = Color(0xFFDEE2E6),
                                focusedLabelColor = primaryColor,
                                errorBorderColor = errorColor,
                                errorLabelColor = errorColor
                            ),
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Password
                            ),
                            isError = passwordError != null,
                            supportingText = passwordError?.let {
                                { Text(it, color = errorColor) }
                            },
                            singleLine = true
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        // Remember Me and Forgot Password Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Checkbox(
                                    checked = rememberMe,
                                    onCheckedChange = { rememberMe = it },
                                    colors = CheckboxDefaults.colors(
                                        checkedColor = primaryColor,
                                        checkmarkColor = Color.White,
                                        uncheckedColor = Color(0xFFDEE2E6)
                                    )
                                )
                                Text(
                                    "Remember me",
                                    color = textColor,
                                    fontSize = 14.sp
                                )
                            }

                            Text(
                                "Forget Password",
                                color = primaryColor,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                textDecoration = TextDecoration.Underline,
                                modifier = Modifier.clickable {
                                    onNavigateToResetPassword()
                                }
                            )
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        // Login Button
                        Button(
                            onClick = {
                                if (validateInputs()) {
                                    // Save credentials if remember me is checked
                                    if (rememberMe) {
                                        editor.putString("saved_email", email.trim())
                                        editor.putString("saved_password", password)
                                        editor.putBoolean("remember_me", true)
                                        editor.apply()
                                    } else {
                                        // Clear saved credentials if not remembering
                                        editor.clear()
                                        editor.apply()
                                    }

                                    // 🔥 Use Firebase Authentication
                                    authViewModel.loginWithEmailAndPassword(email.trim(), password)
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = primaryColor,
                                disabledContainerColor = primaryColor.copy(alpha = 0.5f)
                            ),
                            elevation = ButtonDefaults.buttonElevation(
                                defaultElevation = 4.dp,
                                pressedElevation = 8.dp
                            ),
                            enabled = !isLoading
                        ) {
                            if (isLoading) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(20.dp),
                                    color = Color.White,
                                    strokeWidth = 2.dp
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    "Signing In...",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color.White
                                )
                            } else {
                                Text(
                                    "Sign In",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color.White
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        // Sign Up Link
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                "Don't have an account? ",
                                color = placeholderColor,
                                fontSize = 14.sp
                            )
                            Text(
                                "Sign Up",
                                color = primaryColor,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                textDecoration = TextDecoration.Underline,
                                modifier = Modifier.clickable {
                                    onNavigateToRegistration()
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Social Login Options
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 40.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Card(
                        modifier = Modifier
                            .size(60.dp)
                            .clickable {
                                Toast.makeText(context, "Google login not implemented yet", Toast.LENGTH_SHORT).show()
                            },
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Image(
                                painter = painterResource(R.drawable.google),
                                contentDescription = "Google Login",
                                modifier = Modifier.size(32.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(20.dp))

                    Card(
                        modifier = Modifier
                            .size(60.dp)
                            .clickable {
                                Toast.makeText(context, "Facebook login not implemented yet", Toast.LENGTH_SHORT).show()
                            },
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Image(
                                painter = painterResource(R.drawable.fb),
                                contentDescription = "Facebook Login",
                                modifier = Modifier.size(32.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}

@Preview
@Composable
fun LoginPreviewBody() {
    // For preview purposes, create mock implementations
    LoginBody(
        innerPaddingValues = PaddingValues(0.dp),
        onNavigateToResetPassword = {},
        onNavigateToRegistration = {},
        onLoginSuccess = {}
    )
}
