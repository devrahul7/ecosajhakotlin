package com.example.ecosajha.view



import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import com.example.ecosajha.R

class RegistrationActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            RegistrationBody()
        }
    }
}

@Composable
fun RegistrationBody() {
    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordVisibility by remember { mutableStateOf(false) }
    var confirmPasswordVisibility by remember { mutableStateOf(false) }
    var agreeToTerms by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val activity = context as? Activity
    val scrollState = rememberScrollState()

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(color = Color.White)
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Join Ecosajha Recycle",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            Text(
                text = "Create an account to start recycling",
                fontSize = 16.sp,
                color = Color.Gray,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(30.dp))

            Image(
                painter = painterResource(R.drawable.loginimg), // Using same image as login
                contentDescription = null,
                modifier = Modifier
                    .height(180.dp)
                    .width(180.dp)
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Full Name Field
            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp),
                shape = RoundedCornerShape(12.dp),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text
                ),
                prefix = {
                    Icon(Icons.Default.Person, contentDescription = null)
                },
                placeholder = {
                    Text("Full Name")
                },
                value = fullName,
                onValueChange = { input ->
                    fullName = input
                }
            )

            Spacer(modifier = Modifier.height(15.dp))

            // Email Field
            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp),
                shape = RoundedCornerShape(12.dp),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email
                ),
                prefix = {
                    Icon(Icons.Default.Email, contentDescription = null)
                },
                placeholder = {
                    Text("abc@gmail.com")
                },
                value = email,
                onValueChange = { input ->
                    email = input
                }
            )

            Spacer(modifier = Modifier.height(15.dp))

            // Phone Number Field
            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp),
                shape = RoundedCornerShape(12.dp),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Phone
                ),
                prefix = {
                    Icon(Icons.Default.Phone, contentDescription = null)
                },
                placeholder = {
                    Text("Phone Number")
                },
                value = phoneNumber,
                onValueChange = { input ->
                    phoneNumber = input
                }
            )

            Spacer(modifier = Modifier.height(15.dp))

            // Password Field
            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp),
                shape = RoundedCornerShape(12.dp),
                visualTransformation = if (passwordVisibility) VisualTransformation.None
                else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password
                ),
                prefix = {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = null,
                    )
                },
                suffix = {
                    Icon(
                        painter = painterResource(
                            if (passwordVisibility)
                                R.drawable.baseline_visibility_24
                            else
                                R.drawable.baseline_visibility_off_24
                        ),
                        contentDescription = null,
                        modifier = Modifier.clickable {
                            passwordVisibility = !passwordVisibility
                        }
                    )
                },
                placeholder = {
                    Text("Password")
                },
                value = password,
                onValueChange = { input ->
                    password = input
                }
            )

            Spacer(modifier = Modifier.height(15.dp))

            // Confirm Password Field
            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp),
                shape = RoundedCornerShape(12.dp),
                visualTransformation = if (confirmPasswordVisibility) VisualTransformation.None
                else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password
                ),
                prefix = {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = null,
                    )
                },
                suffix = {
                    Icon(
                        painter = painterResource(
                            if (confirmPasswordVisibility)
                                R.drawable.baseline_visibility_24
                            else
                                R.drawable.baseline_visibility_off_24
                        ),
                        contentDescription = null,
                        modifier = Modifier.clickable {
                            confirmPasswordVisibility = !confirmPasswordVisibility
                        }
                    )
                },
                placeholder = {
                    Text("Confirm Password")
                },
                value = confirmPassword,
                onValueChange = { input ->
                    confirmPassword = input
                }
            )

            Spacer(modifier = Modifier.height(15.dp))

            // Terms and Conditions Checkbox
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = agreeToTerms,
                    onCheckedChange = { checked ->
                        agreeToTerms = checked
                    },
                    colors = CheckboxDefaults.colors(
                        checkedColor = Color.Green,
                        checkmarkColor = Color.White
                    )
                )
                Text(
                    text = "I agree to the Terms and Conditions",
                    modifier = Modifier.padding(start = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Register Button
            Button(
                onClick = {
                    when {
                        fullName.isEmpty() -> {
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar("Please enter your full name")
                            }
                        }
                        email.isEmpty() -> {
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar("Please enter your email")
                            }
                        }
                        phoneNumber.isEmpty() -> {
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar("Please enter your phone number")
                            }
                        }
                        password.isEmpty() -> {
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar("Please enter a password")
                            }
                        }
                        password != confirmPassword -> {
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar("Passwords do not match")
                            }
                        }
                        !agreeToTerms -> {
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar("Please agree to Terms and Conditions")
                            }
                        }
                        else -> {
                            // Registration successful
                            Toast.makeText(
                                context,
                                "Registration successful! Please login.",
                                Toast.LENGTH_LONG
                            ).show()

                            // Navigate back to login
                            val intent = Intent(context, LoginActivity::class.java)
                            context.startActivity(intent)
                            activity?.finish()
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("Register")
            }

            Spacer(modifier = Modifier.height(15.dp))

            // Login Link
            Text(
                text = "Already have an account? Login Now",
                modifier = Modifier.clickable {
                    val intent = Intent(context, LoginActivity::class.java)
                    context.startActivity(intent)
                    activity?.finish()
                },
                color = Color.Blue
            )

            Spacer(modifier = Modifier.height(15.dp))

            // Social Login Options
            Text(
                text = "Or register with",
                color = Color.Gray,
                fontSize = 14.sp
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 40.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(R.drawable.google),
                    contentDescription = null,
                    modifier = Modifier
                        .height(50.dp)
                        .width(50.dp)
                        .clickable {
                            Toast.makeText(context, "Google registration coming soon!", Toast.LENGTH_SHORT).show()
                        }
                )

                Spacer(modifier = Modifier.width(20.dp))

                Image(
                    painter = painterResource(R.drawable.fb),
                    contentDescription = null,
                    modifier = Modifier
                        .height(40.dp)
                        .width(50.dp)
                        .clickable {
                            Toast.makeText(context, "Facebook registration coming soon!", Toast.LENGTH_SHORT).show()
                        }
                )
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Preview
@Composable
fun RegistrationPreviewBody() {
    RegistrationBody()
}