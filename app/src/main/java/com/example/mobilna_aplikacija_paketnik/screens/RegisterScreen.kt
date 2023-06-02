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
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.mobilna_aplikacija_paketnik.API.Register.RegisterRequest
import com.example.mobilna_aplikacija_paketnik.API.Register.RegisterResponse
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.ByteArrayOutputStream
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.FileProvider
import java.io.File

fun Bitmap.toByteArray(): ByteArray {
    val stream = ByteArrayOutputStream()
    this.compress(Bitmap.CompressFormat.PNG, 100, stream)
    return stream.toByteArray()
}
fun ByteArray.toRequestBody(contentType: String): RequestBody {
    return this.toRequestBody(contentType.toMediaType().toString())
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
                val requestBodyBuilder = MultipartBody.Builder().setType(MultipartBody.FORM)
                    .addFormDataPart("username", username.value)
                    .addFormDataPart("password", password.value)
                    .addFormDataPart("email", email.value)

                capturedPictures.forEachIndexed { index, bitmap ->
                    bitmap?.let {
                        val stream = ByteArrayOutputStream()
                        it.compress(Bitmap.CompressFormat.PNG, 100, stream)
                        val byteArray = stream.toByteArray()
                        val requestBody = byteArray.toRequestBody("image/png")
                        requestBodyBuilder.addFormDataPart(
                            "image_$index",
                            "image_$index.png",
                            requestBody
                        )
                    }
                }
                coroutineScope.launch {
                    try {
                        //val res = RegisterInterFace.
                        if (res.isSuccessful) {
                            // Handle successful response
                            val responseBody = res.body()
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
            Text("Register")
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
