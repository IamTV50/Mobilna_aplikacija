package com.example.mobilna_aplikacija_paketnik.screens

import android.graphics.Bitmap
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.launch
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.mobilna_aplikacija_paketnik.API.Register.RegisterInterFace
import com.example.mobilna_aplikacija_paketnik.API.Register.RegisterRequest
import kotlinx.coroutines.launch

@ExperimentalMaterial3Api
@Composable
fun RegisterScreen(registerInter: RegisterInterFace, navController: NavController) {
    val username = remember { mutableStateOf("") }
    val password = remember { mutableStateOf("") }
    val gmail = remember { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()

    Column(modifier = Modifier.padding(16.dp)) {
        TextField(
            value = username.value,
            onValueChange = { newValue -> username.value = newValue },
            modifier = Modifier.padding(bottom = 8.dp)
        )

        TextField(
            value = password.value,
            onValueChange = { newValue -> password.value = newValue },
            modifier = Modifier.padding(bottom = 8.dp)
        )

        TextField(
            value = gmail.value,
            onValueChange = { newValue -> gmail.value = newValue },
            modifier = Modifier.padding(bottom = 8.dp)
        )

        val capturedImages = remember { mutableStateListOf<Bitmap>() }
        val takePictureLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.TakePicturePreview()
        ) { bitmap ->
            if (bitmap != null) {
                capturedImages.add(bitmap)
            }
        }

        Button(
            onClick = {
                takePictureLauncher.launch()
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.Outlined.Add, contentDescription = "Open Camera")
            Text("Open Camera")
        }

        Button(
            onClick = {
                val registerRequest = RegisterRequest(username.value,gmail.value,password.value)
                coroutineScope.launch {
                    try {
                        val registerResponse = registerInter.register(registerRequest)
                        println("Register successful: ${registerResponse.username}")
                        navController.navigate("home")
                    } catch (t: Throwable) {
                        println("Register failed: ${t.message}")
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Register")
        }
    }
}