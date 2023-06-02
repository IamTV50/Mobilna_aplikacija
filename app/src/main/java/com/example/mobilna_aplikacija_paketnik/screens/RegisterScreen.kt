import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.navigation.NavController
import com.example.mobilna_aplikacija_paketnik.API.Register.RegisterResponse
import kotlinx.coroutines.launch
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Response

import java.io.ByteArrayOutputStream
import java.io.File

fun Bitmap.toByteArray(): ByteArray {
    val stream = ByteArrayOutputStream()
    this.compress(Bitmap.CompressFormat.PNG, 100, stream)
    return stream.toByteArray()
}

@Composable
fun RegisterScreen(registerInter: RegisterInterFace, navController: NavController) {
    val username = remember { mutableStateOf("") }
    val password = remember { mutableStateOf("") }
    val email = remember { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()
    val capturedPictures = remember { mutableStateListOf<Bitmap?>() } // List to store the captured pictures

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
            value = email.value,
            onValueChange = { newValue -> email.value = newValue },
            modifier = Modifier.padding(bottom = 8.dp)
        )

        val context = LocalContext.current
        val tempImageFile = remember { mutableStateOf<File?>(null) }

        val takePictureLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.TakePicture()
        ) { success ->
            if (success) {
                tempImageFile.value?.let { file ->
                    val bitmap = BitmapFactory.decodeFile(file.absolutePath)
                    capturedPictures.add(bitmap)
                }
            }
        }


        Button(
            onClick = {
                coroutineScope.launch {
                    repeat(2) {
                        val imageFile = File(context.cacheDir, "temp_image_${System.currentTimeMillis()}.jpg")
                        tempImageFile.value = imageFile
                        val imageUri = FileProvider.getUriForFile(
                            context,
                            context.packageName + ".fileprovider",
                            imageFile
                        )
                        takePictureLauncher.launch(imageUri)
                        kotlinx.coroutines.delay(5000)
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Capture Pictures")
        }


        Button(
            onClick = {
                val parts = capturedPictures.mapIndexedNotNull { index, bitmap ->
                    bitmap?.let {
                        val byteArray = it.toByteArray()
                        val requestBody = byteArray.toRequestBody("image/png".toMediaTypeOrNull())
                        MultipartBody.Part.createFormData("image_$index", "image_$index.png", requestBody)
                    }
                }

                coroutineScope.launch {
                    try {
                        val response: Response<RegisterResponse> = registerInter.register(parts)
                        if (response.isSuccessful) {
                            // Handle successful response
                            val responseBody = response.body()
                            println("Register successful: $responseBody")
                            navController.navigate("home")
                        } else {
                            // Handle unsuccessful response
                            println("Register failed")
                        }
                    } catch (e: Exception) {
                        // Handle exception
                        println("Register failed: ${e.message}")
                    }
                }

            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Submit")
        }


        LazyColumn {
            items(capturedPictures.size) { index ->
                val capturedImage: Bitmap? = capturedPictures[index]
                capturedImage?.let {
                    val imageBitmap: ImageBitmap = it.asImageBitmap()
                    Image(
                        bitmap = imageBitmap,
                        contentDescription = "Captured image $index"
                    )
                }
            }
        }
    }
}