package com.example.mobilna_aplikacija_paketnik

import android.content.Intent
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun CameraScreen(navController: NavController) {
    val context = LocalContext.current

    val qrCodeValue = remember { mutableStateOf("") }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val qrCode = result.data?.getStringExtra("SCAN_RESULT")
        qrCode?.let {
            qrCodeValue.value = it
        }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(
            onClick = {
                val intent = Intent("com.google.zxing.client.android.SCAN")
                launcher.launch(intent)
            }
        ) {
            Text(text = "Scan QR Code")
        }

        Text(
            text = "QR Code Value: ${qrCodeValue.value}",
            modifier = Modifier.padding(top = 16.dp)
        )
    }
}

