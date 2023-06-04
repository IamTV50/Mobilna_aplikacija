package com.example.mobilna_aplikacija_paketnik.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController



@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController) {
    Scaffold(
        content = { innerPadding ->
            Column(modifier = Modifier.padding(innerPadding)) {
                Header()
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Button(
                        onClick = { navController.navigate("camera") },
                        modifier = Modifier.padding(16.dp),
                        shape = CircleShape,
                        contentPadding = PaddingValues(16.dp),
                        colors = ButtonDefaults.buttonColors(Color(0x30, 0x30, 0x36))
                    ) {
                        Text("Scan QR Code")
                    }
                    Button(
                        onClick = { navController.navigate("logs") },
                        modifier = Modifier.padding(16.dp),
                        shape = CircleShape,
                        contentPadding = PaddingValues(16.dp),
                        colors = ButtonDefaults.buttonColors(Color(0x30, 0x30, 0x36)),
                    ) {
                        Text("View Logs")
                    }
                }
                Footer()
            }
        }
    )
}

@Composable
fun Header() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(0.dp)
            .height(80.dp)
            .background(Color.Black),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Pametni paketnik",
            color = Color.White,
            fontSize = 20.sp,
        )
    }
}

@Composable
fun Footer() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(0.dp, vertical = 0.dp)
            .height(40.dp)
            .background(Color.Black),
        contentAlignment = Alignment.Center,

    ) {
        Text(
            text = "LFL",
            color = Color.White,
            fontSize = 20.sp,
        )
    }
}


