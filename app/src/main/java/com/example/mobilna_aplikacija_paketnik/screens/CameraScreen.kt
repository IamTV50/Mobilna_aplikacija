package com.example.mobilna_aplikacija_paketnik.screens

import android.content.Intent
import android.util.Log
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.mobilna_aplikacija_paketnik.OpenBox.OpenBoxRequest
import com.example.mobilna_aplikacija_paketnik.OpenBox.OpenBoxResponse
import com.example.mobilna_aplikacija_paketnik.OpenBox.OpenInterface
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import android.util.Base64

@Composable
fun CameraScreen(navController: NavController,OpenInter:OpenInterface) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    val qrCodeValue = remember { mutableStateOf("") }
    val response = remember { mutableStateOf("") }

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
                coroutineScope.launch{
                    println("NEKAJ TU NOT ")
                    try{
                        val openBoxRequest = OpenBoxRequest(
                            deliveryId = 0,
                            boxId = 540,
                            tokenFormat = 5,
                            latitude = 0.0,
                            longitude = 0.0,
                            qrCodeInfo = "string",
                            terminalSeed = 0,
                            isMultibox = false,
                            doorIndex = 0,
                            addAccessLog = true
                        )
                        val openBoxResponse = OpenInter.openBox(openBoxRequest)
                        response.value = openBoxResponse.result.toString()
                        print("Response: ${openBoxResponse.data}")
                        println("ErrorNum:${openBoxResponse.errorNumber}")
                        val decodedBytes = Base64.decode(openBoxResponse.data,Base64.DEFAULT)
                        val decodedString = String(decodedBytes)
                        print("Dekodiran zeton: $decodedString")
                    }catch (E:Exception){
                        println("Napaka v klicu API-ja" + E.message)
                    }
                }
            }
        ) {
            Text(text = "Scan QR Code")
        }

        Text(
            text = "QR Code Value: ${qrCodeValue.value}",
            modifier = Modifier.padding(top = 16.dp)
        )
        Text(
            text = "API response: ${response.value}",
            modifier = Modifier.padding(top = 16.dp)
        )
    }
}

