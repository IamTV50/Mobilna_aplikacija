package com.example.mobilna_aplikacija_paketnik

import LoginScreen
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.mobilna_aplikacija_paketnik.API.Login.LoginInterface
import com.example.mobilna_aplikacija_paketnik.API.Register.RegisterInterFace
import com.example.mobilna_aplikacija_paketnik.screens.CameraScreen
import com.example.mobilna_aplikacija_paketnik.screens.HomeScreen
import com.example.mobilna_aplikacija_paketnik.screens.RegisterScreen
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val retrofit = Retrofit.Builder()
            .baseUrl("http://10.0.2.2:3001/") // Replace with your API base URL
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val loginInter = retrofit.create(LoginInterface::class.java)
        val registerInter  = retrofit.create(RegisterInterFace::class.java)

        setContent {
            val navController = rememberNavController()

            NavHost(navController, startDestination = "login") {
                composable("home") {
                    HomeScreen(navController)
                }
                composable("camera") {
                    CameraScreen(navController)
                }
                composable("login") {
                    LoginScreen(loginInter,navController = navController)
                }
                composable("register"){
                    RegisterScreen(registerInter,navController)
                }
            }
        }
    }
}