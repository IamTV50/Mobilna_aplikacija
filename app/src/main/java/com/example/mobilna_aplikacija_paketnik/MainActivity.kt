package com.example.mobilna_aplikacija_paketnik

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.util.Size
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.example.mobilna_aplikacija_paketnik.ui.theme.Mobilna_aplikacija_paketnikTheme
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import com.google.zxing.ResultPoint
import com.journeyapps.barcodescanner.BarcodeCallback
import com.journeyapps.barcodescanner.BarcodeResult
import com.journeyapps.barcodescanner.CompoundBarcodeView
import java.util.jar.Manifest
import androidx.core.content.ContextCompat
import com.google.common.util.concurrent.ListenableFuture

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Mobilna_aplikacija_paketnikTheme {
                var code by remember {
                    mutableStateOf("")
                }
                val context = LocalContext.current
                val lifecyleOwner = LocalLifecycleOwner.current
                val cameraProviderFuture = remember {
                    ProcessCameraProvider.getInstance(context)
                }

                var hasCameraPermission by remember {
                    mutableStateOf(
                        ContextCompat.checkSelfPermission(
                            context,
                            android.Manifest.permission.CAMERA
                        ) == PackageManager.PERMISSION_GRANTED
                    )
                }

                val buttonClicked = remember { mutableStateOf(false) }

                val launcher = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.RequestPermission(),
                    onResult = { granted ->
                        hasCameraPermission = granted
                        if (granted) {
                            buttonClicked.value = true
                        }
                    }
                )

                LaunchedEffect(buttonClicked.value) {
                    if (buttonClicked.value) {
                        launcher.launch(android.Manifest.permission.CAMERA)
                    }
                }

                // A surface container using the 'background' color from the theme
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    if (!buttonClicked.value) {
                        openBoxButton(
                            onClick = {
                                buttonClicked.value = true
                            }
                        )
                    } else if (hasCameraPermission) {
                        CameraView(
                            context = context,
                            lifecyleOwner = lifecyleOwner,
                            cameraProviderFuture = cameraProviderFuture,
                            onCodeScanned = { result ->
                                code = result
                            }
                        )
                        Text(
                            text = code,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun openBoxButton(onClick: () -> Unit) {
    val buttonText = remember { mutableStateOf("Click me!") }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Button(
            onClick = {
                buttonText.value = "Clicked!"
                onClick()
            },
            shape = CircleShape,
            modifier = Modifier
                .padding(16.dp)
                .size(100.dp)
        ) {
            Text(text = buttonText.value)
        }
    }
}

@Composable
fun CameraView(
    context: Context,
    lifecyleOwner: LifecycleOwner,
    cameraProviderFuture: ListenableFuture<ProcessCameraProvider>,
    onCodeScanned: (String) -> Unit
) {
    AndroidView(factory = { context ->
        val previewView = PreviewView(context)
        val preview = androidx.camera.core.Preview.Builder().build()
        val selector = CameraSelector.Builder()
            .requireLensFacing(CameraSelector.LENS_FACING_BACK)
            .build()
        preview.setSurfaceProvider(previewView.surfaceProvider)
        val imageAnalysis = ImageAnalysis.Builder()
            .setTargetResolution(
                Size(
                    previewView.width,
                    previewView.height
                )
            )
            .setBackpressureStrategy(STRATEGY_KEEP_ONLY_LATEST)
            .build()
        imageAnalysis.setAnalyzer(
            ContextCompat.getMainExecutor(context),
            QrScanner { result ->
                onCodeScanned(result)
            }
        )
        try {
            cameraProviderFuture.get().bindToLifecycle(
                lifecyleOwner,
                selector,
                preview,
                imageAnalysis
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
        previewView
    },
        modifier = Modifier
            .fillMaxSize()
    )
}






