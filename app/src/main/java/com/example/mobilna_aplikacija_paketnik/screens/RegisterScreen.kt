import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageFormat
import android.hardware.camera2.*
import android.hardware.camera2.params.StreamConfigurationMap
import android.media.Image
import android.media.ImageReader
import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import android.os.Looper
import android.util.Log
import android.util.Size
import android.view.Surface
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.navigation.NavController
import com.example.mobilna_aplikacija_paketnik.API.Register.RegisterInterFace
import com.example.mobilna_aplikacija_paketnik.API.Register.RegisterRequest
import kotlinx.coroutines.launch
import java.nio.ByteBuffer
import java.util.*
import java.util.concurrent.Semaphore

//TU SE VRNI CE NE DELA
@ExperimentalMaterial3Api
@Composable
fun RegisterScreen(registerInter: RegisterInterFace, navController: NavController) {
    val username = remember { mutableStateOf("") }
    val password = remember { mutableStateOf("") }
    val gmail = remember { mutableStateOf("") }
    val capturedImages = remember { mutableStateListOf<Bitmap>() }
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current // Access the context here
    Column(modifier = Modifier.padding(16.dp)) {
        TextField(
            value = username.value,
            onValueChange = { newValue -> username.value = newValue },
            modifier = Modifier.padding(bottom = 8.dp)
        )

        TextField(
            value = password.value,
            onValueChange = { newValue -> password.value = newValue },
            modifier = Modifier.padding(bottom = 8.dp)
        )

        TextField(
            value = gmail.value,
            onValueChange = { newValue -> gmail.value = newValue },
            modifier = Modifier.padding(bottom = 8.dp)
        )

        /*val cameraPermissionLauncher: ManagedActivityResultLauncher<String, Boolean> =
            rememberLauncherForActivityResult(
                contract = ActivityResultContracts.RequestPermission()
            ) { isGranted: Boolean ->
                if (isGranted) {
                    openCameraAndCaptureFrames(context = context ,capturedImages)
                } else {
                    // Permission denied, handle accordingly
                    Log.e("CameraPermission", "Permission denied")
                }
            }*/

        val requestCameraPermission: () -> Unit = {
            //cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
        }

        Button(
            onClick = requestCameraPermission,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.Outlined.Add, contentDescription = "Open Camera")
            Text("Open Camera")
        }

        Button(
            onClick = {
                val registerRequest = RegisterRequest(username.value, gmail.value, password.value)
                coroutineScope.launch {
                    try {
                        val registerResponse = registerInter.register(registerRequest)
                        println("Register successful: ${registerResponse.username}")

                        // Request camera permission and start capturing frames
                        requestCameraPermission()

                        navController.navigate("home")
                    } catch (t: Throwable) {
                        println("Register failed: ${t.message}")
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Register")
        }
    }
}

/*override fun onRequestPermissionsResult(
    requestCode: Int,
    permissions: Array<String>,
    grantResults: IntArray
) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults)

    if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
        if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            // Permission granted, open camera
        } else {
            // Permission denied
        }
    }
}*/

/*private fun openCameraAndCaptureFrames(context: Context, capturedImages: MutableList<Bitmap>) {
    val cameraManager = context.getSystemService(Context.CAMERA_SERVICE) as CameraManager
    val cameraId = getCameraId(cameraManager)
    val imageReader = ImageReader.newInstance(imageWidth, imageHeight, ImageFormat.YUV_420_888, MAX_IMAGES)

    if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
        // Request camera permission here if needed
        return
    }

    cameraManager.openCamera(cameraId, object : CameraDevice.StateCallback() {
        override fun onOpened(camera: CameraDevice) {
            createCaptureSession(camera, imageReader, capturedImages)
        }

        override fun onDisconnected(camera: CameraDevice) {
            camera.close()
        }

        override fun onError(camera: CameraDevice, error: Int) {
            camera.close()
        }
    }, null)
}*/

/*private fun createCaptureSession(
    camera: CameraDevice,
    imageReader: ImageReader,
    capturedImages: MutableList<Bitmap>
) {
    val surfaces = listOf(imageReader.surface)

    camera.createCaptureSession(surfaces, object : CameraCaptureSession.StateCallback() {
        override fun onConfigured(session: CameraCaptureSession) {
            val captureRequestBuilder = camera.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW)
            captureRequestBuilder.addTarget(imageReader.surface)

            session.setRepeatingRequest(
                captureRequestBuilder.build(),
                null,
                Handler(Looper.getMainLooper())
            )
        }

        override fun onConfigureFailed(session: CameraCaptureSession) {
            // Failed to configure the capture session
        }
    }, null)
}*/

/*private fun getCameraId(cameraManager: CameraManager): String {
    val cameraIdList = cameraManager.cameraIdList
    for (id in cameraIdList) {
        val cameraCharacteristics = cameraManager.getCameraCharacteristics(id)
        val facing = cameraCharacteristics.get(CameraCharacteristics.LENS_FACING)
        if (facing == CameraCharacteristics.LENS_FACING_BACK) {
            return id
        }
    }
    return cameraIdList[0] // Default to the first camera if no back-facing camera is found
}*/

/*private fun imageToBitmap(image: Image): Bitmap {
    val buffer: ByteBuffer = image.planes[0].buffer
    val bytes = ByteArray(buffer.capacity())
    buffer.get(bytes)
    val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
    // Implement your own conversion logic here based on the captured image format

    return bitmap
}*/

private const val CAMERA_PERMISSION_REQUEST_CODE = 200
private const val imageWidth = 100
private const val imageHeight = 100
private const val MAX_IMAGES = 3
