package com.example.mobilna_aplikacija_paketnik.screens

import LogInterface
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.media.MediaPlayer
import android.os.Build
import android.util.Base64
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
fun CameraScreen(navController: NavController,OpenInter: OpenInterface, logInter:LogInterface,sharedPreferences: SharedPreferences,boxInter:BoxInterface) {

    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val opend = LocalDate.now()
    val qrCodeValue = remember { mutableStateOf("") }
    val response = remember { mutableStateOf("") }
    val GetLogResponse = remember{ mutableStateOf("") }
    val mediaPlayer = remember { MediaPlayer() }
    val GetBoxResponseList = remember { mutableStateOf("") }
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
            coroutineScope.launch {
                try {
                    var GetBoxResponse = boxInter.getBox("646f3c9c69a3a1f7b9a1216a")
                    GetBoxResponseList.value= GetBoxResponse.body()?.userIds.toString()
                    println("Boxi "+GetBoxResponse.body()?.userIds.toString())
                } catch (E: Exception) {
                    println("Napaka v pridobivanju box-a " + E.message)
                }
            }
            val userIDSP=sharedPreferences.getString("_id","")
            userId.value = userIDSP ?: ""
            println("USER:"+userId.value)
            if(GetBoxResponseList.value.contains(userId.value)){
            coroutineScope.launch {
                println("NEKAJ TU NOT ")
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
                    createPushNotification(context)
                } catch (E: Exception) {
                    println("Napaka pri posiljanju log ")
                }
            }
        }else{
            println("Uporabnik ni avtoriziran za odklep ")

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
        Button(onClick = {
            val boxid = sharedPreferences.getInt("boxId", 0)
            val user = sharedPreferences.getString("username", "") ?: ""
            coroutineScope.launch {
                val getlogRequest = LogRequest(
                    user,
                    Date.from(opend.atStartOfDay(ZoneId.systemDefault()).toInstant()),
                    boxid,
                    false
                )

                var getLogResponse = logInter.getLogs(user)
                GetLogResponse.value = getLogResponse.toString()

            }


        }

        ) {
            Text("Get Logs")
        }
        Text("Logs for user: "+GetLogResponse.value)

    }
}

fun createPushNotification(context: Context)
{
    val channelId = "my_channel_id" // Unique ID for the notification channel
    val notificationId = 1 // Unique ID for the notification

    // Create a notification channel for devices running Android Oreo and above
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val channel = NotificationChannel(
            channelId,
            "My Channel",
            NotificationManager.IMPORTANCE_DEFAULT
        )
        // Set additional properties for the channel (e.g., description, importance, sound, etc.)
        // Customize the channel based on your requirements
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    // Create the notification builder
    val builder = NotificationCompat.Builder(context, channelId)
        .setSmallIcon(androidx.core.R.drawable.notification_bg)
        .setContentTitle("New Log")
        .setContentText("A new log was sent")
        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        .setAutoCancel(true)

    // Add any additional configurations to the notification builder (e.g., actions, intent, etc.)
    // Customize the notification based on your requirements

    // Show the notification
    with(NotificationManagerCompat.from(context)) {
        notify(notificationId, builder.build())
    }
}

