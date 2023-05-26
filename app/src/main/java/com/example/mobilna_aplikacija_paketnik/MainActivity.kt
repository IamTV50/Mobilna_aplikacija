package com.example.mobilna_aplikacija_paketnik

import LogInterface
import LoginScreen
import RegisterScreen
import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
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
         val scope = CoroutineScope(Dispatchers.Main)
        super.onCreate(savedInstanceState)
        val retrofit = Retrofit.Builder()
            .baseUrl("http://192.168.0.44:3001/") // Replace with your API base URL
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val loginInter = retrofit.create(LoginInterface::class.java)
        val registerInter  = retrofit.create(RegisterInterFace::class.java)
        
        val retrofitAPI = Retrofit.Builder()
            .baseUrl("https://api-d4me-stage.direct4.me/sandbox/v1/") // Replace with your API base URL
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val openInter=retrofitAPI.create(OpenInterface::class.java)
        val logInter=retrofit.create(LogInterface::class.java)

          val threshold = 10
        sharedPreferences = getSharedPreferences("MyAppPreferences", Context.MODE_PRIVATE)

        // Access the user_id from shared preferences
        //val userId = sharedPreferences.getString("user_id", "")

        //val UID=ObjectId(userId)
        val UID="646f3b6e69a3a1f7b9a12152"
        scope.launch {

                val vibrations = 20
                println("Random number: $vibrations")
                val opend=LocalDate.now()

                if (vibrations > threshold) {
                    //val loginRequest = LoginRequest("username", "password") // Replace with your actual login credentials
                    val logRequest=LogRequest("dolfa", Date.from(opend.atStartOfDay(ZoneId.systemDefault()).toInstant()),Random.nextInt(1,999),true)
                    try {
                        val logResponse = logInter.sendLog(logRequest, UID)


                        println("POST request successful")
                    } catch (e: Exception) {
                        println("POST request failed: ${e.message} ")
                    }
                }

                delay(1 * 100 * 1) // Delay for 5 minutes

        }

        setContent {
            val navController = rememberNavController()

            NavHost(navController, startDestination = "register") {
                composable("home") {
                    HomeScreen(navController)
                }
                composable("camera") {
                    CameraScreen(navController,openInter)
                }
                composable("login") {
                    LoginScreen(loginInter,navController = navController,this@MainActivity)
                }
                composable("register"){
                    RegisterScreen(registerInter,navController)
                }
            }
        }

    }


}