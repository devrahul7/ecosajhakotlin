package com.example.ecosajha.view

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.CurrencyRupee
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.ArrowBack
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import kotlinx.coroutines.delay
import com.example.ecosajha.R
import com.example.ecosajha.model.ProductModel
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
private val BackgroundGradient = listOf(Color(0xFFF0FFF0), Color(0xFFE8F5E8))
private val SurfaceWhite = Color(0xFFFFFFFF)

class AddProductActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MaterialTheme {
                AddProductScreen()
            }
        }
    }
}

@Composable
fun AddProductScreen() {
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        selectedImageUri = uri
    }

    AddProductBody(
        selectedImageUri = selectedImageUri,
        onPickImage = {
            imagePickerLauncher.launch("image/*")
        }
    )
}

@Composable
fun FloatingEcoIcon() {
    val infiniteTransition = rememberInfiniteTransition(label = "floating_eco")

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
        initialValue = 1f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    Icon(
        imageVector = Icons.Default.Eco,
        contentDescription = "Floating Eco",
        tint = VibrantGreen.copy(alpha = 0.6f),
        modifier = Modifier
            .size(24.dp)
            .rotate(rotation)
            .scale(scale)
    )
}

@Composable
fun PulsingLoadingDots() {
    val infiniteTransition = rememberInfiniteTransition(label = "loading_dots")

    Row(
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(3) { index ->
            val scale by infiniteTransition.animateFloat(
                initialValue = 0.5f,
                targetValue = 1f,
                animationSpec = infiniteRepeatable(
                    animation = tween(600),
                    repeatMode = RepeatMode.Reverse,
                    initialStartOffset = StartOffset(index * 200)
                ),
                label = "dot_scale_$index"
            )

            Box(
                modifier = Modifier
                    .size(8.dp)
                    .scale(scale)
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(VibrantGreen, DeepGreen)
                        ),
                        shape = CircleShape
                    )
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddProductBody(
    selectedImageUri: Uri?,
    onPickImage: () -> Unit
) {
    var recyclableItemName by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var isVisible by remember { mutableStateOf(false) }

    val repo = remember { ProductRepositoryImpl() }
    val viewModel = remember { ProductViewModel(repo) }

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
                        FloatingEcoIcon()
                        AnimatedVisibility(
                            visible = isVisible,
                            enter = slideInHorizontally(
                                initialOffsetX = { it },
                                animationSpec = tween(800, easing = FastOutSlowInEasing)
                            )
                        ) {
                            Text(
                                "Add Recyclable Item",
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
            AnimatedVisibility(
                visible = isVisible,
                enter = fadeIn(
                    animationSpec = tween(1000, easing = FastOutSlowInEasing)
                )
            ) {
                LazyColumn(
                    modifier = Modifier
                        .padding(padding)
                        .fillMaxSize()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    item {
                        // Animated Header Card
                        AnimatedVisibility(
                            visible = isVisible,
                            enter = slideInVertically(
                                initialOffsetY = { -it },
                                animationSpec = tween(800, delayMillis = 200)
                            )
                        ) {
                            HeaderCard()
                        }
                    }

                    item {
                        // Image Selection with animations
                        AnimatedVisibility(
                            visible = isVisible,
                            enter = slideInHorizontally(
                                initialOffsetX = { -it },
                                animationSpec = tween(800, delayMillis = 400)
                            )
                        ) {
                            ImageSelectionCard(
                                selectedImageUri = selectedImageUri,
                                onPickImage = onPickImage
                            )
                        }
                    }

                    item {
                        // Form Fields with staggered animation
                        AnimatedVisibility(
                            visible = isVisible,
                            enter = slideInHorizontally(
                                initialOffsetX = { it },
                                animationSpec = tween(800, delayMillis = 600)
                            )
                        ) {
                            FormFieldsCard(
                                recyclableItemName = recyclableItemName,
                                onItemNameChange = { recyclableItemName = it },
                                price = price,
                                onPriceChange = { price = it },
                                description = description,
                                onDescriptionChange = { description = it }
                            )
                        }
                    }

                    item {
                        // Animated Submit Button
                        AnimatedVisibility(
                            visible = isVisible,
                            enter = slideInVertically(
                                initialOffsetY = { it },
                                animationSpec = tween(800, delayMillis = 800)
                            )
                        ) {
                            SubmitButton(
                                onClick = {
                                    when {
                                        selectedImageUri == null -> {
                                            Toast.makeText(
                                                context,
                                                "Please select an image first",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                        recyclableItemName.isBlank() -> {
                                            Toast.makeText(
                                                context,
                                                "Please enter recyclable item name",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                        price.isBlank() -> {
                                            Toast.makeText(
                                                context,
                                                "Please enter price",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                        description.isBlank() -> {
                                            Toast.makeText(
                                                context,
                                                "Please enter description",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                        else -> {
                                            try {
                                                val priceValue = price.toDouble()
                                                viewModel.uploadImage(context, selectedImageUri) { imageUrl ->
                                                    if (imageUrl != null) {
                                                        val model = ProductModel(
                                                            "",
                                                            recyclableItemName,
                                                            priceValue,
                                                            description,
                                                            imageUrl
                                                        )
                                                        viewModel.addProduct(model) { success, message ->
                                                            Toast.makeText(context, message, Toast.LENGTH_LONG).show()
                                                            if (success) activity?.finish()
                                                        }
                                                    } else {
                                                        Log.e("Upload Error", "Failed to upload image to Cloudinary")
                                                        Toast.makeText(
                                                            context,
                                                            "Failed to upload image. Please try again.",
                                                            Toast.LENGTH_SHORT
                                                        ).show()
                                                    }
                                                }
                                            } catch (e: NumberFormatException) {
                                                Toast.makeText(
                                                    context,
                                                    "Please enter a valid price",
                                                    Toast.LENGTH_SHORT
                                                ).show()
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
fun HeaderCard() {
    val infiniteTransition = rememberInfiniteTransition(label = "header_animation")

    val backgroundShift by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(8000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
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
                    Icon(
                        imageVector = Icons.Default.Eco,
                        contentDescription = "Eco",
                        tint = Color.White,
                        modifier = Modifier.size(36.dp)
                    )
                    Icon(
                        imageVector = Icons.Default.Favorite,
                        contentDescription = "Love",
                        tint = AccentPink,
                        modifier = Modifier.size(24.dp)
                    )
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "Star",
                        tint = AccentOrange,
                        modifier = Modifier.size(28.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "🌱 Help Make Our Planet Greener! 🌱",
                    style = MaterialTheme.typography.titleLarge,
                    color = Color.White,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Every recyclable item is a step towards sustainability ♻️",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.9f),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(12.dp))

                PulsingLoadingDots()
            }
        }
    }
}

@Composable
fun ImageSelectionCard(
    selectedImageUri: Uri?,
    onPickImage: () -> Unit
) {
    var isPressed by remember { mutableStateOf(false) }

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "image_scale"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .scale(scale)
            .shadow(8.dp, RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.PhotoCamera,
                    contentDescription = "Camera",
                    tint = AccentBlue,
                    modifier = Modifier.size(24.dp)
                )
                Text(
                    text = "📸 Product Photo",
                    style = MaterialTheme.typography.titleMedium,
                    color = DeepGreen,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .border(
                        width = 2.dp,
                        brush = if (selectedImageUri != null) {
                            Brush.linearGradient(
                                colors = listOf(VibrantGreen, AccentBlue, AccentPurple)
                            )
                        } else {
                            Brush.linearGradient(
                                colors = listOf(Color.Gray.copy(alpha = 0.3f), Color.Gray.copy(alpha = 0.3f))
                            )
                        },
                        shape = RoundedCornerShape(16.dp)
                    )
                    .clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() }
                    ) {
                        isPressed = true
                        onPickImage()
                    },
                contentAlignment = Alignment.Center
            ) {
                LaunchedEffect(isPressed) {
                    if (isPressed) {
                        delay(150)
                        isPressed = false
                    }
                }

                if (selectedImageUri != null) {
                    AsyncImage(
                        model = selectedImageUri,
                        contentDescription = "Selected Image",
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(16.dp)),
                        contentScale = ContentScale.Crop
                    )

                    // Floating edit button
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(12.dp)
                            .size(36.dp)
                            .background(
                                brush = Brush.radialGradient(
                                    colors = listOf(AccentOrange, AccentPink)
                                ),
                                shape = CircleShape
                            )
                            .clickable { onPickImage() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.PhotoCamera,
                            contentDescription = "Change Photo",
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                brush = Brush.radialGradient(
                                    colors = listOf(
                                        VibrantGreen.copy(alpha = 0.1f),
                                        AccentBlue.copy(alpha = 0.05f)
                                    )
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(70.dp)
                                    .background(
                                        brush = Brush.radialGradient(
                                            colors = listOf(AccentBlue.copy(alpha = 0.2f), Color.Transparent)
                                        ),
                                        shape = CircleShape
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.PhotoCamera,
                                    contentDescription = "Add Photo",
                                    tint = AccentBlue,
                                    modifier = Modifier.size(32.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            Text(
                                text = "📱 Tap to add photo",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = DeepGreen
                            )

                            Text(
                                text = "Show your recyclable item clearly",
                                fontSize = 14.sp,
                                color = Color.Gray,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun FormFieldsCard(
    recyclableItemName: String,
    onItemNameChange: (String) -> Unit,
    price: String,
    onPriceChange: (String) -> Unit,
    description: String,
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
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = "Info",
                    tint = AccentPurple,
                    modifier = Modifier.size(24.dp)
                )
                Text(
                    text = "📝 Item Details",
                    style = MaterialTheme.typography.titleMedium,
                    color = DeepGreen,
                    fontWeight = FontWeight.Bold
                )
            }

            // Item Name Field
            OutlinedTextField(
                value = recyclableItemName,
                onValueChange = onItemNameChange,
                label = { Text("♻️ Recyclable Item Name", fontSize = 14.sp) },
                placeholder = { Text("e.g., Plastic Bottles, Paper, Metal Cans", fontSize = 14.sp) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Eco,
                        contentDescription = "Item Name",
                        tint = VibrantGreen
                    )
                },
                modifier = Modifier.fillMaxWidth(),
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
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.CurrencyRupee,
                        contentDescription = "Price",
                        tint = AccentOrange
                    )
                },
                modifier = Modifier.fillMaxWidth(),
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
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = "Description",
                        tint = AccentPurple
                    )
                },
                modifier = Modifier.fillMaxWidth(),
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
fun SubmitButton(onClick: () -> Unit) {
    var isPressed by remember { mutableStateOf(false) }
    val infiniteTransition = rememberInfiniteTransition(label = "submit_button")

    val gradientShift by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "gradient_shift"
    )

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "button_scale"
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
                    brush = Brush.linearGradient(
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
                ),
            contentAlignment = Alignment.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
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
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add",
                        modifier = Modifier.size(18.dp),
                        tint = Color.White
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    "🚀 Add Recyclable Item",
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
fun AddProductPreview() {
    MaterialTheme {
        AddProductBody(
            selectedImageUri = null,
            onPickImage = {}
        )
    }
}
