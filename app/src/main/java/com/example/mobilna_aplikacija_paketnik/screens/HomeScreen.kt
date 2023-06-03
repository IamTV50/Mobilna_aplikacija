package com.example.mobilna_aplikacija_paketnik.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController



@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController) {
    Scaffold(
        content = {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Button(
                    onClick = { navController.navigate("camera") },
                    modifier = Modifier.padding(16.dp),
                    shape = CircleShape,
                    contentPadding = PaddingValues(16.dp)
                ) {
                    Text("Scan QR Code")
                }
                Button(
                    onClick = { navController.navigate("logs") },
                    modifier = Modifier.padding(16.dp),
                    shape = CircleShape,
                    contentPadding = PaddingValues(16.dp)
                ) {
                    Text("View Logs")
                }
                Button(
                    onClick = { navController.navigate("register") },
                    modifier = Modifier.padding(16.dp),
                    shape = CircleShape,
                    contentPadding = PaddingValues(16.dp)
                ) {
                    Text("View Logs")
                }
            }
        }
    )
}