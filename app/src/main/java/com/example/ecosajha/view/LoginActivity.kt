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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import com.example.ecosajha.R
import com.example.ecosajha.repository.UserRepositoryImpl
import com.example.inkspira_adigitalartportfolio.viewmodel.UserViewModel

class LoginActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LoginBody()
        }
    }
}

@Composable
fun LoginBody() {

    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisibility by remember { mutableStateOf(false) }
    var rememberMe by remember { mutableStateOf(false) }
    val repo = remember { UserRepositoryImpl() }
    val userViewModel = remember { UserViewModel(repo) }

    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope() // Fixed typo: was "couroutineScope"
    val snackbarHostState = remember { SnackbarHostState() }
    val activity = context as? Activity

    Scaffold (
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    )  { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(color = Color.White),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Welcome to Ecosajha Recycle")
            Text("helping to keep nature clean")
            Spacer(modifier = Modifier.height(50.dp))
0
            Image(
                painter = painterResource(R.drawable.loginimg),
                contentDescription = null,
                modifier = Modifier
                    .height(250.dp)
                    .width(250.dp)
            )

            Spacer(modifier = Modifier.height(15.dp))

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
                value = username,
                onValueChange = { input ->
                    username = input
                }
            )

            Spacer(modifier = Modifier.height(20.dp))

            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp),
                shape = RoundedCornerShape(12.dp),
                visualTransformation =
                    if (passwordVisibility) VisualTransformation.None
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
                    Text("*******")
                },
                value = password,
                onValueChange = { input ->
                    password = input
                }
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Checkbox(
                        checked = rememberMe,
                        onCheckedChange = { remember -> // Fixed typo: was "rememeber"
                            rememberMe = remember
                        },
                        colors = CheckboxDefaults.colors(
                            checkedColor = Color.Green,
                            checkmarkColor = Color.White
                        )
                    )
                    Text(text = "Remember me")
                }

                Text("Forget Password",
                    modifier = Modifier.clickable {
                        val intent = Intent(context, ResetPasswordActivity::class.java)
                        context.startActivity(intent)
                        // to destroy activity
                        activity?.finish()
                    })
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = {

                    userViewModel.login(username, password) {
                            success, message ->

                        if (success) {

                            Toast.makeText(context, message, Toast.LENGTH_LONG).show()
                            val intent = Intent(context, DashboardActivity:: class.java)
                            context.startActivity(intent)
                            activity?.finish()
                        } else {
//                                Log.d("checkkkk", message)

                            Toast.makeText(context, message, Toast.LENGTH_LONG).show()
                        }

                    }


                },
                shape = RoundedCornerShape(15.dp),
                modifier = Modifier
                    .height(50.dp)
                    .width(150.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Blue,
                    contentColor = Color.White
                )
            ) {
                Text(text = "Sign In")
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                "New user? Register Now",
                modifier = Modifier.clickable {
                    val intent = Intent(context, RegistrationActivity::class.java)
                    context.startActivity(intent)
                    // to destroy activity
                    activity?.finish()
                }
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
                )

                Spacer(modifier = Modifier.width(10.dp))

                Image(
                    painter = painterResource(R.drawable.fb),
                    contentDescription = null,
                    modifier = Modifier
                        .height(40.dp)
                        .width(50.dp)
                )
            }
        }
    }
}

@Preview
@Composable
fun LoginPreviewBody() {
    LoginBody()
}