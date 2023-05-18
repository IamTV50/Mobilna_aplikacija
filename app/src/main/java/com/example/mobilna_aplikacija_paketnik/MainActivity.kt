package com.example.mobilna_aplikacija_paketnik

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.mobilna_aplikacija_paketnik.API.Login.LoginRequest
import com.example.mobilna_aplikacija_paketnik.API.Login.loginInterface
import com.example.mobilna_aplikacija_paketnik.screens.CameraScreen
import com.example.mobilna_aplikacija_paketnik.screens.HomeScreen
import com.example.mobilna_aplikacija_paketnik.screens.LoginScreen
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val retrofit = Retrofit.Builder()
            .baseUrl("http://10.0.2.2:3001/") // Replace with your API base URL
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val loginInter = retrofit.create(loginInterface::class.java)

        setContent {
            val navController = rememberNavController()

            NavHost(navController, startDestination = "home") {
                composable("home") {
                    HomeScreen(navController)
                }
                composable("camera") {
                    CameraScreen(navController)
                }
                composable("login") {
                    LoginScreen(loginInter)
                }
            }
        }
    }
}