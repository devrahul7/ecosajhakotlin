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
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import com.example.ecosajha.repository.ProductRepositoryImpl
import com.example.ecosajha.viewmodel.ProductViewModel

// Modern vibrant color palette - MATCHING AddProduct & ViewProduct
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

class UpdateProductActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MaterialTheme {
                UpdateProductScreen()
            }
        }
    }
}

@Composable
fun FloatingUpdateIcon() {
    val infiniteTransition = rememberInfiniteTransition(label = "floating_icon")

    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(10000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )

    val scale by infiniteTransition.animateFloat(
        initialValue = 0.9f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    Icon(
        imageVector = Icons.Default.Edit,
        contentDescription = "Update",
        tint = VibrantGreen.copy(alpha = 0.7f),
        modifier = Modifier
            .size(24.dp)
            .rotate(rotation)
            .scale(scale)
    )
}

@Composable
fun UpdateProductScreen() {
    val context = LocalContext.current
    val activity = context as? Activity
    val productID: String? = activity?.intent?.getStringExtra("productID")

    if (productID.isNullOrEmpty()) {
        ModernErrorState { activity?.finish() }
        return
    }

    UpdateProductBody(productID = productID)
}

@Composable
fun ModernErrorState(onGoBack: () -> Unit) {
    var isVisible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(300)
        isVisible = true
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.radialGradient(
                    colors = listOf(
                        AccentPink.copy(alpha = 0.15f),
                        AccentPurple.copy(alpha = 0.05f),
                        Color.Transparent
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        AnimatedVisibility(
            visible = isVisible,
            enter = scaleIn(
                animationSpec = tween(800, easing = FastOutSlowInEasing)
            ) + fadeIn()
        ) {
            Card(
                modifier = Modifier
                    .padding(24.dp)
                    .shadow(16.dp, RoundedCornerShape(20.dp)),
                colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                shape = RoundedCornerShape(20.dp)
            ) {
                Column(
                    modifier = Modifier.padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("🔍", fontSize = 48.sp)

                    Spacer(modifier = Modifier.height(20.dp))

                    Text(
                        text = "Oops! Item Not Found",
                        style = MaterialTheme.typography.headlineSmall,
                        color = AccentPink,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = "The recyclable item ID is missing or invalid",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(top = 8.dp)
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = onGoBack,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Transparent
                        ),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .background(
                                brush = Brush.horizontalGradient(
                                    colors = listOf(AccentPink, AccentPurple)
                                ),
                                shape = RoundedCornerShape(16.dp)
                            )
                            .shadow(8.dp, RoundedCornerShape(16.dp)),
                        contentPadding = PaddingValues(horizontal = 24.dp, vertical = 12.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(Icons.Default.ArrowBack, contentDescription = null, modifier = Modifier.size(18.dp))
                            Text("Go Back", fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UpdateProductBody(productID: String) {
    var recyclableItemName by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(true) }
    var isUpdating by remember { mutableStateOf(false) }
    var isContentVisible by remember { mutableStateOf(false) }

    val repo = remember { ProductRepositoryImpl() }
    val viewModel = remember { ProductViewModel(repo) }
    val context = LocalContext.current
    val activity = context as? Activity

    val product = viewModel.products.observeAsState(initial = null)

    LaunchedEffect(productID) {
        viewModel.getProductByID(productID)
    }

    LaunchedEffect(product.value) {
        product.value?.let { productData ->
            recyclableItemName = productData.productName ?: ""
            description = productData.description ?: ""
            price = productData.price?.toString() ?: ""
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
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        FloatingUpdateIcon()
                        AnimatedVisibility(
                            visible = !isLoading,
                            enter = slideInHorizontally(
                                initialOffsetX = { it },
                                animationSpec = tween(800)
                            )
                        ) {
                            Text(
                                "✏️ Update Item",
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                fontSize = 18.sp
                            )
                        }
                    }
                },
                navigationIcon = {  // 👈 BACK ARROW ADDED
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
                            colors = listOf(VibrantGreen, DeepGreen, AccentBlue)
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
            if (isLoading) {
                ModernLoadingState(modifier = Modifier.padding(padding))
            } else {
                AnimatedVisibility(
                    visible = isContentVisible,
                    enter = fadeIn(tween(1000))
                ) {
                    LazyColumn(
                        modifier = Modifier
                            .padding(padding)
                            .fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Header Card
                        item {
                            ModernHeaderCard()
                        }

                        // Form Card
                        item {
                            ModernFormCard(
                                recyclableItemName = recyclableItemName,
                                price = price,
                                description = description,
                                isUpdating = isUpdating,
                                onNameChange = { recyclableItemName = it },
                                onPriceChange = { price = it },
                                onDescriptionChange = { description = it }
                            )
                        }

                        // Update Button
                        item {
                            ModernUpdateButton(
                                isUpdating = isUpdating,
                                onClick = {
                                    when {
                                        recyclableItemName.isBlank() -> {
                                            Toast.makeText(context, "Please enter recyclable item name", Toast.LENGTH_SHORT).show()
                                        }
                                        price.isBlank() -> {
                                            Toast.makeText(context, "Please enter price", Toast.LENGTH_SHORT).show()
                                        }
                                        description.isBlank() -> {
                                            Toast.makeText(context, "Please enter description", Toast.LENGTH_SHORT).show()
                                        }
                                        else -> {
                                            try {
                                                val priceValue = price.toDouble()
                                                if (priceValue <= 0) {
                                                    Toast.makeText(context, "Price must be greater than 0", Toast.LENGTH_SHORT).show()
                                                    return@ModernUpdateButton
                                                }

                                                isUpdating = true

                                                val updateData = mutableMapOf<String, Any?>().apply {
                                                    put("description", description)
                                                    put("price", priceValue)
                                                    put("productName", recyclableItemName)
                                                    put("productID", productID)
                                                }

                                                viewModel.updateProduct(productID, updateData) { success, message ->
                                                    isUpdating = false
                                                    if (success) {
                                                        Toast.makeText(context, "✅ Recyclable item updated successfully!", Toast.LENGTH_SHORT).show()
                                                        activity?.finish()
                                                    } else {
                                                        Toast.makeText(context, "❌ ${message ?: "Failed to update recyclable item"}", Toast.LENGTH_SHORT).show()
                                                    }
                                                }
                                            } catch (e: NumberFormatException) {
                                                Toast.makeText(context, "Please enter a valid price", Toast.LENGTH_SHORT).show()
                                            }
                                        }
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ModernLoadingState(modifier: Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition(label = "loading")

    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(tween(2000, easing = LinearEasing)),
        label = "rotation"
    )

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier.shadow(8.dp, RoundedCornerShape(16.dp)),
            colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .background(
                            brush = Brush.radialGradient(
                                colors = listOf(VibrantGreen.copy(0.3f), Color.Transparent)
                            ),
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Edit,
                        contentDescription = null,
                        tint = VibrantGreen,
                        modifier = Modifier
                            .size(28.dp)
                            .rotate(rotation)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    "🔄 Loading details...",
                    style = MaterialTheme.typography.titleMedium,
                    color = DeepGreen,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    "Getting recyclable item ready for editing",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
fun ModernHeaderCard() {
    val infiniteTransition = rememberInfiniteTransition(label = "header")

    val backgroundShift by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            tween(8000, easing = LinearEasing),
            RepeatMode.Reverse
        ),
        label = "background_shift"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(8.dp, RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        shape = RoundedCornerShape(16.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            VibrantGreen.copy(alpha = 0.9f),
                            AccentBlue.copy(alpha = 0.8f),
                            AccentPurple.copy(alpha = 0.7f)
                        ),
                        start = androidx.compose.ui.geometry.Offset(
                            0f + backgroundShift * 200f,
                            0f
                        ),
                        end = androidx.compose.ui.geometry.Offset(
                            1000f + backgroundShift * 200f,
                            1000f
                        )
                    )
                )
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Edit, null, tint = Color.White, modifier = Modifier.size(36.dp))
                    Icon(Icons.Default.Favorite, null, tint = AccentPink, modifier = Modifier.size(24.dp))
                    Icon(Icons.Default.Star, null, tint = AccentYellow, modifier = Modifier.size(28.dp))
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    "🔄 Update Item Details",
                    style = MaterialTheme.typography.titleLarge,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    "Modify your recyclable item information ♻️",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(0.9f),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
fun ModernFormCard(
    recyclableItemName: String,
    price: String,
    description: String,
    isUpdating: Boolean,
    onNameChange: (String) -> Unit,
    onPriceChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(8.dp, RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(text = "📝", fontSize = 24.sp)
                Text(
                    "Edit Item Information",
                    style = MaterialTheme.typography.titleMedium,
                    color = DeepGreen,
                    fontWeight = FontWeight.Bold
                )
            }

            // Item Name Field
            OutlinedTextField(
                value = recyclableItemName,
                onValueChange = onNameChange,
                label = { Text("♻️ Recyclable Item Name", fontSize = 14.sp) },
                placeholder = { Text("e.g., Plastic Bottles, Paper, Metal Cans", fontSize = 14.sp) },
                leadingIcon = { Icon(Icons.Default.Eco, null, tint = VibrantGreen) },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isUpdating,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = VibrantGreen,
                    focusedLabelColor = VibrantGreen,
                    cursorColor = VibrantGreen,
                    focusedLeadingIconColor = VibrantGreen
                ),
                shape = RoundedCornerShape(12.dp)
            )

            // Price Field
            OutlinedTextField(
                value = price,
                onValueChange = onPriceChange,
                label = { Text("💰 Price (₹)", fontSize = 14.sp) },
                placeholder = { Text("Enter price per unit/kg", fontSize = 14.sp) },
                leadingIcon = { Icon(Icons.Default.CurrencyRupee, null, tint = AccentOrange) },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isUpdating,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = AccentOrange,
                    focusedLabelColor = AccentOrange,
                    cursorColor = AccentOrange,
                    focusedLeadingIconColor = AccentOrange
                ),
                shape = RoundedCornerShape(12.dp)
            )

            // Description Field
            OutlinedTextField(
                value = description,
                onValueChange = onDescriptionChange,
                label = { Text("📋 Description", fontSize = 14.sp) },
                placeholder = { Text("Describe condition, quantity, pickup details...", fontSize = 14.sp) },
                leadingIcon = { Icon(Icons.Default.Info, null, tint = AccentPurple) },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isUpdating,
                minLines = 3,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = AccentPurple,
                    focusedLabelColor = AccentPurple,
                    cursorColor = AccentPurple,
                    focusedLeadingIconColor = AccentPurple
                ),
                shape = RoundedCornerShape(12.dp)
            )
        }
    }
}

@Composable
fun ModernUpdateButton(
    isUpdating: Boolean,
    onClick: () -> Unit
) {
    var isPressed by remember { mutableStateOf(false) }
    val infiniteTransition = rememberInfiniteTransition(label = "button")

    val gradientShift by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            tween(3000, easing = LinearEasing),
            RepeatMode.Reverse
        ),
        label = "gradient"
    )

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        animationSpec = spring(Spring.DampingRatioMediumBouncy),
        label = "scale"
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
        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
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
                                AccentPurple,
                                AccentOrange
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
                            Icons.Default.Edit,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp),
                            tint = Color.White
                        )
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                Text(
                    if (isUpdating) "🔄 Updating..." else "🚀 Update Recyclable Item",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun UpdateProductPreview() {
    MaterialTheme {
        UpdateProductBody(productID = "sample_id")
    }
}
