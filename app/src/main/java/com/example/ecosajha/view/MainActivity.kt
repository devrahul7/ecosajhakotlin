package com.example.ecosajha.view

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import com.example.ecosajha.R
import kotlin.math.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            EcoSajhaTheme {
                SplashAndNavigate()
            }
        }
    }
}

@Composable
fun MainActivity(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = lightColorScheme(
            primary = Color(0xFF4CAF50),
            secondary = Color(0xFF66BB6A),
            background = Color(0xFF1B5E20)
        ),
        content = content
    )
}

@Composable
fun SplashAndNavigate() {
    val context = LocalContext.current
    var navigate by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(3500L)
        navigate = true
    }

    if (!navigate) {
        RecyclingAppSplashScreen()
    } else {
        // Navigate to LoginActivity
        LaunchedEffect(navigate) {
            val intent = Intent(context, LoginActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            }
            context.startActivity(intent)
            if (context is ComponentActivity) context.finish()
        }
    }
}

@Composable
fun RecyclingAppSplashScreen() {
    var startAnimations by remember { mutableStateOf(false) }

    // Animation States
    val logoScale by animateFloatAsState(
        targetValue = if (startAnimations) 1f else 0.2f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMediumLow
        ),
        label = "logo_scale"
    )

    val logoAlpha by animateFloatAsState(
        targetValue = if (startAnimations) 1f else 0f,
        animationSpec = tween(1200, delayMillis = 300, easing = FastOutSlowInEasing),
        label = "logo_alpha"
    )

    val titleSlideY by animateFloatAsState(
        targetValue = if (startAnimations) 0f else 100f,
        animationSpec = tween(1000, delayMillis = 700, easing = FastOutSlowInEasing),
        label = "title_slide"
    )

    val titleAlpha by animateFloatAsState(
        targetValue = if (startAnimations) 1f else 0f,
        animationSpec = tween(800, delayMillis = 700, easing = FastOutSlowInEasing),
        label = "title_alpha"
    )

    val subtitleAlpha by animateFloatAsState(
        targetValue = if (startAnimations) 1f else 0f,
        animationSpec = tween(600, delayMillis = 1100, easing = FastOutSlowInEasing),
        label = "subtitle_alpha"
    )

    val taglineAlpha by animateFloatAsState(
        targetValue = if (startAnimations) 1f else 0f,
        animationSpec = tween(600, delayMillis = 1500, easing = FastOutSlowInEasing),
        label = "tagline_alpha"
    )

    val loadingAlpha by animateFloatAsState(
        targetValue = if (startAnimations) 1f else 0f,
        animationSpec = tween(400, delayMillis = 1900, easing = FastOutSlowInEasing),
        label = "loading_alpha"
    )

    LaunchedEffect(Unit) {
        startAnimations = true
    }

    // Premium Eco-Friendly Gradient
    val ecoGradientBrush = Brush.radialGradient(
        colors = listOf(
            Color(0xFF81C784), // Light Green
            Color(0xFF66BB6A), // Medium Light Green
            Color(0xFF4CAF50), // Primary Green
            Color(0xFF388E3C), // Medium Green
            Color(0xFF2E7D32), // Dark Green
            Color(0xFF1B5E20), // Deep Forest Green
            Color(0xFF0D4715)  // Very Deep Green
        ),
        radius = 1500f
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(ecoGradientBrush),
        contentAlignment = Alignment.Center
    ) {
        // Simple background decoration
        RecyclingBackgroundElements(alpha = loadingAlpha * 0.4f)

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
        ) {
            Spacer(modifier = Modifier.weight(0.3f))

            // Enhanced Logo Section
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(160.dp)
                    .scale(logoScale)
                    .alpha(logoAlpha)
            ) {
                // Glow Effect
                Box(
                    modifier = Modifier
                        .size(200.dp)
                        .background(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    Color.White.copy(alpha = 0.4f),
                                    Color(0xFF4CAF50).copy(alpha = 0.3f),
                                    Color.Transparent
                                )
                            ),
                            shape = CircleShape
                        )
                        .blur(20.dp)
                )

                Box(
                    modifier = Modifier
                        .size(160.dp)
                        .background(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    Color.White.copy(alpha = 0.2f),
                                    Color.Transparent
                                )
                            ),
                            shape = CircleShape
                        )
                )

                Image(
                    painter = painterResource(R.drawable.logo),
                    contentDescription = "EcoSajha Recycling Logo",
                    modifier = Modifier.size(130.dp)
                )
            }

            Spacer(modifier = Modifier.height(40.dp))

            // Main App Title
            Text(
                text = "EcoSajha",
                fontSize = 52.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White,
                textAlign = TextAlign.Center,
                letterSpacing = 2.sp,
                modifier = Modifier
                    .alpha(titleAlpha)
                    .offset(y = titleSlideY.dp)
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Recycle",
                fontSize = 28.sp,
                fontWeight = FontWeight.Light,
                color = Color.White.copy(alpha = 0.95f),
                textAlign = TextAlign.Center,
                letterSpacing = 8.sp,
                modifier = Modifier
                    .alpha(titleAlpha)
                    .offset(y = titleSlideY.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Recycling Actions
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.alpha(subtitleAlpha)
            ) {
                RecyclingActionChip("♻️", "RECYCLE")
                Spacer(modifier = Modifier.width(8.dp))
                Text("•", color = Color.White.copy(alpha = 0.7f), fontSize = 16.sp)
                Spacer(modifier = Modifier.width(8.dp))
                RecyclingActionChip("🔄", "REUSE")
                Spacer(modifier = Modifier.width(8.dp))
                Text("•", color = Color.White.copy(alpha = 0.7f), fontSize = 16.sp)
                Spacer(modifier = Modifier.width(8.dp))
                RecyclingActionChip("⬇️", "REDUCE")
            }

            Spacer(modifier = Modifier.height(48.dp))

            // Tagline
            Text(
                text = "Building a Sustainable Future",
                fontSize = 20.sp,
                fontWeight = FontWeight.Medium,
                color = Color.White.copy(alpha = 0.9f),
                textAlign = TextAlign.Center,
                letterSpacing = 1.sp,
                modifier = Modifier.alpha(taglineAlpha)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "One Step at a Time 🌱",
                fontSize = 16.sp,
                fontWeight = FontWeight.Normal,
                color = Color.White.copy(alpha = 0.8f),
                textAlign = TextAlign.Center,
                modifier = Modifier.alpha(taglineAlpha)
            )

            Spacer(modifier = Modifier.weight(0.4f))

            // Loading Section
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.alpha(loadingAlpha)
            ) {
                EcoLoadingIndicator()

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "Preparing your eco-journey...",
                    fontSize = 16.sp,
                    color = Color.White.copy(alpha = 0.85f),
                    fontWeight = FontWeight.Normal,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.weight(0.3f))
        }

        // Bottom Branding
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            contentAlignment = Alignment.BottomCenter
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.alpha(loadingAlpha)
            ) {
                Text(
                    text = "🌍 Powered by Sustainability • Made with 💚",
                    fontSize = 13.sp,
                    color = Color.White.copy(alpha = 0.7f),
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Version 1.0.0 • EcoSajha Team",
                    fontSize = 11.sp,
                    color = Color.White.copy(alpha = 0.6f),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
fun RecyclingActionChip(emoji: String, text: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier
            .background(
                Color.White.copy(alpha = 0.15f),
                shape = CircleShape
            )
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Text(
            text = emoji,
            fontSize = 14.sp
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = text,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color.White.copy(alpha = 0.9f)
        )
    }
}

@Composable
fun EcoLoadingIndicator() {
    val infiniteTransition = rememberInfiniteTransition(label = "eco_loading")

    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )

    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.9f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.size(70.dp)
    ) {
        // Outer Ring
        Canvas(
            modifier = Modifier
                .size(70.dp)
                .rotate(rotation)
                .scale(pulseScale)
        ) {
            val center = Offset(size.width / 2, size.height / 2)
            val radius = size.minDimension / 2 - 10.dp.toPx()

            val ecoColors = listOf(
                Color(0xFF4CAF50), // Green
                Color(0xFF66BB6A), // Light Green
                Color(0xFF81C784), // Lighter Green
                Color.White,       // White
                Color(0xFF4CAF50), // Green again
            )

            ecoColors.forEachIndexed { index, color ->
                val startAngle = index * 72f
                val sweepAngle = 50f

                drawArc(
                    color = color,
                    startAngle = startAngle,
                    sweepAngle = sweepAngle,
                    useCenter = false,
                    style = Stroke(
                        width = 5.dp.toPx(),
                        cap = StrokeCap.Round
                    ),
                    topLeft = Offset(center.x - radius, center.y - radius),
                    size = androidx.compose.ui.geometry.Size(radius * 2, radius * 2)
                )
            }
        }

        // Center Symbol
        val centerPulse by infiniteTransition.animateFloat(
            initialValue = 0.7f,
            targetValue = 1.0f,
            animationSpec = infiniteRepeatable(
                animation = tween(1000, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "center_pulse"
        )

        Text(
            text = "♻️",
            fontSize = 20.sp,
            modifier = Modifier.scale(centerPulse)
        )
    }
}

@Composable
fun RecyclingBackgroundElements(alpha: Float) {
    // Simplified background with static decorative elements
    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .alpha(alpha)
    ) {
        val centerX = size.width / 2
        val centerY = size.height / 2

        // Draw simple decorative circles
        drawCircle(
            color = Color.White.copy(alpha = 0.1f),
            radius = 20f,
            center = Offset(centerX - 200, centerY - 300)
        )
        drawCircle(
            color = Color.White.copy(alpha = 0.08f),
            radius = 15f,
            center = Offset(centerX + 250, centerY - 200)
        )
        drawCircle(
            color = Color.White.copy(alpha = 0.12f),
            radius = 25f,
            center = Offset(centerX - 150, centerY + 250)
        )
        drawCircle(
            color = Color.White.copy(alpha = 0.06f),
            radius = 18f,
            center = Offset(centerX + 180, centerY + 300)
        )
    }
}

// Compose Previews
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun RecyclingAppSplashScreenPreview() {
    EcoSajhaTheme {
        RecyclingAppSplashScreen()
    }
}

@Preview(showBackground = true, name = "Logo Section")
@Composable
fun LogoSectionPreview() {
    EcoSajhaTheme {
        Box(
            modifier = Modifier
                .size(200.dp)
                .background(Color(0xFF2E7D32)),
            contentAlignment = Alignment.Center
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(160.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(200.dp)
                        .background(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    Color.White.copy(alpha = 0.4f),
                                    Color(0xFF4CAF50).copy(alpha = 0.3f),
                                    Color.Transparent
                                )
                            ),
                            shape = CircleShape
                        )
                )

                // Placeholder for logo
                Box(
                    modifier = Modifier
                        .size(130.dp)
                        .background(Color.White.copy(alpha = 0.9f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text("LOGO", fontWeight = FontWeight.Bold, color = Color(0xFF2E7D32))
                }
            }
        }
    }
}

@Preview(showBackground = true, name = "Loading Indicator")
@Composable
fun EcoLoadingIndicatorPreview() {
    EcoSajhaTheme {
        Box(
            modifier = Modifier
                .size(100.dp)
                .background(Color(0xFF2E7D32)),
            contentAlignment = Alignment.Center
        ) {
            EcoLoadingIndicator()
        }
    }
}

@Preview(showBackground = true, name = "Action Chips")
@Composable
fun RecyclingActionChipsPreview() {
    EcoSajhaTheme {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF2E7D32))
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                RecyclingActionChip("♻️", "RECYCLE")
                Spacer(modifier = Modifier.width(8.dp))
                Text("•", color = Color.White.copy(alpha = 0.7f), fontSize = 16.sp)
                Spacer(modifier = Modifier.width(8.dp))
                RecyclingActionChip("🔄", "REUSE")
                Spacer(modifier = Modifier.width(8.dp))
                Text("•", color = Color.White.copy(alpha = 0.7f), fontSize = 16.sp)
                Spacer(modifier = Modifier.width(8.dp))
                RecyclingActionChip("⬇️", "REDUCE")
            }
        }
    }
}
