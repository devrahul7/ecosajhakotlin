package com.example.ecosajha.view

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.*
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import com.example.ecosajha.model.ProductModel
import com.example.ecosajha.model.UserModel
import com.example.ecosajha.repository.ProductRepositoryImpl
import com.example.ecosajha.repository.UserRepositoryImpl
import com.example.ecosajha.viewmodel.ProductViewModel
import com.example.ecosajha.viewmodel.UserViewModel
import com.google.firebase.auth.FirebaseUser
import kotlin.math.*

// Modern Color Palette for EcoSajha
private val EcoGreen = Color(0xFF00C853)
private val EcoGreenDark = Color(0xFF00A047)
private val EcoGreenLight = Color(0xFF69F0AE)
private val EcoGreenSurface = Color(0xFFE8F5E8)
private val EcoAccent = Color(0xFF66BB6A)
private val EcoBackground = Color(0xFFF8FFF8)
private val EcoCardBackground = Color(0xFFFFFFFF)
private val GradientStart = Color(0xFF4CAF50)
private val GradientEnd = Color(0xFF2E7D32)

class DashboardActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ModernEcoSajhaTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = EcoBackground
                ) {
                    ModernEcoSajhaDashboard()
                }
            }
        }
    }
}

@Composable
fun ModernEcoSajhaTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = lightColorScheme(
            primary = EcoGreen,
            onPrimary = Color.White,
            primaryContainer = EcoGreenLight,
            onPrimaryContainer = EcoGreenDark,
            secondary = EcoAccent,
            onSecondary = Color.White,
            secondaryContainer = EcoGreenSurface,
            onSecondaryContainer = EcoGreenDark,
            background = EcoBackground,
            onBackground = Color(0xFF1C1B1F),
            surface = EcoCardBackground,
            onSurface = Color(0xFF1C1B1F),
            surfaceVariant = Color(0xFFF3F3F3),
            onSurfaceVariant = Color(0xFF49454F),
            outline = Color(0xFF79747E),
            outlineVariant = Color(0xFFCAC4D0)
        ),
        content = content
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
@Composable
fun ModernEcoSajhaDashboard() {
    var selectedTab by remember { mutableStateOf(0) }
    var searchQuery by remember { mutableStateOf("") }
    var notificationMessage by remember { mutableStateOf<String?>(null) }
    var showStats by remember { mutableStateOf(false) }

    val repo = remember { ProductRepositoryImpl() }
    val viewModel = remember { ProductViewModel(repo) }
    val context = LocalContext.current

    val products = viewModel.allProducts.observeAsState(initial = emptyList())
    val loading = viewModel.loading.observeAsState(initial = false)

    LaunchedEffect(Unit) {
        viewModel.getAllProduct()
    }

    // Handle notification display
    LaunchedEffect(notificationMessage) {
        notificationMessage?.let { message ->
            delay(3000) // Show for 3 seconds
            notificationMessage = null
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            ModernEcoTopAppBar(
                onSearchClick = { selectedTab = 1 },
                onNotificationClick = { /* Handle notifications */ },
                onLogoClick = { selectedTab = 0 }, // Navigate to home tab
                notificationMessage = notificationMessage
            )
        },
        bottomBar = {
            ModernEcoBottomNavigation(
                selectedTab = selectedTab,
                onTabSelected = { selectedTab = it }
            )
        },
        floatingActionButton = {
            ModernFloatingActionButton(
                onClick = {
                    try {
                        val intent = Intent(context, AddProductActivity::class.java)
                        context.startActivity(intent)
                    } catch (e: Exception) {
                        Log.e("DashboardActivity", "Error opening AddProductActivity", e)
                        Toast.makeText(context, "Error opening add product page", Toast.LENGTH_SHORT).show()
                    }
                }
            )
        },
        containerColor = EcoBackground
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            EcoBackground,
                            Color(0xFFF0F8F0),
                            EcoGreenSurface.copy(alpha = 0.3f)
                        )
                    )
                )
        ) {
            AnimatedContent(
                targetState = selectedTab,
                transitionSpec = {
                    slideInHorizontally(
                        initialOffsetX = { if (targetState > initialState) it else -it },
                        animationSpec = tween(300, easing = FastOutSlowInEasing)
                    ) with slideOutHorizontally(
                        targetOffsetX = { if (targetState > initialState) -it else it },
                        animationSpec = tween(300, easing = FastOutSlowInEasing)
                    )
                },
                label = "tab_transition"
            ) { tab ->
                when (tab) {
                    0 -> {
                        if (showStats) {
                            ModernStatsDetailScreen(
                                products = products.value,
                                onBack = { showStats = false }
                            )
                        } else {
                            ModernHomeScreen(
                                products = products.value,
                                loading = loading.value,
                                onViewProduct = { productId ->
                                    try {
                                        if (productId.isNotEmpty() && productId != "null") {
                                            val intent = Intent(context, ViewProductActivity::class.java)
                                            intent.putExtra("productID", productId)
                                            context.startActivity(intent)

                                            // Find product name for notification
                                            val product = products.value.find { it?.productID.toString() == productId }
                                            notificationMessage = "📦 ${product?.productName ?: "Product"} viewed"
                                        } else {
                                            Toast.makeText(context, "Invalid product ID", Toast.LENGTH_SHORT).show()
                                        }
                                    } catch (e: Exception) {
                                        Log.e("DashboardActivity", "Error opening ViewProductActivity", e)
                                        Toast.makeText(context, "Error opening product details", Toast.LENGTH_SHORT).show()
                                    }
                                },
                                onEditProduct = { productId ->
                                    try {
                                        if (productId.isNotEmpty() && productId != "null") {
                                            val intent = Intent(context, UpdateProductActivity::class.java)
                                            intent.putExtra("productID", productId)
                                            context.startActivity(intent)
                                        } else {
                                            Toast.makeText(context, "Invalid product ID", Toast.LENGTH_SHORT).show()
                                        }
                                    } catch (e: Exception) {
                                        Log.e("DashboardActivity", "Error opening UpdateProductActivity", e)
                                        Toast.makeText(context, "Error opening edit page", Toast.LENGTH_SHORT).show()
                                    }
                                },
                                onDeleteProduct = { productId ->
                                    try {
                                        if (productId.isNotEmpty() && productId != "null") {
                                            val product = products.value.find { it?.productID.toString() == productId }
                                            viewModel.deleteProduct(productId) { success, message ->
                                                Toast.makeText(context, message, Toast.LENGTH_LONG).show()
                                                if (success) {
                                                    viewModel.getAllProduct()
                                                    notificationMessage = "🗑️ ${product?.productName ?: "Product"} deleted"
                                                }
                                            }
                                        } else {
                                            Toast.makeText(context, "Invalid product ID", Toast.LENGTH_SHORT).show()
                                        }
                                    } catch (e: Exception) {
                                        Log.e("DashboardActivity", "Error deleting product", e)
                                        Toast.makeText(context, "Error deleting product", Toast.LENGTH_SHORT).show()
                                    }
                                },
                                onAddProduct = {
                                    try {
                                        val intent = Intent(context, AddProductActivity::class.java)
                                        context.startActivity(intent)
                                    } catch (e: Exception) {
                                        Toast.makeText(context, "Error opening add product page", Toast.LENGTH_SHORT).show()
                                    }
                                },
                                onViewStats = { showStats = true },
                                onViewAllProducts = {
                                    try {
                                        val intent = Intent(context, ViewProductActivity::class.java)
                                        context.startActivity(intent)
                                    } catch (e: Exception) {
                                        Toast.makeText(context, "Error opening products view", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            )
                        }
                    }
                    1 -> ModernSearchScreen(
                        searchQuery = searchQuery,
                        onSearchQueryChange = { searchQuery = it },
                        products = products.value,
                        onViewProduct = { productId ->
                            try {
                                if (productId.isNotEmpty() && productId != "null") {
                                    val intent = Intent(context, ViewProductActivity::class.java)
                                    intent.putExtra("productID", productId)
                                    context.startActivity(intent)

                                    val product = products.value.find { it?.productID.toString() == productId }
                                    notificationMessage = "📦 ${product?.productName ?: "Product"} viewed"
                                } else {
                                    Toast.makeText(context, "Invalid product ID", Toast.LENGTH_SHORT).show()
                                }
                            } catch (e: Exception) {
                                Log.e("DashboardActivity", "Error opening ViewProductActivity from search", e)
                                Toast.makeText(context, "Error opening product details", Toast.LENGTH_SHORT).show()
                            }
                        }
                    )
                    2 -> ModernProfileScreen()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModernEcoTopAppBar(
    onSearchClick: () -> Unit,
    onNotificationClick: () -> Unit,
    onLogoClick: () -> Unit,
    notificationMessage: String? = null
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        shape = RoundedCornerShape(24.dp)
    ) {
        TopAppBar(
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.clickable { onLogoClick() } // Make clickable to navigate to home
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .background(
                                brush = Brush.radialGradient(
                                    colors = listOf(EcoGreen, EcoGreenDark)
                                ),
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "♻️",
                            fontSize = 24.sp
                        )
                    }
                    Column {
                        Text(
                            text = "EcoSajha",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = EcoGreenDark
                        )
                        Text(
                            text = "Recycle & Earn Rewards",
                            fontSize = 12.sp,
                            color = Color.Gray.copy(alpha = 0.8f),
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            },
            actions = {
                IconButton(
                    onClick = onSearchClick,
                    modifier = Modifier
                        .background(
                            color = EcoGreenSurface,
                            shape = CircleShape
                        )
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Search,
                        contentDescription = "Search",
                        tint = EcoGreenDark,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Box {
                    IconButton(
                        onClick = onNotificationClick,
                        modifier = Modifier
                            .background(
                                color = EcoGreenSurface,
                                shape = CircleShape
                            )
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Notifications,
                            contentDescription = "Notifications",
                            tint = EcoGreenDark,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    // Show notification badge when there's a message
                    if (notificationMessage != null) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .background(
                                    color = Color.Red,
                                    shape = CircleShape
                                )
                                .align(Alignment.TopEnd)
                                .offset(x = (-4).dp, y = 4.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.width(8.dp))
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color.Transparent
            )
        )

        // Show notification message
        AnimatedVisibility(
            visible = notificationMessage != null,
            enter = slideInVertically() + fadeIn(),
            exit = slideOutVertically() + fadeOut()
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = EcoGreenSurface
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = notificationMessage ?: "",
                    modifier = Modifier.padding(12.dp),
                    style = MaterialTheme.typography.bodyMedium,
                    color = EcoGreenDark,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
fun ModernHomeScreen(
    products: List<ProductModel?>,
    loading: Boolean,
    onViewProduct: (String) -> Unit,
    onEditProduct: (String) -> Unit,
    onDeleteProduct: (String) -> Unit,
    onAddProduct: () -> Unit,
    onViewStats: () -> Unit,
    onViewAllProducts: () -> Unit
) {
    val validProducts = products.filterNotNull()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(8.dp))
        }

        item {
            ModernWelcomeCard(
                onStartRecycling = onAddProduct // Navigate to AddProductActivity
            )
        }

        item {
            ModernStatsSection(productCount = validProducts.size)
        }

        item {
            ModernQuickActions(
                onAddItem = onAddProduct,
                onViewStats = onViewStats,
                onViewProducts = onViewAllProducts
            )
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "My Recyclables",
                        style = MaterialTheme.typography.headlineMedium,
                        color = EcoGreenDark,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = if (validProducts.isEmpty()) {
                            "Please add items to recycle"
                        } else {
                            "${validProducts.size} items ready to recycle"
                        },
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (validProducts.isEmpty()) Color.Red.copy(alpha = 0.7f) else Color.Gray
                    )
                }

                Surface(
                    color = if (validProducts.isEmpty()) Color.Red.copy(alpha = 0.1f) else EcoGreenSurface,
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Text(
                        text = "${validProducts.size}",
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                        style = MaterialTheme.typography.titleMedium,
                        color = if (validProducts.isEmpty()) Color.Red else EcoGreenDark,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        if (loading) {
            item {
                ModernLoadingSection()
            }
        } else if (validProducts.isEmpty()) {
            item {
                ModernEmptyStateSection(onAddProduct = onAddProduct)
            }
        } else {
            items(validProducts) { product ->
                ModernProductCard(
                    product = product,
                    onView = {
                        val productId = product.productID?.toString() ?: ""
                        if (productId.isNotEmpty()) {
                            onViewProduct(productId)
                        }
                    },
                    onEdit = {
                        val productId = product.productID?.toString() ?: ""
                        if (productId.isNotEmpty()) {
                            onEditProduct(productId)
                        }
                    },
                    onDelete = {
                        val productId = product.productID?.toString() ?: ""
                        if (productId.isNotEmpty()) {
                            onDeleteProduct(productId)
                        }
                    }
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

@Composable
fun ModernWelcomeCard(onStartRecycling: () -> Unit) {
    val infiniteTransition = rememberInfiniteTransition(label = "welcome_glow")
    val glowOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(8000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "glow_offset"
    )

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 16.dp),
        shape = RoundedCornerShape(24.dp)
    ) {
        Box {
            // Animated background gradient
            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
            ) {
                val radius = size.minDimension / 2
                val centerX = size.width / 2
                val centerY = size.height / 2

                val offsetX = cos(Math.toRadians(glowOffset.toDouble())).toFloat() * 50f
                val offsetY = sin(Math.toRadians(glowOffset.toDouble())).toFloat() * 30f

                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            EcoGreen.copy(alpha = 0.3f),
                            EcoGreenLight.copy(alpha = 0.1f),
                            Color.Transparent
                        ),
                        radius = radius
                    ),
                    center = Offset(centerX + offsetX, centerY + offsetY),
                    radius = radius
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "Welcome Back!",
                        style = MaterialTheme.typography.headlineMedium,
                        color = EcoGreenDark,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Ready to make the planet greener?",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.Gray.copy(alpha = 0.8f)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Surface(
                        color = EcoGreen,
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier.clickable { onStartRecycling() }
                    ) {
                        Text(
                            text = "🌱 Start Recycling",
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                            color = Color.White,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .background(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    EcoGreenLight.copy(alpha = 0.3f),
                                    Color.Transparent
                                )
                            ),
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "🌍",
                        fontSize = 48.sp
                    )
                }
            }
        }
    }
}

@Composable
fun ModernStatsSection(productCount: Int) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(getModernStatsData(productCount)) { stat ->
            ModernStatCard(stat = stat)
        }
    }
}

@Composable
fun ModernQuickActions(
    onAddItem: () -> Unit,
    onViewStats: () -> Unit,
    onViewProducts: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = "Quick Actions",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = EcoGreenDark
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                ModernActionButton(
                    icon = "📦",
                    title = "Add Item",
                    subtitle = "New recyclable",
                    backgroundColor = EcoGreenSurface,
                    modifier = Modifier.weight(1f),
                    onClick = onAddItem
                )

                ModernActionButton(
                    icon = "📊",
                    title = "View Stats",
                    subtitle = "Your progress",
                    backgroundColor = Color(0xFFE3F2FD),
                    modifier = Modifier.weight(1f),
                    onClick = onViewStats
                )

                ModernActionButton(
                    icon = "📋",
                    title = "View Items",
                    subtitle = "All recyclables",
                    backgroundColor = Color(0xFFFFF3E0),
                    modifier = Modifier.weight(1f),
                    onClick = onViewProducts
                )
            }
        }
    }
}

@Composable
fun ModernEmptyStateSection(onAddProduct: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        shape = RoundedCornerShape(24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(40.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .background(
                        color = Color.Red.copy(alpha = 0.1f),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "📦",
                    fontSize = 48.sp
                )
            }
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = "Please Add Items to Recycle",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = Color.Red.copy(alpha = 0.8f)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Start your eco-journey by adding your first recyclable item",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(20.dp))
            Surface(
                color = EcoGreen,
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier.clickable { onAddProduct() }
            ) {
                Text(
                    text = "🌱 Add Your First Item",
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 12.dp),
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
fun ModernStatsDetailScreen(
    products: List<ProductModel?>,
    onBack: () -> Unit
) {
    val validProducts = products.filterNotNull()
    val totalItems = validProducts.size
    val totalValue = validProducts.sumOf { it.price?.toDouble() ?: 0.0 }
    val carbonSaved = totalItems * 2.5 // Estimate: 2.5kg CO2 per item

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = EcoGreen
                    )
                }
                Text(
                    text = "Recycling Statistics",
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold,
                    color = EcoGreenDark
                )
            }
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 12.dp),
                shape = RoundedCornerShape(24.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "🏆",
                        fontSize = 64.sp
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Great Work!",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = EcoGreenDark
                    )
                    Text(
                        text = "Your recycling impact",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.Gray
                    )
                }
            }
        }

        items(getDetailedStats(totalItems, totalValue, carbonSaved)) { stat ->
            ModernDetailedStatCard(stat = stat)
        }
    }
}

@Composable
fun ModernDetailedStatCard(stat: DetailedStatData) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        shape = RoundedCornerShape(20.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .background(
                        color = stat.backgroundColor,
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stat.icon,
                    fontSize = 32.sp
                )
            }

            Spacer(modifier = Modifier.width(20.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = stat.value,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = EcoGreenDark
                )
                Text(
                    text = stat.label,
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = stat.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray.copy(alpha = 0.8f)
                )
            }
        }
    }
}

// Helper data classes and functions
data class ModernBottomNavItem(
    val label: String,
    val icon: ImageVector,
    val selectedIcon: ImageVector
)

data class ModernStatData(
    val icon: String,
    val value: String,
    val label: String,
    val backgroundColor: Color
)

data class DetailedStatData(
    val icon: String,
    val value: String,
    val label: String,
    val description: String,
    val backgroundColor: Color
)

fun getModernStatsData(productCount: Int): List<ModernStatData> = listOf(
    ModernStatData("📦", "$productCount", "Total Items", EcoGreenSurface),
    ModernStatData("💰", "₹${productCount * 45}", "Est. Earnings", Color(0xFFE8F5E8)),
    ModernStatData("🌱", "${(productCount * 2.5).toInt()} kg", "CO₂ Saved", Color(0xFFE3F2FD)),
    ModernStatData("♻️", "$productCount", "Items Recycled", Color(0xFFFFF3E0))
)

fun getDetailedStats(totalItems: Int, totalValue: Double, carbonSaved: Double): List<DetailedStatData> = listOf(
    DetailedStatData(
        icon = "📦",
        value = "$totalItems",
        label = "Items Added",
        description = "Total recyclable items in your collection",
        backgroundColor = EcoGreenSurface
    ),
    DetailedStatData(
        icon = "💰",
        value = "₹${totalValue.toInt()}",
        label = "Estimated Value",
        description = "Potential earnings from your recyclables",
        backgroundColor = Color(0xFFE8F5E8)
    ),
    DetailedStatData(
        icon = "🌱",
        value = "${carbonSaved.toInt()} kg",
        label = "CO₂ Saved",
        description = "Environmental impact of your recycling",
        backgroundColor = Color(0xFFE3F2FD)
    ),
    DetailedStatData(
        icon = "🏆",
        value = "Level ${(totalItems / 5) + 1}",
        label = "Eco Level",
        description = "Your current environmental impact level",
        backgroundColor = Color(0xFFFFF3E0)
    )
)

// Continue with the rest of the components (ModernActionButton, ModernProductCard, etc.)
// [Rest of the code remains the same as in the previous version]

@Composable
fun ModernActionButton(
    icon: String,
    title: String,
    subtitle: String,
    backgroundColor: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .clickable { onClick() }
            .height(80.dp),
        colors = CardDefaults.cardColors(
            containerColor = backgroundColor
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = icon,
                fontSize = 24.sp
            )
            Text(
                text = title,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = EcoGreenDark,
                textAlign = TextAlign.Center
            )
            Text(
                text = subtitle,
                fontSize = 10.sp,
                color = Color.Gray,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun ModernFloatingActionButton(onClick: () -> Unit) {
    val infiniteTransition = rememberInfiniteTransition(label = "fab_glow")
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.8f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow_alpha"
    )

    Box(contentAlignment = Alignment.Center) {
        // Glow effect
        Box(
            modifier = Modifier
                .size(80.dp)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            EcoGreen.copy(alpha = glowAlpha),
                            Color.Transparent
                        )
                    ),
                    shape = CircleShape
                )
        )

        FloatingActionButton(
            onClick = onClick,
            modifier = Modifier.size(64.dp),
            containerColor = EcoGreen,
            contentColor = Color.White,
            elevation = FloatingActionButtonDefaults.elevation(
                defaultElevation = 12.dp,
                pressedElevation = 16.dp
            )
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Add Recyclable Item",
                modifier = Modifier.size(28.dp)
            )
        }
    }
}

@Composable
fun ModernEcoBottomNavigation(
    selectedTab: Int,
    onTabSelected: (Int) -> Unit
) {
    val items = listOf(
        ModernBottomNavItem("Home", Icons.Outlined.Home, Icons.Filled.Home),
        ModernBottomNavItem("Search", Icons.Outlined.Search, Icons.Filled.Search),
        ModernBottomNavItem("Profile", Icons.Outlined.Person, Icons.Filled.Person)
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 12.dp),
        shape = RoundedCornerShape(28.dp)
    ) {
        NavigationBar(
            containerColor = Color.Transparent,
            tonalElevation = 0.dp,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            items.forEachIndexed { index, item ->
                NavigationBarItem(
                    icon = {
                        Icon(
                            imageVector = if (selectedTab == index) item.selectedIcon else item.icon,
                            contentDescription = item.label,
                            modifier = Modifier.size(24.dp)
                        )
                    },
                    label = {
                        Text(
                            item.label,
                            fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Medium,
                            fontSize = 12.sp
                        )
                    },
                    selected = selectedTab == index,
                    onClick = { onTabSelected(index) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = EcoGreen,
                        selectedTextColor = EcoGreen,
                        indicatorColor = EcoGreenSurface,
                        unselectedIconColor = Color.Gray,
                        unselectedTextColor = Color.Gray
                    ),
                    modifier = Modifier.clip(RoundedCornerShape(20.dp))
                )
            }
        }
    }
}

// Add the remaining components from the previous version...
@Composable
fun ModernProductCard(
    product: ProductModel,
    onView: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    var isExpanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize(
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessLow
                )
            ),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 12.dp),
        shape = RoundedCornerShape(24.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(12.dp)
                                .background(
                                    color = EcoGreen,
                                    shape = CircleShape
                                )
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = product.productName ?: "Unknown Item",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = EcoGreenDark
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Surface(
                        color = EcoGreenSurface,
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "₹",
                                fontSize = 16.sp,
                                color = EcoGreen,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "${product.price ?: 0}",
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = EcoGreen
                            )
                            Text(
                                text = "/kg",
                                fontSize = 12.sp,
                                color = Color.Gray
                            )
                        }
                    }

                    if (isExpanded) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = product.description ?: "No description available",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Gray,
                            lineHeight = 20.sp
                        )
                    }
                }

                IconButton(
                    onClick = { isExpanded = !isExpanded }
                ) {
                    Icon(
                        imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = if (isExpanded) "Collapse" else "Expand",
                        tint = EcoGreen
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                ModernActionChip(
                    icon = Icons.Outlined.Visibility,
                    text = "View",
                    color = EcoGreen,
                    modifier = Modifier.weight(1f),
                    onClick = onView
                )

                ModernActionChip(
                    icon = Icons.Outlined.Edit,
                    text = "Edit",
                    color = Color(0xFF1976D2),
                    modifier = Modifier.weight(1f),
                    onClick = onEdit
                )

                ModernActionChip(
                    icon = Icons.Outlined.Delete,
                    text = "Delete",
                    color = Color(0xFFD32F2F),
                    modifier = Modifier.weight(1f),
                    onClick = onDelete
                )
            }
        }
    }
}

@Composable
fun ModernActionChip(
    icon: ImageVector,
    text: String,
    color: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        modifier = modifier
            .clickable { onClick() }
            .height(40.dp),
        color = color.copy(alpha = 0.1f),
        shape = RoundedCornerShape(20.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = text,
                tint = color,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = text,
                color = color,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

// Loading Section
@Composable
fun ModernLoadingSection() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(40.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CircularProgressIndicator(
                color = EcoGreen,
                strokeWidth = 4.dp,
                modifier = Modifier.size(48.dp)
            )
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = "Loading recyclable items...",
                style = MaterialTheme.typography.bodyLarge,
                color = Color.Gray,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

// Search Screen
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModernSearchScreen(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    products: List<ProductModel?>,
    onViewProduct: (String) -> Unit
) {
    val filteredProducts = remember(searchQuery, products) {
        if (searchQuery.isEmpty()) {
            emptyList()
        } else {
            products.filterNotNull().filter { product ->
                product.productName?.contains(searchQuery, ignoreCase = true) == true ||
                        product.description?.contains(searchQuery, ignoreCase = true) == true
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Spacer(modifier = Modifier.height(8.dp))

        Card(
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
            shape = RoundedCornerShape(24.dp)
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = onSearchQueryChange,
                label = { Text("Search recyclable items") },
                placeholder = { Text("What are you looking to recycle?") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = EcoGreen
                    )
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { onSearchQueryChange("") }) {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = "Clear",
                                tint = Color.Gray
                            )
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = EcoGreen,
                    focusedLabelColor = EcoGreen,
                    cursorColor = EcoGreen,
                    unfocusedBorderColor = Color.Gray.copy(alpha = 0.3f)
                )
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        if (searchQuery.isEmpty()) {
            ModernEmptySearchState()
        } else if (filteredProducts.isEmpty()) {
            ModernNoResultsState(query = searchQuery)
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(filteredProducts) { product ->
                    ModernSearchResultCard(
                        product = product,
                        searchQuery = searchQuery,
                        onView = {
                            val productId = product.productID?.toString() ?: ""
                            if (productId.isNotEmpty()) {
                                onViewProduct(productId)
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun ModernSearchResultCard(
    product: ProductModel,
    searchQuery: String,
    onView: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onView() },
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        shape = RoundedCornerShape(20.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .background(
                        color = EcoGreenSurface,
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "📦",
                    fontSize = 24.sp
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = product.productName ?: "Unknown Product",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = EcoGreenDark
                )

                Surface(
                    color = EcoGreen,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "₹${product.price ?: 0}/kg",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = product.description ?: "No description",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = "View details",
                tint = EcoGreen,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Composable
fun ModernEmptySearchState() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(120.dp)
                .background(
                    color = EcoGreenSurface,
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "🔍",
                fontSize = 64.sp
            )
        }
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "Search Recyclables",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = EcoGreenDark
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Enter keywords to find items you can recycle and earn from",
            style = MaterialTheme.typography.bodyLarge,
            color = Color.Gray,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun ModernNoResultsState(query: String) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(120.dp)
                .background(
                    color = Color.Gray.copy(alpha = 0.1f),
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "😔",
                fontSize = 64.sp
            )
        }
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "No Results Found",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = EcoGreenDark
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "No items match \"$query\"",
            style = MaterialTheme.typography.bodyLarge,
            color = Color.Gray,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Try searching with different keywords",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray,
            textAlign = TextAlign.Center
        )
    }
}

// Profile Screen
@Composable
fun ModernProfileScreen() {
    val context = LocalContext.current
    val userRepo = remember { UserRepositoryImpl() }
    val userViewModel = remember { UserViewModel(userRepo) }

    val user = userViewModel.users.observeAsState(initial = null)
    val currentUser = userViewModel.getCurrentUser()

    LaunchedEffect(currentUser) {
        currentUser?.uid?.let { userId ->
            userViewModel.getUserByID(userId)
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(8.dp))
        }

        item {
            if (user.value == null) {
                ModernProfileLoadingCard()
            } else {
                ModernProfileHeader(user = user.value, currentUser = currentUser)
            }
        }

        item {
            ModernProfileStatsSection()
        }

        item {
            ModernProfileActions(
                onEditProfile = {
                    try {
                        val intent = Intent(context, EditProfileActivity::class.java)
                        user.value?.let { userData ->
                            intent.putExtra("userID", userData.userID)
                            intent.putExtra("fullName", userData.fullName)
                            intent.putExtra("email", userData.email)
                            intent.putExtra("phoneNumber", userData.phoneNumber)
                            intent.putExtra("address", userData.address)
                        }
                        context.startActivity(intent)
                    } catch (e: Exception) {
                        Toast.makeText(context, "Error opening edit profile", Toast.LENGTH_SHORT).show()
                    }
                },
                onLogout = {
                    userViewModel.logout { success, message ->
                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                        if (success) {
                            try {
                                val intent = Intent(context, LoginActivity::class.java)
                                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                context.startActivity(intent)
                            } catch (e: Exception) {
                                Toast.makeText(context, "Please restart the app to login", Toast.LENGTH_LONG).show()
                            }
                        }
                    }
                }
            )
        }

        item {
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

@Composable
fun ModernProfileHeader(user: UserModel?, currentUser: FirebaseUser?) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 16.dp),
        shape = RoundedCornerShape(28.dp)
    ) {
        Box {
            // Background gradient
            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
            ) {
                drawRect(
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            EcoGreen.copy(alpha = 0.1f),
                            EcoGreenLight.copy(alpha = 0.2f)
                        )
                    )
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(28.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Profile Picture with glow effect
                Box(
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(140.dp)
                            .background(
                                brush = Brush.radialGradient(
                                    colors = listOf(
                                        EcoGreen.copy(alpha = 0.3f),
                                        Color.Transparent
                                    )
                                ),
                                shape = CircleShape
                            )
                    )

                    Box(
                        modifier = Modifier
                            .size(120.dp)
                            .background(
                                brush = Brush.radialGradient(
                                    colors = listOf(EcoGreen, EcoGreenDark)
                                ),
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = user?.fullName?.take(2)?.uppercase() ?: "U",
                            fontSize = 42.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = user?.fullName ?: "Anonymous User",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = EcoGreenDark
                )

                Text(
                    text = user?.email ?: currentUser?.email ?: "No email",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )

                if (!user?.phoneNumber.isNullOrEmpty()) {
                    Text(
                        text = user?.phoneNumber ?: "",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                }

                if (!user?.address.isNullOrEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Surface(
                        color = EcoGreenSurface,
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text(
                            text = "📍 ${user?.address}",
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            style = MaterialTheme.typography.bodySmall,
                            color = EcoGreenDark,
                            textAlign = TextAlign.Center
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Surface(
                        color = EcoGreen,
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "🌱", fontSize = 16.sp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Eco Warrior",
                                style = MaterialTheme.typography.labelLarge,
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Surface(
                        color = if (currentUser?.isEmailVerified == true) EcoGreenSurface else Color.Red.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (currentUser?.isEmailVerified == true) "✅" else "⚠️",
                                fontSize = 14.sp
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (currentUser?.isEmailVerified == true) "Verified" else "Not Verified",
                                style = MaterialTheme.typography.labelMedium,
                                color = if (currentUser?.isEmailVerified == true) EcoGreenDark else Color.Red,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ModernProfileLoadingCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        shape = RoundedCornerShape(24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(40.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CircularProgressIndicator(
                color = EcoGreen,
                modifier = Modifier.size(48.dp)
            )
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = "Loading profile...",
                style = MaterialTheme.typography.bodyLarge,
                color = Color.Gray
            )
        }
    }
}

@Composable
fun ModernProfileStatsSection() {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(getProfileStats()) { stat ->
            ModernStatCard(stat = stat)
        }
    }
}

@Composable
fun ModernStatCard(stat: ModernStatData) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        shape = RoundedCornerShape(20.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(
                        color = stat.backgroundColor,
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stat.icon,
                    fontSize = 20.sp
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column {
                Text(
                    text = stat.value,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = EcoGreenDark
                )
                Text(
                    text = stat.label,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray,
                    fontSize = 11.sp
                )
            }
        }
    }
}

@Composable
fun ModernProfileActions(
    onEditProfile: () -> Unit,
    onLogout: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Button(
            onClick = onEditProfile,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = EcoGreen
            ),
            shape = RoundedCornerShape(20.dp),
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Edit,
                contentDescription = null,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                "Edit Profile",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedButton(
                onClick = { /* Handle settings */ },
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = EcoGreen
                ),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(2.dp, EcoGreen)
            ) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Settings", fontWeight = FontWeight.SemiBold)
            }

            OutlinedButton(
                onClick = onLogout,
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = Color.Red
                ),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(2.dp, Color.Red)
            ) {
                Icon(
                    imageVector = Icons.Default.ExitToApp,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Logout", fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

fun getProfileStats(): List<ModernStatData> = listOf(
    ModernStatData("⭐", "4.8", "Rating", Color(0xFFFFF3E0)),
    ModernStatData("📈", "Level 5", "Eco Level", EcoGreenSurface),
    ModernStatData("🏆", "15", "Badges", Color(0xFFE3F2FD)),
    ModernStatData("👥", "48", "Referrals", Color(0xFFF3E5F5))
)
