package com.example.ecosajha.view

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import com.example.ecosajha.R

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                SplashAndNavigate()
            }
        }
    }
}

@Composable
fun SplashAndNavigate() {
    val context = LocalContext.current
    var navigate by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(2500L)
        navigate = true
    }

    if (!navigate) {
        SplashScreenUI()
    } else {
        // Always navigate to Login - update as needed for other logic
        val intent = Intent(context, LoginActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        }
        context.startActivity(intent)
        if (context is ComponentActivity) context.finish()
    }
}

@Composable
fun SplashScreenUI() {
    var startAnim by remember { mutableStateOf(false) }
    val logoScale by animateFloatAsState(
        targetValue = if (startAnim) 1f else 0.3f,
        animationSpec = tween(800, easing = FastOutSlowInEasing),
        label = "logo_scale"
    )
    val logoAlpha by animateFloatAsState(
        targetValue = if (startAnim) 1f else 0f,
        animationSpec = tween(800, delayMillis = 200, easing = FastOutSlowInEasing),
        label = "logo_alpha"
    )
    val titleAlpha by animateFloatAsState(
        targetValue = if (startAnim) 1f else 0f,
        animationSpec = tween(600, delayMillis = 600, easing = FastOutSlowInEasing),
        label = "title_alpha"
    )
    val subtitleAlpha by animateFloatAsState(
        targetValue = if (startAnim) 1f else 0f,
        animationSpec = tween(600, delayMillis = 800, easing = FastOutSlowInEasing),
        label = "subtitle_alpha"
    )
    val progressAlpha by animateFloatAsState(
        targetValue = if (startAnim) 1f else 0f,
        animationSpec = tween(400, delayMillis = 1000, easing = FastOutSlowInEasing),
        label = "progress_alpha"
    )

    LaunchedEffect(Unit) {
        startAnim = true
    }

    val gradientBrush = Brush.verticalGradient(
        listOf(
            Color(0xFF4CAF50), // Green
            Color(0xFF2E7D32), // Darker Green
            Color(0xFF1B5E20)  // Deep Green
        )
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(gradientBrush),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(32.dp)
        ) {
            Image(
                painter = painterResource(R.drawable.logo),
                contentDescription = "EcoSajha Logo",
                modifier = Modifier
                    .size(120.dp)
                    .scale(logoScale)
                    .alpha(logoAlpha)
            )
            Spacer(Modifier.height(24.dp))
            Text(
                text = "EcoSajha Recycle",
                fontSize = 42.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                textAlign = TextAlign.Center,
                modifier = Modifier.alpha(titleAlpha)
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = "Recycle • Reuse • Reduce",
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
                color = Color.White.copy(alpha = 0.9f),
                textAlign = TextAlign.Center,
                modifier = Modifier.alpha(titleAlpha)
            )
            Spacer(Modifier.height(40.dp))
            Text(
                text = "Welcome to EcoSajha",
                fontSize = 24.sp,
                fontWeight = FontWeight.Normal,
                color = Color.White.copy(alpha = 0.95f),
                textAlign = TextAlign.Center,
                modifier = Modifier.alpha(subtitleAlpha)
            )
            Spacer(Modifier.height(32.dp))
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.alpha(progressAlpha)
            ) {
                CircularProgressIndicator(
                    color = Color.White,
                    strokeWidth = 3.dp,
                    modifier = Modifier.size(32.dp)
                )
                Spacer(Modifier.height(16.dp))
                Text(
                    text = "Loading...",
                    fontSize = 14.sp,
                    color = Color.White.copy(alpha = 0.8f),
                    fontWeight = FontWeight.Light
                )
            }
        }
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            contentAlignment = Alignment.BottomCenter
        ) {
            Text(
                text = "v1.0.0 • Powered by Sustainability",
                fontSize = 12.sp,
                color = Color.White.copy(alpha = 0.6f),
                modifier = Modifier.alpha(progressAlpha)
            )
        }
    }
}
