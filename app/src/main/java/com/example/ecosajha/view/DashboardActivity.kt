package com.example.ecosajha.view


import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
//import com.example.ecosajha.R

class DashboardActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DashboardB()
        }

    }
}


@Composable
fun DashboardB() {
    val context = LocalContext.current
    val activity = context as Activity
    val email: String? = activity.intent.getStringExtra("email")
    Scaffold()
    {
            padding ->
        Column(modifier = Modifier.padding(padding)){
            Text("Good Morning, $email")
        }

    }
}


@Preview
@Composable
fun Preview () {
    DashboardB()
}