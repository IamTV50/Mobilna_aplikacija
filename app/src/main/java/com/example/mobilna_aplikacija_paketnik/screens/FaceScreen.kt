import android.Manifest
import android.content.Context
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.navigation.NavController
import com.example.mobilna_aplikacija_paketnik.API.FaceLogin.FaceLoginResponse
import com.example.mobilna_aplikacija_paketnik.API.Register.RegisterResponse
import com.example.mobilna_aplikacija_paketnik.API.Register.RegisterUser
import com.example.mobilna_aplikacija_paketnik.CompressionUtility
import com.example.mobilna_aplikacija_paketnik.screens.Footer
import com.example.mobilna_aplikacija_paketnik.screens.Header
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Response

import java.io.File
import java.io.FileOutputStream
import java.nio.file.Files
import java.nio.file.Paths
import java.util.Base64
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

@Composable
fun FaceLoginScreen(
    navController: NavController,
    faceLoginInterface: FaceLoginInterface,
    sharedPreferences: SharedPreferences
) {
    var capturedPictures by remember { mutableStateOf(listOf<Bitmap>()) }
    var picturesTaken by remember { mutableStateOf(0) }
    val context = LocalContext.current
    val tempImageFile = remember { mutableStateOf<File?>(null) }
    val scope = rememberCoroutineScope()


    val takePictureLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            tempImageFile.value?.let { file ->
                val bitmap = BitmapFactory.decodeFile(file.absolutePath)
                if (bitmap != null) {
                    capturedPictures = capturedPictures.toMutableList().apply { add(bitmap) }
                    picturesTaken += 1
                }
            }
        }
    }
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Header() // Added Header composable

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
                .weight(1f), // Added weight to occupy remaining space
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(
                onClick = {
                    if (picturesTaken < 3) {
                        val imageFile = File(context.cacheDir, "temp_image_${System.currentTimeMillis()}.jpeg")
                        tempImageFile.value = imageFile
                        val imageUri = FileProvider.getUriForFile(
                            context,
                            context.packageName + ".fileprovider",
                            imageFile
                        )
                        takePictureLauncher.launch(imageUri)
                    } else {
                        Toast.makeText(context, "You have captured 3 photos", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = picturesTaken < 3,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0x30, 0x30, 0x36))
            ) {
                Text(text = "Capture Pictures")
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    if (capturedPictures.size == 3) {
                        val username = sharedPreferences.getString("username", "") ?: ""
                        val zipFile=File(context.cacheDir, "captured_images.zip")
                        ZipOutputStream(FileOutputStream(zipFile)).use { zipOut ->
                            capturedPictures.forEachIndexed { index, bitmap ->
                                val byteArray = bitmap.toByteArray()
                                val zipEntry = ZipEntry("image_$index.jpeg")
                                zipOut.putNextEntry(zipEntry)
                                zipOut.write(byteArray)
                                zipOut.closeEntry()
                            }
                        }
                        val compressionUtility = CompressionUtility()
                        val base64String = encodetoBase64(zipFile.absolutePath)
                        var base64StringInt=base64ToNumericValues(base64String)
                        val compressedData = compressionUtility.compress(base64StringInt)
                        val requestBody = compressedData.toRequestBody("application/octet-stream".toMediaTypeOrNull())
                        val part = MultipartBody.Part.createFormData("compressed_data", "compressed_data.bin", requestBody)

//                        val parts = capturedPictures.mapIndexedNotNull { index, bitmap ->
//                            bitmap?.let {
//                                val byteArray = it.toByteArray()
//                                val requestBody = byteArray.toRequestBody("image/png".toMediaTypeOrNull())
//                                val fileName = "image_$index.jpeg"
//                                println("Image $fileName: ${byteArray.size} bytes")
//                                MultipartBody.Part.createFormData("images", fileName, requestBody)
//                            }
//                        }

                        val parts = listOf(part)

                        // Wrap the API call in a coroutine using launch
                        scope.launch {
                            try {
                                val response: Response<FaceLoginResponse> = faceLoginInterface.loginFace(username, parts)

                                if (response.isSuccessful) {
                                    // Handle successful login
                                    println("Image upload successful")
                                    navController.navigate("home")
                                } else {
                                    // Handle login failure
                                    println("Image upload failed")
                                }
                            } catch (e: Exception) {
                                // Handle exception
                                println("Image upload failed: ${e.message}")
                            }
                        }
                    } else {
                        Toast.makeText(context, "Please capture 3 photos", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = capturedPictures.size == 3
            ) {
                Text(text = "Submit")
            }
        }

        Footer() // Added Footer composable
    }

}

fun encodetoBase64(filePath: String): String {
    val imageBytes: ByteArray = Files.readAllBytes(Paths.get(filePath))

    // Encode the byte array to Base64
    val base64String= Base64.getEncoder().encodeToString(imageBytes)
    println("Base64 string :"+base64String)
    return base64String;
}

fun base64ToNumericValues(base64: String): List<Int> {
    return base64.toCharArray().map { it.code }
}


