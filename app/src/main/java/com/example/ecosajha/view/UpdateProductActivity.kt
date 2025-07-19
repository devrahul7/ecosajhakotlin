package com.example.ecosajha.view

import android.app.Activity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ecosajha.repository.ProductRepositoryImpl
import com.example.ecosajha.viewmodel.ProductViewModel

// Define custom green color scheme for EcoSajha
private val EcoGreen = Color(0xFF4CAF50)
private val EcoGreenDark = Color(0xFF388E3C)
private val EcoGreenLight = Color(0xFFC8E6C9)
private val EcoBackground = Color(0xFFF1F8E9)

class UpdateProductActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            UpdateProductScreen()
        }
    }
}

@Composable
fun UpdateProductScreen() {
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
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
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
                        text = "Recyclable Item ID not found",
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

    UpdateProductBody(productID = productID)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UpdateProductBody(productID: String) {
    // State for form fields
    var recyclableItemName by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(true) }
    var isUpdating by remember { mutableStateOf(false) }

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

    // Update form fields when product data is loaded
    LaunchedEffect(product.value) {
        product.value?.let { productData ->
            recyclableItemName = productData.productName ?: ""
            description = productData.description ?: ""
            price = productData.price?.toString() ?: ""
            isLoading = false
        }
    }

    Scaffold(
        containerColor = EcoBackground,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Update Recyclable Item",
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
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
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
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
                            text = "Loading recyclable item details...",
                            style = MaterialTheme.typography.bodyLarge,
                            color = EcoGreenDark,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        } else {
            // Main content
            LazyColumn(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                item {
                    // Header Card
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Update",
                                tint = EcoGreen,
                                modifier = Modifier.size(32.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Update your recyclable item details",
                                style = MaterialTheme.typography.bodyMedium,
                                color = EcoGreenDark,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }

                item {
                    // Form Fields Card
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            // Recyclable Item Name Field
                            OutlinedTextField(
                                value = recyclableItemName,
                                onValueChange = { recyclableItemName = it },
                                label = { Text("Recyclable Item Name") },
                                placeholder = { Text("e.g., Plastic Bottles, Paper, Metal Cans") },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.Person,
                                        contentDescription = "Item Name",
                                        tint = EcoGreen
                                    )
                                },
                                modifier = Modifier.fillMaxWidth(),
                                enabled = !isUpdating,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = EcoGreen,
                                    focusedLabelColor = EcoGreen,
                                    cursorColor = EcoGreen
                                ),
                                shape = RoundedCornerShape(12.dp)
                            )

                            // Price Field
                            OutlinedTextField(
                                value = price,
                                onValueChange = { price = it },
                                label = { Text("Price (₹)") },
                                placeholder = { Text("Enter price per unit/kg") },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.Star,
                                        contentDescription = "Price",
                                        tint = EcoGreen
                                    )
                                },
                                modifier = Modifier.fillMaxWidth(),
                                enabled = !isUpdating,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = EcoGreen,
                                    focusedLabelColor = EcoGreen,
                                    cursorColor = EcoGreen
                                ),
                                shape = RoundedCornerShape(12.dp)
                            )

                            // Description Field
                            OutlinedTextField(
                                value = description,
                                onValueChange = { description = it },
                                label = { Text("Description") },
                                placeholder = { Text("Describe condition, quantity, pickup details...") },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.Info,
                                        contentDescription = "Description",
                                        tint = EcoGreen
                                    )
                                },
                                modifier = Modifier.fillMaxWidth(),
                                minLines = 4,
                                enabled = !isUpdating,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = EcoGreen,
                                    focusedLabelColor = EcoGreen,
                                    cursorColor = EcoGreen
                                ),
                                shape = RoundedCornerShape(12.dp)
                            )
                        }
                    }
                }

                item {
                    // Update Button
                    Button(
                        onClick = {
                            // Validate inputs
                            when {
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
                                        if (priceValue <= 0) {
                                            Toast.makeText(
                                                context,
                                                "Price must be greater than 0",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                            return@Button
                                        }

                                        isUpdating = true

                                        val updateData = mutableMapOf<String, Any?>().apply {
                                            put("description", description)
                                            put("price", priceValue)
                                            put("productName", recyclableItemName)
                                            put("productID", productID)
                                        }

                                        viewModel.updateProduct(
                                            productID,
                                            updateData
                                        ) { success, message ->
                                            isUpdating = false
                                            if (success) {
                                                Toast.makeText(
                                                    context,
                                                    "Recyclable item updated successfully",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                                activity?.finish()
                                            } else {
                                                Toast.makeText(
                                                    context,
                                                    message ?: "Failed to update recyclable item",
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
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        enabled = !isUpdating,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = EcoGreen,
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(12.dp),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 6.dp)
                    ) {
                        if (isUpdating) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                        } else {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Update",
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                        }
                        Text(
                            if (isUpdating) "Updating..." else "Update Recyclable Item",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
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

@Preview(showBackground = true)
@Composable
fun UpdateProductPreview() {
    UpdateProductBody(productID = "sample_id")
}
