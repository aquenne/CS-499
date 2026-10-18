package com.example.inventorymanagerprolynch

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.telephony.SmsManager
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat

object SmsNotificationManager {
    // Object manager responsible for handling automated SMS notifications when stock is low
    fun sendLowInventoryAlert(context: Context, phoneNumber: String, itemName: String, stockLevel: Int) {
        // Verify if the user has granted the SEND_SMS permission
        val permissionCheck = ContextCompat.checkSelfPermission(context, Manifest.permission.SEND_SMS)
        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
            try {
                // Access the system's SmsManager to transmit the text message
                val smsManager = context.getSystemService(SmsManager::class.java)
                val message = "Low Inventory Alert: '$itemName' is down to $stockLevel units!"
                smsManager.sendTextMessage(phoneNumber, null, message, null, null)
                Toast.makeText(context, "Low stock SMS alert sent!", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                // Catch any unexpected exceptions during transmission and display an error toast
                Toast.makeText(context, "Failed to send SMS: ${e.message}", Toast.LENGTH_LONG).show()
            }
        } else {
            // Handle missing permission without crashing the app
            Toast.makeText(context, "SMS Permission not granted. Alert skipped.", Toast.LENGTH_SHORT).show()
        }
    }
}

@Composable
fun SmsSettingsScreen(
    onBackClick: () -> Unit = {}
) {
    val context = LocalContext.current
    // State to track whether the SEND_SMS permission is currently granted
    var hasPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.SEND_SMS
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    // Activity result launcher to handle the runtime permission request dialog flow
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasPermission = isGranted
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "SMS Notification Settings",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(bottom = 16.dp),
                textAlign = TextAlign.Center
            )

            // Text detailing the permission feature
            Text(
                text = "Enable SMS alerts to receive automated low-inventory warnings. If disabled, the app will continue to operate normally without notifications.",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(bottom = 32.dp),
                textAlign = TextAlign.Center
            )

            // Dynamic status message reflecting whether permission is active or denied/not set
            val statusText = if (hasPermission) {
                "Status: SMS Notifications Granted ✓"
            } else {
                "Status: SMS Notifications Denied / Not Set ✕"
            }

            // Status displayed as plain clean text instead of a button/card
            Text(
                text = statusText,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 32.dp),
                textAlign = TextAlign.Center
            )
            // SMS Request button
            Button(
                onClick = {
                    permissionLauncher.launch(Manifest.permission.SEND_SMS)
                },
                shape = RoundedCornerShape(16.dp),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .padding(bottom = 12.dp)
            ) {
                Text("Request SMS Permission")
            }

            // Inventory back button
            Button(
                onClick = onBackClick,
                shape = RoundedCornerShape(16.dp),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .padding(bottom = 12.dp)
            ) {
                Text("Back to Inventory")
            }
        }
    }
}