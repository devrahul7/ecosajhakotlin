package com.example.ecosajha.view

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.ecosajha.repository.ProductRepositoryImpl
import com.example.ecosajha.viewmodel.ProductViewModel

// Define custom green color scheme for EcoSajha
private val EcoGreen = Color(0xFF4CAF50)
private val EcoGreenDark = Color(0xFF388E3C)
private val EcoGreenLight = Color(0xFFC8E6C9)
private val EcoBackground = Color(0xFFF1F8E9)

class ViewProductActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ViewProductScreen()
        }
    }
}

@Composable
fun ViewProductScreen() {
    val context = LocalContext.current
    val activity = context as? Activity
    val productID: String? = activity?.intent?.getStringExtra("productID")

    if (productID.isNullOrEmpty()) {
        // Handle case where productID is not provided
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = "Error",
                        tint = Color.Red,
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Recyclable Item Not Found",
                        style = MaterialTheme.typography.headlineSmall,
                        color = EcoGreenDark,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Button(
                        onClick = { activity?.finish() },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = EcoGreen,
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Go Back", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
        return
    }

    ViewProductBody(productID = productID)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ViewProductBody(productID: String) {
    var isLoading by remember { mutableStateOf(true) }

    val repo = remember { ProductRepositoryImpl() }
    val viewModel = remember { ProductViewModel(repo) }

    val context = LocalContext.current
    val activity = context as? Activity

    // Observe product data
    val product = viewModel.products.observeAsState(initial = null)

    // Load product data when component mounts
    LaunchedEffect(productID) {
        viewModel.getProductByID(productID)
    }

    // Update loading state when product data is loaded
    LaunchedEffect(product.value) {
        if (product.value != null) {
            isLoading = false
        }
    }

    Scaffold(
        containerColor = EcoBackground,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Recyclable Item Details",
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
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
                    IconButton(
                        onClick = {
                            val intent = Intent(context, UpdateProductActivity::class.java)
                            intent.putExtra("productID", productID)
                            context.startActivity(intent)
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = EcoGreen
                )
            )
        }
    ) { padding ->
        if (isLoading) {
            // Loading state
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator(
                            color = EcoGreen,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Loading details...",
                            style = MaterialTheme.typography.bodyLarge,
                            color = EcoGreenDark,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        } else {
            product.value?.let { productData ->
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    EcoBackground,
                                    Color(0xFFE8F5E8)
                                )
                            )
                        ),
                    verticalArrangement = Arrangement.spacedBy(20.dp),
                    contentPadding = PaddingValues(16.dp)
                ) {
                    item {
                        // Product Image Card with Real Image Support
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                            shape = RoundedCornerShape(20.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(300.dp)
                                    .padding(16.dp)
                                    .clip(RoundedCornerShape(16.dp))
                            ) {
                                // Get image URL from product data
                                val imageUrl = getImageUrl(productData)

                                if (!imageUrl.isNullOrEmpty()) {
                                    // Show actual uploaded image
                                    AsyncImage(
                                        model = ImageRequest.Builder(context)
                                            .data(imageUrl)
                                            .crossfade(true)
                                            .build(),
                                        contentDescription = "Product Image",
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .clip(RoundedCornerShape(16.dp)),
                                        contentScale = ContentScale.Crop,
                                        onError = {
                                            Log.e("ViewProduct", "Error loading image: ${it.result.throwable.message}")
                                        }
                                    )
                                } else {
                                    // Fallback placeholder when no image
                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .background(
                                                Brush.verticalGradient(
                                                    colors = listOf(
                                                        EcoGreenLight,
                                                        EcoGreen.copy(alpha = 0.3f)
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
                                                fontSize = 64.sp
                                            )
                                            Spacer(modifier = Modifier.height(8.dp))
                                            Text(
                                                text = "No Image Available",
                                                color = EcoGreenDark,
                                                fontSize = 16.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                            Text(
                                                text = "Image will appear here when uploaded",
                                                color = Color.Gray,
                                                fontSize = 12.sp,
                                                textAlign = TextAlign.Center
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    item {
                        // Product Details Card
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                            shape = RoundedCornerShape(20.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(24.dp),
                                verticalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                // Product Name
                                Text(
                                    text = productData.productName ?: "Unknown Item",
                                    fontSize = 28.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = EcoGreenDark
                                )

                                // Price Section
                                Row(
                                    verticalAlignment = Alignment.Bottom
                                ) {
                                    Text(
                                        text = "₹",
                                        fontSize = 20.sp,
                                        color = EcoGreen,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "${productData.price ?: 0}",
                                        fontSize = 36.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = EcoGreen
                                    )
                                    Text(
                                        text = " /kg",
                                        fontSize = 16.sp,
                                        color = Color.Gray,
                                        modifier = Modifier.padding(bottom = 4.dp)
                                    )
                                }

                                HorizontalDivider(
                                    color = EcoGreenLight,
                                    thickness = 1.dp,
                                    modifier = Modifier.padding(vertical = 8.dp)
                                )

                                // Description Section
                                Text(
                                    text = "Description",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = EcoGreenDark
                                )

                                Text(
                                    text = productData.description ?: "No description available for this recyclable item.",
                                    fontSize = 16.sp,
                                    color = Color.Gray,
                                    lineHeight = 24.sp
                                )
                            }
                        }
                    }

                    item {
                        // Product Info Card
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(20.dp),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                InfoItem(
                                    icon = "🏷️",
                                    title = "Category",
                                    value = "Recyclable"
                                )
                                InfoItem(
                                    icon = "📅",
                                    title = "Added",
                                    value = "Today"
                                )
                                InfoItem(
                                    icon = "📊",
                                    title = "Status",
                                    value = "Active"
                                )
                            }
                        }
                    }

                    item {
                        // Environmental Impact Card
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = EcoGreen
                            ),
                            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                            shape = RoundedCornerShape(20.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "🌍",
                                    fontSize = 48.sp
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    text = "Environmental Impact",
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "By recycling this item, you're contributing to a cleaner planet and reducing waste in our environment!",
                                    fontSize = 14.sp,
                                    color = Color.White.copy(alpha = 0.9f),
                                    textAlign = TextAlign.Center,
                                    lineHeight = 20.sp
                                )
                            }
                        }
                    }

                    item {
                        // Action Buttons
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            // Edit Button
                            Button(
                                onClick = {
                                    val intent = Intent(context, UpdateProductActivity::class.java)
                                    intent.putExtra("productID", productID)
                                    context.startActivity(intent)
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(56.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = EcoGreen,
                                    contentColor = Color.White
                                ),
                                shape = RoundedCornerShape(16.dp),
                                elevation = ButtonDefaults.buttonElevation(defaultElevation = 6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = "Edit",
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    "Edit Item",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            // Share Button
                            OutlinedButton(
                                onClick = {
                                    Toast.makeText(
                                        context,
                                        "Share feature coming soon!",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(56.dp),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    contentColor = EcoGreen
                                ),
                                shape = RoundedCornerShape(16.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Share,
                                    contentDescription = "Share",
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    "Share",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
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

// Helper function to safely get image URL from ProductModel
@Composable
fun getImageUrl(product: Any): String? {
    return try {
        // Try different possible field names for image URL
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

        // If no image field found, log available fields for debugging
        Log.d("ViewProduct", "Available fields: ${fields.map { it.name }}")
        null
    } catch (e: Exception) {
        Log.e("ViewProduct", "Error getting image URL: ${e.message}")
        null
    }
}

@Composable
fun InfoItem(
    icon: String,
    title: String,
    value: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = icon,
            fontSize = 24.sp
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = title,
            fontSize = 12.sp,
            color = Color.Gray,
            fontWeight = FontWeight.Medium
        )
        Text(
            text = value,
            fontSize = 14.sp,
            color = EcoGreenDark,
            fontWeight = FontWeight.Bold
        )
    }
}
