package com.example.mobilna_aplikacija_paketnik.screens

import android.content.SharedPreferences
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.mobilna_aplikacija_paketnik.TSP.City
import com.example.mobilna_aplikacija_paketnik.TSP.GA
import com.example.mobilna_aplikacija_paketnik.TSP.RandomUtils
import com.example.mobilna_aplikacija_paketnik.TSP.TSP
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.CameraPosition
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TSPVisualMap(navController: NavController, sharedPreferences: SharedPreferences) {
    var allCities by remember { mutableStateOf<List<City>?>(null) }
    var selectedCities by remember { mutableStateOf(mutableListOf<City>()) }
    val random = RandomUtils()
    val tsp = TSP("a280.tsp", 0, random)

    // Use LaunchedEffect to load cities from JSON file
    LaunchedEffect(Unit) {
        try {
            val context = navController.context
            val assetManager = context.assets
            val inputStream = assetManager.open("locations.json")
            val jsonString = inputStream.bufferedReader().use { it.readText() }
            val gson = Gson()

            // Use TypeToken to parse the JSON into a list of City objects
            val cityListType = object : TypeToken<List<City>>() {}.type
            allCities = gson.fromJson(jsonString, cityListType)

            selectedCities=allCities!!.toMutableList()

        } catch (e: Exception) {
            println("Error loading cities: ${e.message}")
            e.printStackTrace()
        }
    }

    // Rest of your existing code...
    var popSize by remember { mutableStateOf("") }
    var crossoverProbability by remember { mutableStateOf("") }
    var mutationProbability by remember { mutableStateOf("") }

    val scrollState = rememberScrollState()
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(LatLng(0.0, 0.0), 10f)
    }

    Column(modifier = Modifier
        .fillMaxSize()
        .verticalScroll(scrollState)
        .padding(16.dp)) {
        Text("Select Cities for TSP", style = MaterialTheme.typography.titleLarge)

        // City selection list
        LazyColumn(modifier = Modifier.weight(1f)) {
            items(allCities ?: emptyList()) { city ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                        .clickable {
                            if (selectedCities.contains(city)) {
                                selectedCities.remove(city)
                            } else {
                                selectedCities.add(city)
                            }
                        },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = selectedCities.contains(city),
                        onCheckedChange = null // Checkbox state is handled by the Row click
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(city.city) // Display city name
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        TextField(
            value = popSize,
            onValueChange = { popSize = it },
            label = { Text("Population Size") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        TextField(
            value = crossoverProbability,
            onValueChange = { crossoverProbability = it },
            label = { Text("Crossover Probability") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        TextField(
            value = mutationProbability,
            onValueChange = { mutationProbability = it },
            label = { Text("Mutation Probability") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
           //todo

        }) {
            Text("Start TSP with Selected Cities")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Google Map
        /*GoogleMap(
            modifier = Modifier
                .fillMaxWidth()
                .height(400.dp),
            cameraPositionState = cameraPositionState,
            properties = MapProperties(isMyLocationEnabled = true),
            uiSettings = MapUiSettings(zoomControlsEnabled = true)
        )*/
    }
}