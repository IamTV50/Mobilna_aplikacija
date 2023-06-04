import android.Manifest
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.navigation.NavController
import com.example.mobilna_aplikacija_paketnik.API.Register.RegisterResponse
import com.example.mobilna_aplikacija_paketnik.API.Register.RegisterUser
import kotlinx.coroutines.launch
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Response
import java.io.ByteArrayOutputStream
import java.io.File

fun checkAndRequestCameraPermission(
    context: Context,
    permission: String,
    launcher: ManagedActivityResultLauncher<String, Boolean>
) {
    val permissionCheckResult = ContextCompat.checkSelfPermission(context, permission)
    if (permissionCheckResult == PackageManager.PERMISSION_GRANTED) {
        // Open camera because permission is already granted
    } else {
        // Request permission
        launcher.launch(permission)
    }
}

fun Bitmap.toByteArray(): ByteArray {
    val stream = ByteArrayOutputStream()
    this.compress(Bitmap.CompressFormat.JPEG, 100, stream)
    return stream.toByteArray()
}

@Composable
fun RegisterScreen(registerInter: RegisterInterFace, navController: NavController, sharedPreferences: SharedPreferences) {
    val username = remember { mutableStateOf("") }
    val password = remember { mutableStateOf("") }
    val email = remember { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()
    val capturedPictures = remember { mutableStateListOf<Bitmap?>() } // List to store the captured pictures
    val picturesTaken = remember { mutableStateOf(0) }

    val context = LocalContext.current
    val permission = Manifest.permission.CAMERA
    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            // Permission granted, you can open the camera
        }
    }

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

        val tempImageFile = remember { mutableStateOf<File?>(null) }

        val takePictureLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.TakePicture()
        ) { success ->
            if (success) {
                tempImageFile.value?.let { file ->
                    val bitmap = BitmapFactory.decodeFile(file.absolutePath)
                    if (bitmap != null) {
                        capturedPictures.add(bitmap)
                        picturesTaken.value += 1
                    }
                }
            }
        }

        Button(
            onClick = {
                if (picturesTaken.value < 5) {
                    checkAndRequestCameraPermission(context, permission, launcher)
                    coroutineScope.launch {
                        val imageFile = File(context.cacheDir, "temp_image_${System.currentTimeMillis()}.jpeg")
                        tempImageFile.value = imageFile
                        val imageUri = FileProvider.getUriForFile(
                            context,
                            context.packageName + ".fileprovider",
                            imageFile
                        )
                        takePictureLauncher.launch(imageUri)
                    }
                } else {
                    // Show a message or disable the button when 2 pictures are already taken
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = picturesTaken.value < 5 // Enable the button if picturesTaken is less than 5
        ) {
            Text("Capture Pictures")
        }

        Button(
            onClick = {
                coroutineScope.launch {
                    try {
                        // Retrieve the username from the input field
                        val usernameValue = username.value

                        // Call the register function with the user data
                        val requestBody = RegisterUser(usernameValue, password.value, email.value)
                        val response: Response<RegisterResponse> = registerInter.register(requestBody)

                        if (response.isSuccessful) {
                            // Handle successful response
                            val responseBody = response.body()
                            println("Register successful: $responseBody")

                            if (capturedPictures.isNotEmpty()) {
                                // Upload images
                                val parts = capturedPictures.mapIndexedNotNull { index, bitmap ->
                                    bitmap?.let {
                                        val byteArray = it.toByteArray()
                                        val requestBody = byteArray.toRequestBody("image/png".toMediaTypeOrNull())
                                        val fileName = "image_$index.jpeg"
                                        println("Image $fileName: ${byteArray.size} bytes")
                                        MultipartBody.Part.createFormData("images", fileName, requestBody)
                                    }
                                }

                                // Pass the username and images to the uploadImages function
                                val uploadResponse: Response<RegisterResponse> = registerInter.uploadImages(usernameValue, parts)

                                if (uploadResponse.isSuccessful) {
                                    // Handle successful image upload
                                    println("Image upload successful")
                                } else {
                                    // Handle unsuccessful image upload
                                    println("Image upload failed")
                                }
                            }

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
            modifier = Modifier.fillMaxWidth(),
            enabled = picturesTaken.value == 5 // Enable the button if picturesTaken is exactly 5
        ) {
            Text("Submit")
        }
    }
}
