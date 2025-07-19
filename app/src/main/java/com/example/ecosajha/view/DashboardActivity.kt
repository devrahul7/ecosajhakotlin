package com.example.ecosajha.view

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.foundation.background
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
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ecosajha.model.ProductModel
import com.example.ecosajha.repository.ProductRepositoryImpl
import com.example.ecosajha.viewmodel.ProductViewModel

// Define custom green color scheme for EcoSajha
private val EcoGreen = Color(0xFF4CAF50)
private val EcoGreenDark = Color(0xFF388E3C)
private val EcoGreenLight = Color(0xFFC8E6C9)
private val EcoBackground = Color(0xFFF1F8E9)

class DashboardActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            EcoSajhaTheme {
                EcoSajhaDashboard()
            }
        }
    }
}

@Composable
fun EcoSajhaTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = lightColorScheme(
            primary = EcoGreen,
            secondary = Color(0xFF81C784),
            background = EcoBackground,
            surface = Color.White,
            onPrimary = Color.White,
            onSecondary = Color.Black,
            onBackground = Color.Black,
            onSurface = Color.Black
        ),
        content = content
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
@Composable
fun EcoSajhaDashboard() {
    var selectedTab by remember { mutableStateOf(0) }
    var searchQuery by remember { mutableStateOf("") }

    val repo = remember { ProductRepositoryImpl() }
    val viewModel = remember { ProductViewModel(repo) }
    val context = LocalContext.current

    val products = viewModel.allProducts.observeAsState(initial = emptyList())
    val loading = viewModel.loading.observeAsState(initial = false)

    LaunchedEffect(Unit) {
        viewModel.getAllProduct()
    }

    Scaffold(
        topBar = {
            EcoTopAppBar(
                onSearchClick = { selectedTab = 1 },
                onSettingsClick = { /* Handle settings */ }
            )
        },
        bottomBar = {
            EcoBottomNavigation(
                selectedTab = selectedTab,
                onTabSelected = { selectedTab = it }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    try {
                        val intent = Intent(context, AddProductActivity::class.java)
                        context.startActivity(intent)
                    } catch (e: Exception) {
                        Log.e("DashboardActivity", "Error opening AddProductActivity", e)
                        Toast.makeText(context, "Error opening add product page", Toast.LENGTH_SHORT).show()
                    }
                },
                containerColor = EcoGreen,
                shape = CircleShape,
                modifier = Modifier.size(60.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Recyclable Item",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            EcoBackground,
                            Color(0xFFE8F5E8)
                        )
                    )
                )
        ) {
            AnimatedContent(
                targetState = selectedTab,
                transitionSpec = {
                    slideInHorizontally { it } with slideOutHorizontally { -it }
                },
                label = "tab_transition"
            ) { tab ->
                when (tab) {
                    0 -> HomeScreen(
                        products = products.value,
                        loading = loading.value,
                        onViewProduct = { productId ->
                            try {
                                if (productId.isNotEmpty() && productId != "null") {
                                    Log.d("DashboardActivity", "Viewing product with ID: $productId")
                                    val intent = Intent(context, ViewProductActivity::class.java)
                                    intent.putExtra("productID", productId)
                                    context.startActivity(intent)
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
                                    viewModel.deleteProduct(productId) { success, message ->
                                        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
                                        if (success) {
                                            viewModel.getAllProduct()
                                        }
                                    }
                                } else {
                                    Toast.makeText(context, "Invalid product ID", Toast.LENGTH_SHORT).show()
                                }
                            } catch (e: Exception) {
                                Log.e("DashboardActivity", "Error deleting product", e)
                                Toast.makeText(context, "Error deleting product", Toast.LENGTH_SHORT).show()
                            }
                        }
                    )
                    1 -> SearchScreen(
                        searchQuery = searchQuery,
                        onSearchQueryChange = { searchQuery = it },
                        products = products.value,
                        onViewProduct = { productId ->
                            try {
                                if (productId.isNotEmpty() && productId != "null") {
                                    val intent = Intent(context, ViewProductActivity::class.java)
                                    intent.putExtra("productID", productId)
                                    context.startActivity(intent)
                                } else {
                                    Toast.makeText(context, "Invalid product ID", Toast.LENGTH_SHORT).show()
                                }
                            } catch (e: Exception) {
                                Log.e("DashboardActivity", "Error opening ViewProductActivity from search", e)
                                Toast.makeText(context, "Error opening product details", Toast.LENGTH_SHORT).show()
                            }
                        }
                    )
                    2 -> ProfileScreen()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EcoTopAppBar(
    onSearchClick: () -> Unit,
    onSettingsClick: () -> Unit
) {
    TopAppBar(
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "♻️",
                    fontSize = 28.sp
                )
                Column {
                    Text(
                        text = "EcoSajha",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = EcoGreenDark
                    )
                    Text(
                        text = "Recycle & Earn",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }
            }
        },
        actions = {
            IconButton(onClick = onSearchClick) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search",
                    tint = EcoGreenDark
                )
            }
            IconButton(onClick = onSettingsClick) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "Settings",
                    tint = EcoGreenDark
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.White,
            titleContentColor = EcoGreenDark
        ),
        modifier = Modifier.clip(RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp))
    )
}

@Composable
fun EcoBottomNavigation(
    selectedTab: Int,
    onTabSelected: (Int) -> Unit
) {
    val items = listOf(
        BottomNavItem("Home", Icons.Default.Home),
        BottomNavItem("Search", Icons.Default.Search),
        BottomNavItem("Profile", Icons.Default.Person)
    )

    NavigationBar(
        containerColor = Color.White,
        tonalElevation = 12.dp,
        modifier = Modifier.clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
    ) {
        items.forEachIndexed { index, item ->
            NavigationBarItem(
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.label
                    )
                },
                label = { Text(item.label, fontWeight = FontWeight.Medium) },
                selected = selectedTab == index,
                onClick = { onTabSelected(index) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = EcoGreenDark,
                    selectedTextColor = EcoGreenDark,
                    indicatorColor = EcoGreenLight,
                    unselectedIconColor = Color.Gray,
                    unselectedTextColor = Color.Gray
                )
            )
        }
    }
}

@Composable
fun HomeScreen(
    products: List<ProductModel?>,
    loading: Boolean,
    onViewProduct: (String) -> Unit,
    onEditProduct: (String) -> Unit,
    onDeleteProduct: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        item {
            WelcomeCard()
        }

        item {
            StatsSection()
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "My Recyclable Items",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = EcoGreenDark
                )

                Text(
                    text = "${products.filterNotNull().size} items",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        if (loading) {
            item {
                LoadingSection()
            }
        } else if (products.filterNotNull().isEmpty()) {
            item {
                EmptyStateSection()
            }
        } else {
            items(products.filterNotNull()) { product ->
                ProductCard(
                    product = product,
                    onView = {
                        val productId = product.productID?.toString() ?: ""
                        if (productId.isNotEmpty()) {
                            onViewProduct(productId)
                        } else {
                            Log.e("ProductCard", "Product ID is null or empty")
                        }
                    },
                    onEdit = {
                        val productId = product.productID?.toString() ?: ""
                        if (productId.isNotEmpty()) {
                            onEditProduct(productId)
                        } else {
                            Log.e("ProductCard", "Product ID is null or empty for edit")
                        }
                    },
                    onDelete = {
                        val productId = product.productID?.toString() ?: ""
                        if (productId.isNotEmpty()) {
                            onDeleteProduct(productId)
                        } else {
                            Log.e("ProductCard", "Product ID is null or empty for delete")
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun WelcomeCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = EcoGreen
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        shape = RoundedCornerShape(20.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "Welcome Back!",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Ready to make the planet greener?",
                    fontSize = 14.sp,
                    color = Color.White.copy(alpha = 0.9f)
                )
            }
            Text(
                text = "🌍",
                fontSize = 40.sp
            )
        }
    }
}

@Composable
fun ProductCard(
    product: ProductModel,
    onView: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize(),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        shape = RoundedCornerShape(16.dp)
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
                    Text(
                        text = product.productName ?: "Unknown Item",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = EcoGreenDark
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "₹",
                            fontSize = 14.sp,
                            color = EcoGreen,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "${product.price ?: 0}",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = EcoGreen
                        )
                        Text(
                            text = "/kg",
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = product.description ?: "No description available",
                        fontSize = 14.sp,
                        color = Color.Gray,
                        lineHeight = 20.sp,
                        maxLines = 2
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Action buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // View Button
                OutlinedButton(
                    onClick = {
                        try {
                            Log.d("ProductCard", "View button clicked for product: ${product.productID}")
                            onView()
                        } catch (e: Exception) {
                            Log.e("ProductCard", "Error in view button click", e)
                        }
                    },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = EcoGreen
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Face,
                        contentDescription = "View",
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("View", fontSize = 12.sp)
                }

                // Edit Button
                OutlinedButton(
                    onClick = {
                        try {
                            onEdit()
                        } catch (e: Exception) {
                            Log.e("ProductCard", "Error in edit button click", e)
                        }
                    },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color(0xFF1976D2)
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit",
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Edit", fontSize = 12.sp)
                }

                // Delete Button
                OutlinedButton(
                    onClick = {
                        try {
                            onDelete()
                        } catch (e: Exception) {
                            Log.e("ProductCard", "Error in delete button click", e)
                        }
                    },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color(0xFFD32F2F)
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete",
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Delete", fontSize = 12.sp)
                }
            }
        }
    }
}

// Rest of the functions remain the same...
@Composable
fun EmptyStateSection() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(40.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "📦",
                fontSize = 48.sp
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "No Recyclable Items Yet",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = EcoGreenDark
            )
            Text(
                text = "Tap the + button to add your first recyclable item",
                fontSize = 14.sp,
                color = Color.Gray,
                textAlign = TextAlign.Center
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
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
        Card(
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = onSearchQueryChange,
                label = { Text("Search recyclable items") },
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
                                contentDescription = "Clear"
                            )
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = EcoGreen,
                    focusedLabelColor = EcoGreen,
                    cursorColor = EcoGreen
                )
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        if (searchQuery.isEmpty()) {
            EmptySearchState()
        } else if (filteredProducts.isEmpty()) {
            NoResultsState(query = searchQuery)
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(filteredProducts) { product ->
                    SearchResultCard(
                        product = product,
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
fun SearchResultCard(
    product: ProductModel,
    onView: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = product.productName ?: "Unknown Product",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = EcoGreenDark
                )
                Text(
                    text = "₹${product.price ?: 0}",
                    fontSize = 14.sp,
                    color = EcoGreen,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = product.description ?: "No description",
                    fontSize = 12.sp,
                    color = Color.Gray,
                    maxLines = 1
                )
            }

            OutlinedButton(
                onClick = onView,
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = EcoGreen
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("View", fontSize = 12.sp)
            }
        }
    }
}

// Rest of the composables remain the same...
@Composable
fun StatsSection() {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(getStatsData()) { stat ->
            StatCard(stat = stat)
        }
    }
}

@Composable
fun StatCard(stat: StatData) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(90.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = stat.icon,
                fontSize = 24.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = stat.value,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = EcoGreenDark
            )
            Text(
                text = stat.label,
                fontSize = 10.sp,
                color = Color.Gray,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun LoadingSection() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(40.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CircularProgressIndicator(
                color = EcoGreen,
                strokeWidth = 3.dp,
                modifier = Modifier.size(40.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Loading recyclable items...",
                color = Color.Gray,
                fontSize = 14.sp
            )
        }
    }
}

@Composable
fun EmptySearchState() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "🔍",
            fontSize = 64.sp
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Search Recyclable Items",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = EcoGreenDark
        )
        Text(
            text = "Enter keywords to find items you can recycle",
            fontSize = 14.sp,
            color = Color.Gray,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun NoResultsState(query: String) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "😔",
            fontSize = 64.sp
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "No Results Found",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = EcoGreenDark
        )
        Text(
            text = "No items match \"$query\"",
            fontSize = 14.sp,
            color = Color.Gray,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun ProfileScreen() {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        item {
            ProfileHeader()
        }

        item {
            ProfileStats()
        }

        item {
            ProfileActions()
        }
    }
}

@Composable
fun ProfileHeader() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(EcoGreen),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "👤",
                    fontSize = 32.sp
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "EcoWarrior",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = EcoGreenDark
            )

            Text(
                text = "Making the world greener, one recycle at a time!",
                fontSize = 14.sp,
                color = Color.Gray,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun ProfileStats() {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(getProfileStats()) { stat ->
            StatCard(stat = stat)
        }
    }
}

@Composable
fun ProfileActions() {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Button(
            onClick = { /* Handle edit profile */ },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = EcoGreen
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Edit,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("Edit Profile", fontWeight = FontWeight.Bold)
        }

        OutlinedButton(
            onClick = { /* Handle view statistics */ },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = EcoGreen
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Icon(
                imageVector = Icons.Default.AccountBox,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("View Statistics", fontWeight = FontWeight.Bold)
        }
    }
}

// Data classes and utility functions
data class BottomNavItem(
    val label: String,
    val icon: ImageVector
)

data class StatData(
    val icon: String,
    val value: String,
    val label: String
)

fun getStatsData(): List<StatData> = listOf(
    StatData("📦", "66", "Total Items"),
    StatData("💰", "₹6,450", "Total Earnings"),
    StatData("🌱", "120 kg", "CO₂ Saved"),
    StatData("♻️", "66", "Items Recycled")
)

fun getProfileStats(): List<StatData> = listOf(
    StatData("⭐", "4.8", "Rating"),
    StatData("📈", "Level 5", "Eco Level"),
    StatData("🏆", "15", "Badges"),
    StatData("👥", "48", "Referrals")
)
