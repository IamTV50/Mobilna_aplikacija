package com.example.mobilna_aplikacija_paketnik.screens

import LogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.media.MediaPlayer
import android.os.Build
import android.util.Base64
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.navigation.NavController
import com.example.mobilna_aplikacija_paketnik.API.Box.BoxInterface
import com.example.mobilna_aplikacija_paketnik.API.Log.LogRequest
import com.example.mobilna_aplikacija_paketnik.API.Log.LogResponse
import com.example.mobilna_aplikacija_paketnik.API.OpenBox.OpenBoxRequest
import com.example.mobilna_aplikacija_paketnik.API.OpenBox.OpenInterface
import com.example.mobilna_aplikacija_paketnik.R
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.time.LocalDate
import java.time.ZoneId
import java.util.Date

import kotlin.random.Random

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CameraScreen(
    navController: NavController,
    OpenInter: OpenInterface,
    logInter: LogInterface,
    sharedPreferences: SharedPreferences,
    boxInter: BoxInterface
) {

    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val opend = LocalDate.now()
    val qrCodeValue = remember { mutableStateOf("") }
    val response = remember { mutableStateOf("") }
    val mediaPlayer = remember { MediaPlayer() }
    val userId = remember { mutableStateOf("") }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val qrCode = result.data?.getStringExtra("SCAN_RESULT")
        qrCode?.let {

            qrCodeValue.value = it
            val iskandel = it.split("/")
            val boxId = iskandel.getOrNull(2)?.toIntOrNull() ?: 0
            sharedPreferences.edit().putInt("boxId", 0).apply()
            var GetBoxResponse2 = ""
            val GetBoxResponseDeferred = CompletableDeferred<String>()

            coroutineScope.launch {
                try {
                    val GetBoxResponse = boxInter.getBox("646f3c9c69a3a1f7b9a1216a")
                    GetBoxResponseDeferred.complete(GetBoxResponse.body()?.userIds.toString())
                    println("Boxi " + GetBoxResponse.body()?.userIds.toString())
                } catch (E: Exception) {
                    println("Napaka v pridobivanju box-a " + E.message)
                    GetBoxResponseDeferred.completeExceptionally(E)
                }
            }

            coroutineScope.launch {
                try {
                    val GetBoxResponse = GetBoxResponseDeferred.await()
                    println("GetBoxResponse: $GetBoxResponse")
                    // Use the GetBoxResponse value as needed
                } catch (e: Exception) {
                    println("Error retrieving GetBoxResponse: ${e.message}")
                    // Handle the error case appropriately
                }
            }

            //println("GetBoxResponse: $GetBoxResponse")
            val userIDSP = sharedPreferences.getString("_id", "")
            userId.value = userIDSP ?: ""

            coroutineScope.launch {
                val GetBoxResponse = GetBoxResponseDeferred.await()

                println("NEKAJ TU NOT ")
                if (userId.value in GetBoxResponse) {
                    try {
                        val openBoxRequest = OpenBoxRequest(
                            deliveryId = 0,
                            boxId = boxId,
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


                        val decodedBytes = Base64.decode(openBoxResponse.data, Base64.DEFAULT)
                        val decodedString = String(decodedBytes)
                        val tempFile =
                            withContext(Dispatchers.IO) {
                                File.createTempFile("temp", ".mp3", context.cacheDir)
                            }

                        withContext(Dispatchers.IO) {
                            FileOutputStream(tempFile).use { outputStream ->
                                outputStream.write(decodedBytes)
                            }

                            mediaPlayer.apply {
                                reset()
                                setDataSource(tempFile.absolutePath)
                                prepare()
                                start()
                            }

                        }
                        //print("Dekodiran zeton: $decodedString")
                    } catch (E: Exception) {
                        println("Napaka v klicu API-ja" + E.message)
                    }


                    try {
                        val user = sharedPreferences.getString("username", "") ?: ""

                        val logRequest = LogRequest(
                            user,
                            Date.from(opend.atStartOfDay(ZoneId.systemDefault()).toInstant()),
                            boxId,
                            false
                        )

                        val UID = "646f3b6e69a3a1f7b9a12152"
                        val logResponse = logInter.sendLog(logRequest, UID)
                    } catch (E: Exception) {
                        println("Napaka pri posiljanju log ")
                    }
                } else {
                    Toast.makeText(
                        context,
                        "You're not authorized to open this parcel",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }


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
        Text(
            text = "API response: ${response.value}",
            modifier = Modifier.padding(top = 16.dp)
        )

    }
}

