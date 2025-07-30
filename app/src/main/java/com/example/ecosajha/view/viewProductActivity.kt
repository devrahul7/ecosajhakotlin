package com.example.ecosajha.view

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
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
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import kotlinx.coroutines.delay
import com.example.ecosajha.repository.ProductRepositoryImpl
import com.example.ecosajha.viewmodel.ProductViewModel

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

// Sample Product Data Class for Preview
data class SampleProduct(
    val productName: String,
    val price: Double,
    val description: String,
    val imageUrl: String? = null
)

class ViewProductActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MaterialTheme {
                ViewProductScreen()
            }
        }
    }
}

@Composable
fun FloatingRecycleIcon() {
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
        imageVector = Icons.Default.Recycling,
        contentDescription = "Floating Recycle",
        tint = VibrantGreen.copy(alpha = 0.7f),
        modifier = Modifier
            .size(24.dp) // Reduced from 32dp
            .rotate(rotation)
            .scale(scale)
    )
}

@Composable
fun PulsingPrice(price: Double) {
    val infiniteTransition = rememberInfiniteTransition()

    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.03f, // Reduced from 1.05f
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    val colorShift by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    val dynamicColor = androidx.compose.ui.graphics.lerp(VibrantGreen, AccentBlue, colorShift)

    Row(
        verticalAlignment = Alignment.Bottom,
        modifier = Modifier.scale(scale)
    ) {
        Text(
            text = "₹",
            fontSize = 20.sp, // Reduced from 24sp
            color = dynamicColor,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "$price",
            fontSize = 32.sp, // Reduced from 42sp
            fontWeight = FontWeight.Bold,
            color = dynamicColor
        )
        Text(
            text = " /kg",
            fontSize = 16.sp, // Reduced from 18sp
            color = Color.Gray,
            modifier = Modifier.padding(bottom = 4.dp)
        )
    }
}

@Composable
fun ViewProductScreen() {
    val context = LocalContext.current
    val activity = context as? Activity
    val productID: String? = activity?.intent?.getStringExtra("productID")

    if (productID.isNullOrEmpty()) {
        AnimatedErrorState { activity?.finish() }
        return
    }

    ViewProductBody(
        productID = productID,
        productViewModel = ProductViewModel(ProductRepositoryImpl())
    )
}

@Composable
fun AnimatedErrorState(onGoBack: () -> Unit) {
    var isVisible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(200)
        isVisible = true
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.radialGradient(
                    colors = listOf(
                        AccentPink.copy(alpha = 0.1f),
                        Color.Transparent
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        AnimatedVisibility(
            visible = isVisible,
            enter = slideInVertically(
                initialOffsetY = { it },
                animationSpec = tween(800, easing = FastOutSlowInEasing)
            ) + fadeIn(animationSpec = tween(800))
        ) {
            Card(
                colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                shape = RoundedCornerShape(16.dp), // Reduced from 24dp
                modifier = Modifier
                    .padding(16.dp) // Reduced from 24dp
                    .shadow(8.dp, RoundedCornerShape(16.dp)) // Reduced shadow
            ) {
                Column(
                    modifier = Modifier.padding(24.dp), // Reduced from 40dp
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "🔍",
                        fontSize = 48.sp // Reduced from 64sp
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Oops! Item Not Found",
                        style = MaterialTheme.typography.headlineSmall, // Reduced from headlineMedium
                        color = AccentPink,
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "The recyclable item you're looking for doesn't exist",
                        style = MaterialTheme.typography.bodyMedium, // Reduced from bodyLarge
                        color = Color.Gray,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Button(
                        onClick = onGoBack,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Transparent,
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .background(
                                brush = Brush.horizontalGradient(
                                    colors = listOf(AccentPink, AccentPurple)
                                ),
                                shape = RoundedCornerShape(12.dp)
                            )
                            .shadow(4.dp, RoundedCornerShape(12.dp)), // Reduced shadow
                        contentPadding = PaddingValues(horizontal = 24.dp, vertical = 12.dp) // Reduced padding
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = "Back",
                                modifier = Modifier.size(16.dp) // Reduced from 20dp
                            )
                            Text(
                                "Go Back",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp // Reduced from 16sp
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ViewProductBody(
    productID: String,
    productViewModel: ProductViewModel? = null,
    sampleProduct: SampleProduct? = null
) {
    var isLoading by remember { mutableStateOf(true) }
    var isContentVisible by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val activity = context as? Activity

    val product = productViewModel?.products?.observeAsState(initial = null)

    LaunchedEffect(productID) {
        if (productViewModel != null) {
            productViewModel.getProductByID(productID)
        } else {
            delay(1500)
            isLoading = false
            delay(300)
            isContentVisible = true
        }
    }

    LaunchedEffect(product?.value) {
        if (product?.value != null || sampleProduct != null) {
            isLoading = false
            delay(300)
            isContentVisible = true
        }
    }

    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp) // Reduced from 12dp
                    ) {
                        FloatingRecycleIcon()
                        AnimatedVisibility(
                            visible = !isLoading,
                            enter = slideInHorizontally(
                                initialOffsetX = { it },
                                animationSpec = tween(800)
                            )
                        ) {
                            Text(
                                "🌱 Item Details",
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                fontSize = 18.sp // Reduced from 20sp
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
                actions = {
                    AnimatedVisibility(
                        visible = !isLoading,
                        enter = scaleIn(
                            animationSpec = tween(500, delayMillis = 1000)
                        )
                    ) {
                        IconButton(
                            onClick = {
                                if (productViewModel != null) {
                                    val intent = Intent(context, UpdateProductActivity::class.java)
                                    intent.putExtra("productID", productID)
                                    context.startActivity(intent)
                                } else {
                                    Toast.makeText(context, "✏️ Edit feature", Toast.LENGTH_SHORT).show()
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Edit",
                                tint = Color.White
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                ),
                modifier = Modifier
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(VibrantGreen, DeepGreen, AccentBlue)
                        )
                    )
                    .shadow(8.dp) // Reduced from 12dp
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
            if (isLoading) {
                AnimatedLoadingState(modifier = Modifier.padding(padding))
            } else {
                val productData = product?.value ?: sampleProduct

                productData?.let { data ->
                    AnimatedVisibility(
                        visible = isContentVisible,
                        enter = fadeIn(
                            animationSpec = tween(1000, easing = FastOutSlowInEasing)
                        )
                    ) {
                        ProductContent(
                            data = data,
                            productID = productID,
                            productViewModel = productViewModel,
                            modifier = Modifier.padding(padding)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AnimatedLoadingState(modifier: Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition()

    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing)
        )
    )

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Card(
            colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
            shape = RoundedCornerShape(16.dp), // Reduced from 24dp
            modifier = Modifier.shadow(8.dp, RoundedCornerShape(16.dp)) // Reduced shadow
        ) {
            Column(
                modifier = Modifier.padding(32.dp), // Reduced from 48dp
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(60.dp) // Reduced from 80dp
                        .background(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    VibrantGreen.copy(alpha = 0.3f),
                                    Color.Transparent
                                )
                            ),
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Recycling,
                        contentDescription = "Loading",
                        tint = VibrantGreen,
                        modifier = Modifier
                            .size(28.dp) // Reduced from 40dp
                            .rotate(rotation)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "🔄 Loading details...",
                    style = MaterialTheme.typography.titleMedium, // Reduced from titleLarge
                    color = DeepGreen,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "Getting your recyclable item ready",
                    style = MaterialTheme.typography.bodySmall, // Reduced from bodyMedium
                    color = Color.Gray,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    repeat(3) { index ->
                        val scale by infiniteTransition.animateFloat(
                            initialValue = 0.5f,
                            targetValue = 1f,
                            animationSpec = infiniteRepeatable(
                                animation = tween(600),
                                repeatMode = RepeatMode.Reverse,
                                initialStartOffset = StartOffset(index * 200)
                            )
                        )

                        Box(
                            modifier = Modifier
                                .size(8.dp) // Reduced from 12dp
                                .scale(scale)
                                .background(
                                    brush = Brush.radialGradient(
                                        colors = listOf(VibrantGreen, AccentBlue)
                                    ),
                                    shape = CircleShape
                                )
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ProductContent(
    data: Any,
    productID: String,
    productViewModel: ProductViewModel?,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp), // Reduced from 24dp
        contentPadding = PaddingValues(16.dp) // Reduced from 20dp
    ) {
        item {
            AnimatedVisibility(
                visible = true,
                enter = slideInVertically(
                    initialOffsetY = { -it },
                    animationSpec = tween(800, delayMillis = 200)
                )
            ) {
                EnhancedImageCard(data = data)
            }
        }

        item {
            AnimatedVisibility(
                visible = true,
                enter = slideInHorizontally(
                    initialOffsetX = { -it },
                    animationSpec = tween(800, delayMillis = 400)
                )
            ) {
                ProductDetailsCard(data = data)
            }
        }

        item {
            AnimatedVisibility(
                visible = true,
                enter = slideInHorizontally(
                    initialOffsetX = { it },
                    animationSpec = tween(800, delayMillis = 600)
                )
            ) {
                InfoCard()
            }
        }

        item {
            AnimatedVisibility(
                visible = true,
                enter = slideInVertically(
                    initialOffsetY = { it },
                    animationSpec = tween(800, delayMillis = 800)
                )
            ) {
                EnvironmentalImpactCard()
            }
        }

        item {
            AnimatedVisibility(
                visible = true,
                enter = scaleIn(
                    animationSpec = tween(600, delayMillis = 1000)
                )
            ) {
                ActionButtonsRow(
                    productID = productID,
                    productViewModel = productViewModel,
                    context = context
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun EnhancedImageCard(data: Any) {
    val context = LocalContext.current
    val infiniteTransition = rememberInfiniteTransition()

    val borderAnimation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(8.dp, RoundedCornerShape(16.dp)), // Reduced shadow and corner radius
        colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp) // Reduced from 320dp
                .padding(16.dp) // Reduced from 20dp
                .clip(RoundedCornerShape(12.dp)) // Reduced from 20dp
        ) {
            val imageUrl = getImageUrl(data)

            if (!imageUrl.isNullOrEmpty()) {
                AsyncImage(
                    model = ImageRequest.Builder(context)
                        .data(imageUrl)
                        .crossfade(true)
                        .build(),
                    contentDescription = "Product Image",
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(12.dp)),
                    contentScale = ContentScale.Crop,
                    onError = {
                        Log.e("ViewProduct", "Error loading image: ${it.result.throwable.message}")
                    }
                )

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    Color.Black.copy(alpha = 0.3f)
                                )
                            ),
                            shape = RoundedCornerShape(12.dp)
                        )
                )
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    VibrantGreen.copy(alpha = 0.2f),
                                    AccentBlue.copy(alpha = 0.1f),
                                    Color.Transparent
                                )
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "♻️",
                            fontSize = 64.sp // Reduced from 80sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "📸 No Image Available",
                            color = DeepGreen,
                            fontSize = 16.sp, // Reduced from 20sp
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Image will appear here when uploaded",
                            color = Color.Gray,
                            fontSize = 12.sp, // Reduced from 14sp
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ProductDetailsCard(data: Any) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(8.dp, RoundedCornerShape(16.dp)), // Reduced shadow and corner radius
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
                modifier = Modifier.padding(20.dp), // Reduced from 28dp
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Product Name
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(text = "🏷️", fontSize = 24.sp) // Reduced from 32sp
                    Text(
                        text = getProductName(data),
                        fontSize = 24.sp, // Reduced from 32sp
                        fontWeight = FontWeight.Bold,
                        color = DeepGreen,
                        modifier = Modifier.weight(1f)
                    )
                }

                // Price Section
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(text = "💰", fontSize = 20.sp) // Reduced from 28sp
                    PulsingPrice(price = getProductPrice(data))
                }

                // Divider
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp) // Reduced from 2dp
                        .background(
                            brush = Brush.horizontalGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    VibrantGreen.copy(alpha = 0.3f),
                                    AccentBlue.copy(alpha = 0.3f),
                                    Color.Transparent
                                )
                            )
                        )
                )

                // Description
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(text = "📝", fontSize = 18.sp) // Reduced from 24sp
                        Text(
                            text = "Description",
                            fontSize = 18.sp, // Reduced from 22sp
                            fontWeight = FontWeight.Bold,
                            color = DeepGreen
                        )
                    }

                    Text(
                        text = getProductDescription(data),
                        fontSize = 14.sp, // Reduced from 16sp
                        color = Color.Gray,
                        lineHeight = 20.sp, // Reduced from 26sp
                        modifier = Modifier.padding(start = 26.dp) // Reduced from 36dp
                    )
                }
            }
        }
    }
}

@Composable
fun InfoCard() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(6.dp, RoundedCornerShape(16.dp)), // Reduced shadow
        colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp), // Reduced from 24dp
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            AnimatedInfoItem(
                icon = "🏷️",
                title = "Category",
                value = "Recyclable",
                color = AccentBlue,
                delay = 0
            )
            AnimatedInfoItem(
                icon = "📅",
                title = "Added",
                value = "Today",
                color = AccentPurple,
                delay = 200
            )
            AnimatedInfoItem(
                icon = "📊",
                title = "Status",
                value = "Active",
                color = VibrantGreen,
                delay = 400
            )
        }
    }
}

@Composable
fun AnimatedInfoItem(
    icon: String,
    title: String,
    value: String,
    color: Color,
    delay: Int
) {
    var isVisible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(delay.toLong())
        isVisible = true
    }

    AnimatedVisibility(
        visible = isVisible,
        enter = scaleIn(
            animationSpec = tween(500, easing = FastOutSlowInEasing)
        ) + fadeIn()
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp) // Reduced from 8dp
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp) // Reduced from 48dp
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                color.copy(alpha = 0.2f),
                                Color.Transparent
                            )
                        ),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = icon,
                    fontSize = 20.sp // Reduced from 28sp
                )
            }

            Text(
                text = title,
                fontSize = 12.sp, // Reduced from 14sp
                color = Color.Gray,
                fontWeight = FontWeight.Medium
            )

            Text(
                text = value,
                fontSize = 14.sp, // Reduced from 16sp
                color = color,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun EnvironmentalImpactCard() {
    val infiniteTransition = rememberInfiniteTransition()

    val backgroundShift by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(10000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(8.dp, RoundedCornerShape(16.dp)), // Reduced shadow and corner radius
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            VibrantGreen,
                            AccentBlue,
                            AccentPurple
                        ),
                        start = androidx.compose.ui.geometry.Offset(
                            backgroundShift * 500f,
                            0f
                        ),
                        end = androidx.compose.ui.geometry.Offset(
                            1000f + backgroundShift * 500f,
                            1000f
                        )
                    )
                )
        ) {
            Column(
                modifier = Modifier.padding(20.dp), // Reduced from 32dp
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "🌍✨",
                    fontSize = 40.sp // Reduced from 56sp
                )

                Text(
                    text = "Environmental Impact",
                    fontSize = 20.sp, // Reduced from 24sp
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    textAlign = TextAlign.Center
                )

                Text(
                    text = "♻️ By recycling this item, you're contributing to a cleaner planet! 🌱",
                    fontSize = 14.sp, // Reduced from 16sp
                    color = Color.White.copy(alpha = 0.95f),
                    textAlign = TextAlign.Center,
                    lineHeight = 20.sp // Reduced from 24sp
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "🌿", fontSize = 18.sp) // Reduced from 24sp
                    Text(text = "💚", fontSize = 18.sp)
                    Text(text = "🌍", fontSize = 18.sp)
                    Text(text = "✨", fontSize = 18.sp)
                }
            }
        }
    }
}

@Composable
fun ActionButtonsRow(
    productID: String,
    productViewModel: ProductViewModel?,
    context: android.content.Context
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Edit Button
        AnimatedActionButton(
            onClick = {
                if (productViewModel != null) {
                    val intent = Intent(context, UpdateProductActivity::class.java)
                    intent.putExtra("productID", productID)
                    context.startActivity(intent)
                } else {
                    Toast.makeText(context, "✏️ Edit feature", Toast.LENGTH_SHORT).show()
                }
            },
            icon = Icons.Default.Edit,
            text = "Edit Item",
            gradient = listOf(VibrantGreen, AccentBlue),
            modifier = Modifier.weight(1f)
        )

        // Share Button
        AnimatedActionButton(
            onClick = {
                Toast.makeText(
                    context,
                    "📤 Share feature coming soon!",
                    Toast.LENGTH_SHORT
                ).show()
            },
            icon = Icons.Default.Share,
            text = "Share",
            gradient = listOf(AccentPurple, AccentPink),
            modifier = Modifier.weight(1f),
            isOutlined = true
        )
    }
}

@Composable
fun AnimatedActionButton(
    onClick: () -> Unit,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    text: String,
    gradient: List<Color>,
    modifier: Modifier = Modifier,
    isOutlined: Boolean = false
) {
    var isPressed by remember { mutableStateOf(false) }

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)
    )

    if (isOutlined) {
        OutlinedButton(
            onClick = {
                isPressed = true
                onClick()
            },
            modifier = modifier
                .height(56.dp) // Reduced from 64dp
                .scale(scale)
                .shadow(4.dp, RoundedCornerShape(16.dp)), // Reduced shadow
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = gradient[0]
            ),
            shape = RoundedCornerShape(16.dp),
            border = androidx.compose.foundation.BorderStroke(
                2.dp,
                brush = Brush.horizontalGradient(gradient)
            )
        ) {
            LaunchedEffect(isPressed) {
                if (isPressed) {
                    delay(150)
                    isPressed = false
                }
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = text,
                    modifier = Modifier.size(16.dp) // Reduced from 20dp
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text,
                    fontSize = 14.sp, // Reduced from 16sp
                    fontWeight = FontWeight.Bold
                )
            }
        }
    } else {
        Button(
            onClick = {
                isPressed = true
                onClick()
            },
            modifier = modifier
                .height(56.dp) // Reduced from 64dp
                .scale(scale)
                .shadow(8.dp, RoundedCornerShape(16.dp)), // Reduced shadow
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Transparent,
                contentColor = Color.White
            ),
            shape = RoundedCornerShape(16.dp),
            contentPadding = PaddingValues(0.dp)
        ) {
            LaunchedEffect(isPressed) {
                if (isPressed) {
                    delay(150)
                    isPressed = false
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = Brush.horizontalGradient(gradient)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = text,
                        modifier = Modifier.size(16.dp) // Reduced from 20dp
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text,
                        fontSize = 14.sp, // Reduced from 16sp
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

// Helper functions remain the same
fun getProductName(product: Any): String {
    return when (product) {
        is SampleProduct -> product.productName
        else -> {
            try {
                val field = product.javaClass.getDeclaredField("productName")
                field.isAccessible = true
                field.get(product) as? String ?: "🏷️ Unknown Item"
            } catch (e: Exception) {
                "🏷️ Unknown Item"
            }
        }
    }
}

fun getProductPrice(product: Any): Double {
    return when (product) {
        is SampleProduct -> product.price
        else -> {
            try {
                val field = product.javaClass.getDeclaredField("price")
                field.isAccessible = true
                field.get(product) as? Double ?: 0.0
            } catch (e: Exception) {
                0.0
            }
        }
    }
}

fun getProductDescription(product: Any): String {
    return when (product) {
        is SampleProduct -> product.description
        else -> {
            try {
                val field = product.javaClass.getDeclaredField("description")
                field.isAccessible = true
                field.get(product) as? String ?: "📝 No description available for this recyclable item."
            } catch (e: Exception) {
                "📝 No description available for this recyclable item."
            }
        }
    }
}

fun getImageUrl(product: Any): String? {
    return when (product) {
        is SampleProduct -> product.imageUrl
        else -> {
            try {
                val fields = product.javaClass.declaredFields
                for (field in fields) {
                    field.isAccessible = true
                    when (field.name.lowercase()) {
                        "imageurl", "image_url", "image", "imagelink", "imgurl" -> {
                            val value = field.get(product) as? String
                            if (!value.isNullOrEmpty()) {
                                Log.d("ViewProduct", "Found image URL: $value")
                                return value
                            }
                        }
                    }
                }
                Log.d("ViewProduct", "Available fields: ${fields.map { it.name }}")
                null
            } catch (e: Exception) {
                Log.e("ViewProduct", "Error getting image URL: ${e.message}")
                null
            }
        }
    }
}

// Preview Functions
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ViewProductScreenPreview() {
    MaterialTheme {
        ViewProductBody(
            productID = "sample_product_id",
            sampleProduct = SampleProduct(
                productName = "Eco-Friendly Plastic Bottles",
                price = 25.50,
                description = "♻️ Clean plastic bottles perfect for recycling! These bottles help reduce environmental waste and can be processed into amazing new products. Great for eco-conscious consumers looking to make a positive impact on our beautiful planet! 🌍✨"
            )
        )
    }
}

@Preview(showBackground = true, showSystemUi = true, name = "Loading State")
@Composable
fun ViewProductLoadingPreview() {
    MaterialTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(BackgroundGradient)
                )
        ) {
            AnimatedLoadingState()
        }
    }
}

@Preview(showBackground = true, showSystemUi = true, name = "Error State")
@Composable
fun ViewProductErrorPreview() {
    MaterialTheme {
        AnimatedErrorState {}
    }
}
