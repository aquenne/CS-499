package com.example.inventorymanagerprolynch

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InventoryScreen(
    inventoryItems: List<InventoryItem>,
    onAddItemClick: () -> Unit,
    onEditItemClick: (InventoryItem) -> Unit,
    onDeleteItemClick: (InventoryItem) -> Unit,
    onNavigateToSmsSettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Scaffold(
            containerColor = MaterialTheme.colorScheme.background,
            topBar = {
                // Top navigation bar containing the company name, SMS settings trigger, and add button
                TopAppBar(
                    title = { Text("Inventory Manager Pro", maxLines = 1) }, // Shortened slightly to prevent crowding
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.background
                    ),
                    actions = {
                        // Button to navigate to SMS permission
                        TextButton(onClick = onNavigateToSmsSettings) {
                            Text("SMS") // Compact label to stop overlapping
                        }
                        // Button to trigger adding a new stock item
                        IconButton(onClick = onAddItemClick) {
                            Icon(Icons.Default.Add, contentDescription = "Add Item")
                        }
                    }
                )
            }
        ) { innerPadding ->
            // Main content layout with the company name and list of inventory
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.0.dp)
            ) {
                Text(
                    text = "Current Stock Items",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(bottom = 16.0.dp)
                )

                if (inventoryItems.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("No inventory items found. Tap '+' to add one.")
                    }
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(8.0.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(inventoryItems) { item ->
                            InventoryCard(item = item, onClick = { onEditItemClick(item) }, onDelete = { onDeleteItemClick(item) })
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun InventoryCard(item: InventoryItem, onClick: () -> Unit, onDelete: () -> Unit) {
    Card(
        onClick = onClick,
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.0.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Display item details (Name, Quantity, Price)
            Column(modifier = Modifier.weight(1f)) {
                Text(text = item.name, style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(4.0.dp))
                Text(text = "Quantity: ${item.quantity}", style = MaterialTheme.typography.bodyMedium)
                Text(text = "Price: $${String.format("%.2f", item.price)}", style = MaterialTheme.typography.bodyMedium)
            }
            // Delete action button mapped to remove the item from the database
            IconButton(onClick = onDelete) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete Item",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

// Data class representing a single inventory item record
data class InventoryItem(
    val id: Int,
    val name: String,
    val quantity: Int,
    val price: Double
)