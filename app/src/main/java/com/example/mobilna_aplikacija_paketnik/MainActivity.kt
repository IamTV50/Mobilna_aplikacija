package com.example.mobilna_aplikacija_paketnik

import LogInterface
import LogScreen
import LoginScreen
import RegisterScreen
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.mobilna_aplikacija_paketnik.API.Box.BoxInterface
import com.example.mobilna_aplikacija_paketnik.API.Log.LogRequest
import com.example.mobilna_aplikacija_paketnik.API.Login.LoginInterface
import com.example.mobilna_aplikacija_paketnik.API.Register.RegisterInterFace
import com.example.mobilna_aplikacija_paketnik.API.OpenBox.OpenInterface
import com.example.mobilna_aplikacija_paketnik.screens.CameraScreen
import com.example.mobilna_aplikacija_paketnik.screens.HomeScreen
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.bson.types.ObjectId
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.Date
import kotlin.random.Random
//const val objectIdString = "646f5cd475cc0d88d20dcfce";
//val objectId = ObjectId(objectIdString);
@ExperimentalMaterial3Api
class MainActivity : ComponentActivity() {
    private lateinit var sharedPreferences: SharedPreferences

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        sharedPreferences = getSharedPreferences("MyAppPreferences", Context.MODE_PRIVATE)

        val scope = CoroutineScope(Dispatchers.Main)
        super.onCreate(savedInstanceState)
        val retrofit = Retrofit.Builder()
            .baseUrl("http://10.0.2.2:3001/") // Replace with your API base URL
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val retrofitAPI = Retrofit.Builder()
            .baseUrl("https://api-d4me-stage.direct4.me/sandbox/v1/") // Replace with your API base URL
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val loginInter = retrofit.create(LoginInterface::class.java)
        val registerInter = retrofit.create(RegisterInterFace::class.java)
        val logInter = retrofit.create(LogInterface::class.java)
        val boxInter = retrofit.create(BoxInterface::class.java)
        val openInter = retrofitAPI.create(OpenInterface::class.java)

        val UID = "646f3b6e69a3a1f7b9a12152"
        val threshold = 10
        val userIds = listOf("dolfa", "fico", "leo", "test", "user5")
        val random = Random(System.currentTimeMillis())

        scope.launch {
            fun getRandomUser(): String {
                val randomIndex = random.nextInt(userIds.size)
                return userIds[randomIndex]
            }

            while (true) {
                val vibrations = random.nextInt(1, 11)
                println("Random number: $vibrations")

                if (vibrations > threshold && random.nextDouble() < 0.1) {
                    // The vibrations number is higher than the threshold and the random chance condition is met
                    val opend = LocalDateTime.now()
                    val logRequest = LogRequest(
                        getRandomUser(),
                        Date.from(opend.atZone(ZoneId.systemDefault()).toInstant()),
                        Random.nextInt(1, 999),
                        true
                    )
                    try {
                        val logResponse = logInter.sendLog(logRequest, UID)
                        println("POST request successful")
                        createPushNotification(context = this@MainActivity)
                    } catch (e: Exception) {
                        println("POST request failed: ${e.message} ")
                    }
                }

                // Delay for 5 minutes
                delay(1 * 60 * 1000)
            }
        }

            setContent {
                val navController = rememberNavController()

                NavHost(navController, startDestination = "register") {
                    composable("home") {
                        HomeScreen(navController)
                    }
                    composable("camera") {
                        CameraScreen(
                            navController,
                            openInter,
                            logInter,
                            sharedPreferences,
                            boxInter
                        )
                    }
                    composable("login") {
                        LoginScreen(loginInter, navController = navController, this@MainActivity)
                    }
                    composable("register") {
                        RegisterScreen(registerInter, navController)
                    }
                    composable("logs") {
                        LogScreen(navController, logInter, sharedPreferences)
                    }
                }
            }


    }

    fun createPushNotification(context: Context) {
        val channelId = "my_channel_id"
        val notificationId = 1

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "My Channel",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }

        val builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(androidx.core.R.drawable.notification_bg)
            .setContentTitle("Warning!")
            .setContentText("Someone tried to force the parcel")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)

        with(NotificationManagerCompat.from(context)) {
            notify(notificationId, builder.build())
        }
    }
}