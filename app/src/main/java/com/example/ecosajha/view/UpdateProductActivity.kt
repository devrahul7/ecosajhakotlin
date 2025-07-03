package com.example.ecosajha.view

import android.app.Activity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.ecosajha.repository.ProductRepositoryImpl
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import com.example.ecosajha.viewmodel.ProductViewModel

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
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "Product ID not found",
                    style = MaterialTheme.typography.bodyLarge
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = { activity?.finish() }
                ) {
                    Text("Go Back")
                }
            }
        }
        return
    }

    UpdateProductBody(productID = productID)
}

@Composable
fun UpdateProductBody(productID: String) {
    // State for form fields
    var productName by remember { mutableStateOf("") }
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
            productName = productData.productName ?: ""
            description = productData.description ?: ""
            price = productData.price?.toString() ?: ""
            isLoading = false
        }
    }

    Scaffold { innerPadding ->
        if (isLoading) {
            // Loading state
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator()
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Loading product details...")
                }
            }
        } else {
            // Main content
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp)
            ) {
                item {
                    // Product Name Field
                    OutlinedTextField(
                        value = productName,
                        onValueChange = { productName = it },
                        placeholder = { Text("Enter product name") },
                        label = { Text("Product Name") },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !isUpdating
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Price Field
                    OutlinedTextField(
                        value = price,
                        onValueChange = { price = it },
                        placeholder = { Text("Enter price") },
                        label = { Text("Price") },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !isUpdating
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Description Field
                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        placeholder = { Text("Enter Description") },
                        label = { Text("Description") },
                        minLines = 3,
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !isUpdating
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Update Button
                    Button(
                        onClick = {
                            // Validate inputs
                            when {
                                productName.isBlank() -> {
                                    Toast.makeText(
                                        context,
                                        "Please enter product name",
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
                                            put("productName", productName)
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
                                                    "Product updated successfully",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                                activity?.finish()
                                            } else {
                                                Toast.makeText(
                                                    context,
                                                    message ?: "Failed to update product",
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
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !isUpdating
                    ) {
                        if (isUpdating) {
                            CircularProgressIndicator(
                                modifier = Modifier.padding(end = 8.dp)
                            )
                        }
                        Text(if (isUpdating) "Updating..." else "Update Product")
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun UpdateProductPreview() {
    UpdateProductBody(productID = "sample_id")
}