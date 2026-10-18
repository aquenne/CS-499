package com.example.inventorymanagerprolynch

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.inventorymanagerprolynch.ui.theme.InventoryManagerProTheme

class MainActivity : ComponentActivity() {
    private lateinit var dbHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Initialize the local SQLite database helper instance
        dbHelper = DatabaseHelper(this)
        enableEdgeToEdge()
        setContent {
            InventoryManagerProTheme {
                val context = LocalContext.current
                // State controlling screen navigation between login, inventory, and sms settings
                var currentScreen by remember { mutableStateOf("login") }
                // State holding the current list of inventory items pulled from the database
                var inventoryItems by remember { mutableStateOf(dbHelper.getAllItems()) }
                // State managing authentication status and error messaging
                var authMessage by remember { mutableStateOf("") }
                // State controlling whether the custom Add Item dialog is open
                var showAddDialog by remember { mutableStateOf(false) }
                // State tracking which item is currently selected for editing
                var itemToEdit by remember { mutableStateOf<InventoryItem?>(null) }

                // Screen router based on currentScreen state value
                when (currentScreen) {
                    "login" -> {
                        LoginScreen(
                            authMessage = authMessage,
                            onLoginClick = { user, pass ->
                                if (user.isBlank() || pass.isBlank()) {
                                    authMessage = "Please enter username and password."
                                } else if (dbHelper.checkUser(user, pass)) {
                                    authMessage = ""
                                    // Transition to inventory screen upon successful login
                                    currentScreen = "inventory"
                                } else {
                                    authMessage = "Invalid credentials or account doesn't exist."
                                }
                            },
                            onRegisterClick = { user, pass ->
                                if (user.isBlank() || pass.isBlank()) {
                                    authMessage = "Fields cannot be blank."
                                } else {
                                    val created = dbHelper.registerUser(user, pass)
                                    if (created) {
                                        authMessage = "Account created successfully! You can now log in."
                                    } else {
                                        authMessage = "Username already exists or error occurred."
                                    }
                                }
                            }
                        )
                    }
                    "inventory" -> {
                        InventoryScreen(
                            inventoryItems = inventoryItems,
                            onAddItemClick = {
                                // Open the input dialog instead of inserting hardcoded data
                                showAddDialog = true
                            },
                            onEditItemClick = { clickedItem ->
                                // Capture the clicked item to open the edit dialog
                                itemToEdit = clickedItem
                            },
                            onDeleteItemClick = { itemToDelete ->
                                // Delete the selected item from database and refresh list state
                                dbHelper.deleteItem(itemToDelete.id)
                                inventoryItems = dbHelper.getAllItems()
                            },
                            onNavigateToSmsSettings = {
                                // Navigate to the SMS notification settings view
                                currentScreen = "sms_settings"
                            }
                        )

                        // Dialog popup allowing custom item name, quantity, and price entry
                        if (showAddDialog) {
                            var nameInput by remember { mutableStateOf("") }
                            var quantityInput by remember { mutableStateOf("") }
                            var priceInput by remember { mutableStateOf("") }
                            var inputError by remember { mutableStateOf("") }

                            AlertDialog(
                                onDismissRequest = { showAddDialog = false },
                                title = { Text("Add New Inventory Item") },
                                text = {
                                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                        if (inputError.isNotBlank()) {
                                            Text(text = inputError, color = MaterialTheme.colorScheme.error)
                                        }
                                        OutlinedTextField(
                                            value = nameInput,
                                            onValueChange = { nameInput = it },
                                            label = { Text("Item Name") },
                                            singleLine = true
                                        )
                                        OutlinedTextField(
                                            value = quantityInput,
                                            onValueChange = { quantityInput = it },
                                            label = { Text("Quantity") },
                                            singleLine = true
                                        )
                                        OutlinedTextField(
                                            value = priceInput,
                                            onValueChange = { priceInput = it },
                                            label = { Text("Price ($)") },
                                            singleLine = true
                                        )
                                    }
                                },
                                confirmButton = {
                                    Button(onClick = {
                                        val qty = quantityInput.toIntOrNull()
                                        val prc = priceInput.toDoubleOrNull()
                                        if (nameInput.isBlank() || qty == null || prc == null) {
                                            inputError = "Please fill out all fields with valid numbers."
                                        } else {
                                            // Insert custom item into the SQLite database
                                            val isAdded = dbHelper.addItem(nameInput.trim(), qty, prc)
                                            if (isAdded) {
                                                // Trigger low-inventory SMS notification if stock is 3 or below
                                                if (qty <= 3) {
                                                    SmsNotificationManager.sendLowInventoryAlert(
                                                        context = context,
                                                        phoneNumber = "5551234567",
                                                        itemName = nameInput.trim(),
                                                        stockLevel = qty
                                                    )
                                                }
                                                // Refresh list and close dialog
                                                inventoryItems = dbHelper.getAllItems()
                                                showAddDialog = false
                                            } else {
                                                inputError = "Database error adding item."
                                            }
                                        }
                                    }) {
                                        Text("Save Item")
                                    }
                                },
                                dismissButton = {
                                    TextButton(onClick = { showAddDialog = false }) {
                                        Text("Cancel")
                                    }
                                }
                            )
                        }

                    // Dialog popup allowing modification of an existing item
                    itemToEdit?.let { currentItem ->
                        var nameInput by remember { mutableStateOf(currentItem.name) }
                        var quantityInput by remember { mutableStateOf(currentItem.quantity.toString()) }
                        var priceInput by remember { mutableStateOf(currentItem.price.toString()) }
                        var inputError by remember { mutableStateOf("") }

                        AlertDialog(
                            onDismissRequest = { itemToEdit = null },
                            title = { Text("Edit Inventory Item") },
                            text = {
                                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    if (inputError.isNotBlank()) {
                                        Text(text = inputError, color = MaterialTheme.colorScheme.error)
                                    }
                                    OutlinedTextField(
                                        value = nameInput,
                                        onValueChange = { nameInput = it },
                                        label = { Text("Item Name") },
                                        singleLine = true
                                    )
                                    OutlinedTextField(
                                        value = quantityInput,
                                        onValueChange = { quantityInput = it },
                                        label = { Text("Quantity") },
                                        singleLine = true
                                    )
                                    OutlinedTextField(
                                        value = priceInput,
                                        onValueChange = { priceInput = it },
                                        label = { Text("Price ($)") },
                                        singleLine = true
                                    )
                                }
                            },
                            confirmButton = {
                                Button(onClick = {
                                    val qty = quantityInput.toIntOrNull()
                                    val prc = priceInput.toDoubleOrNull()
                                    if (nameInput.isBlank() || qty == null || prc == null) {
                                        inputError = "Please fill out all fields with valid numbers."
                                    } else {
                                        dbHelper.updateItem(currentItem.id, nameInput.trim(), qty, prc)
                                        inventoryItems = dbHelper.getAllItems()
                                        itemToEdit = null
                                    }
                                }) {
                                    Text("Update Item")
                                }
                            },
                            dismissButton = {
                                TextButton(onClick = { itemToEdit = null }) {
                                    Text("Cancel")
                                }
                            }
                        )
                    }
                }
                    "sms_settings" -> {
                        SmsSettingsScreen(
                            onBackClick = {
                                // Return back to the inventory screen
                                currentScreen = "inventory"
                            }
                        )
                    }
                }
            }
        }
    }
}

// Composable screen handling user login and registration forms
@Composable
fun LoginScreen(
    modifier: Modifier = Modifier,
    authMessage: String = "",
    onLoginClick: (String, String) -> Unit = { _, _ -> },
    onRegisterClick: (String, String) -> Unit = { _, _ -> }
) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.0.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // App name text header
            Text(
                text = "Inventory Manager Pro",
                fontSize = 24.sp,
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(bottom = 24.0.dp)
            )

            // Conditional display card for authentication feedback messages
            if (authMessage.isNotBlank()) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
                ) {
                    Text(
                        text = authMessage,
                        color = MaterialTheme.colorScheme.onTertiaryContainer,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }

            // Username input text field
            OutlinedTextField(
                value = username,
                onValueChange = { username = it },
                label = { Text("Username") },
                singleLine = true,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.0.dp)
            )

            // Password input text field with hidden characters
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                singleLine = true,
                shape = RoundedCornerShape(16.dp),
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.0.dp)
            )

            // Button to submit login credentials
            Button(
                onClick = { onLoginClick(username, password) },
                shape = RoundedCornerShape(16.dp),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.0.dp)
                    .padding(bottom = 12.0.dp)
            ) {
                Text("Log In")
            }

            // Button to create a new user account
            TextButton(
                onClick = { onRegisterClick(username, password) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.0.dp)
            ) {
                Text("Create New Account")
            }
        }
    }
}

// UI Preview for the Login View
@Preview(showBackground = true, name = "Login View")
@Composable
fun LoginPreview() {
    InventoryManagerProTheme {
        LoginScreen()
    }
}

// UI Preview for the Inventory View
@Preview(showBackground = true, name = "Inventory View")
@Composable
fun InventoryPreview() {
    InventoryManagerProTheme {
        InventoryScreen(
            inventoryItems = listOf(
                InventoryItem(id = 1, name = "Sample Crystal", quantity = 5, price = 25.00)
            ),
            onAddItemClick = {},
            onEditItemClick = {},
            onDeleteItemClick = {},
            onNavigateToSmsSettings = {}
        )
    }
}

// UI Preview for the SMS Settings View
@Preview(showBackground = true, name = "SMS Settings View")
@Composable
fun SmsSettingsPreview() {
    InventoryManagerProTheme {
        SmsSettingsScreen()
    }
}