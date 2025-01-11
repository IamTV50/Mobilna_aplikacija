package com.example.mobilna_aplikacija_paketnik.screens

import android.content.Context
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
import com.example.mobilna_aplikacija_paketnik.TSP.Tour
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.CameraPosition
import com.google.maps.android.compose.Polyline

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TSPVisualMap(navController: NavController, sharedPreferences: SharedPreferences) {
    var allCities by remember { mutableStateOf<List<City>?>(null) }
    var selectedCities by remember { mutableStateOf(mutableListOf<City>()) }
    val random = RandomUtils()
    var populationSize by remember{ mutableStateOf("100")}
    var crossoverRate by remember{ mutableStateOf("0.8")}
    var mutationRate by remember{ mutableStateOf("0.1")}
    var bestTour by remember { mutableStateOf<Tour?>(null) }

    LaunchedEffect(Unit) {
        try {
            val context = navController.context
            val assetManager = context.assets
            val inputStream = assetManager.open("locations.json") // resource root here is `app/src/main/assets`
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

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(LatLng(46.559248362905215, 15.641179177119962), 10f)
    }
    var timeDistanceCheck by remember { mutableStateOf(true) }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            Text("Select Cities for TSP", style = MaterialTheme.typography.titleLarge)

            // City selection list
            Box(modifier = Modifier.fillMaxWidth().height(200.dp) ) {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth()
                ) {
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
                                onCheckedChange = null
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("${city.city}; ${city.address}")
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))

            TextField(
                value = populationSize,
                onValueChange = { populationSize = it },
                label = { Text("Population Size (default : ${populationSize})") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            TextField(
                value = crossoverRate,
                onValueChange = { crossoverRate = it },
                label = { Text("Crossover Probability ( default: $crossoverRate") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            TextField(
                value = mutationRate,
                onValueChange = { mutationRate = it },
                label = { Text("Mutation Probability (default: $mutationRate") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))
            Row {
                Text("Optimize for: ", modifier = Modifier.padding(10.dp))
                Text("Time", modifier = Modifier.padding(6.dp))
                Switch(checked = timeDistanceCheck, onCheckedChange = {
                    timeDistanceCheck = it
                })
                Text("Distance", modifier = Modifier.padding(6.dp))
            }
            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = {
                val generatedTSPFileName = "selectedCities.tsp"

                if (!timeDistanceCheck) {
                    generateTSP(selectedCities, generatedTSPFileName, 1000, navController.context, OptimizeType.TOUR_TIME)
                }
                else{
                    generateTSP(selectedCities, generatedTSPFileName, 1000, navController.context)
                }

                random.setSeedFromTime()

                val tsp = TSP(generatedTSPFileName, 150, random, navController.context, "")
                val ga = GA(populationSize.toInt(), crossoverRate.toDouble(), mutationRate.toDouble(), random)
                bestTour = ga.execute(tsp)
            }) {
                Text("Start TSP with Selected Cities")
            }

           // val metric = if (timeDistanceCheck) "Time" else "Distance"
            //routeStatistics =

            Spacer(modifier = Modifier.height(16.dp))

            Spacer(modifier = Modifier.height(16.dp))

            if (bestTour != null && bestTour!!.getPath().isNotEmpty()) {
                Text(
                    text = "Distance is "+bestTour!!.getDistance(),
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )

                GoogleMap(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(400.dp),
                    cameraPositionState = cameraPositionState,
                    properties = MapProperties(isMyLocationEnabled = true),
                    uiSettings = MapUiSettings(zoomControlsEnabled = true),
                ) {
                    val polylinesPoints = mutableListOf<LatLng>()

                    for (c in bestTour!!.getPath()) {
                        polylinesPoints.add(LatLng(c.latitude.toDouble(), c.longitude.toDouble()))
                    }

                    // Draw stuff (lines)
                    if (polylinesPoints.isNotEmpty()) {
                        Polyline(points = polylinesPoints)
                    }
                }
            }
            else {
                Text(
                    text = "Loading or no tour available.",
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                )
            }
        }
    }
}

enum class OptimizeType { TOUR_TIME, TOUR_DISTANCE }

// Generate .tsp from selected cities
fun generateTSP(
    selectedCities: MutableList<City>,
    generatedFileName: String,
    maxEvals: Int,
    context: Context,
    optimizeFor: OptimizeType = OptimizeType.TOUR_DISTANCE
) {
    val random = RandomUtils()
    random.setSeedFromTime()

    val sourceFile = when (optimizeFor) {
        OptimizeType.TOUR_DISTANCE -> "all_locations_distance.tsp"
        OptimizeType.TOUR_TIME -> "all_locations_time.tsp"
    }

    val tsp = TSP(sourceFile, maxEvals, random, context, "")
    val fullWeights = tsp.weights
    val n = selectedCities.size
    val newMatrix = Array(n) { DoubleArray(n) }

    // Fill new matrix with distances between selected cities
    for (i in 0 until n) {
        for (j in 0 until n) {
            val cityI = selectedCities[i]
            val cityJ = selectedCities[j]
            newMatrix[i][j] = fullWeights[cityI.index][cityJ.index]
        }
    }

    // Generate new .tsp file content
    val tspContent = buildString {
        appendLine("NAME: Selected cities from Direct4me")
        appendLine("TYPE: TSP")
        appendLine("COMMENT: ${n} selected cities")
        appendLine("DIMENSION: $n")
        appendLine("EDGE_WEIGHT_TYPE: EXPLICIT")
        appendLine("EDGE_WEIGHT_FORMAT: FULL_MATRIX")
        appendLine("DISPLAY_DATA_TYPE: TWOD_DISPLAY")
        appendLine("EDGE_WEIGHT_SECTION")

        for (i in 0 until n) {
            appendLine(newMatrix[i].joinToString(" "))
        }

        appendLine("DISPLAY_DATA_SECTION")
        for (i in 0 until n) {
            val city = selectedCities[i]
            appendLine("${i + 1} ${city.latitude} ${city.longitude}")
        }
        appendLine("EOF")
    }

    // assets folder is read-only at runtime...
    // Save to internal storage instead of assets
    context.openFileOutput(generatedFileName, Context.MODE_PRIVATE).use { output ->
        output.write(tspContent.toByteArray())
    }
}