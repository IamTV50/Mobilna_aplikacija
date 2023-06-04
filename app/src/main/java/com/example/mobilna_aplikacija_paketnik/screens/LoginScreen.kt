import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color.rgb
import android.text.style.BackgroundColorSpan
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Black
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import androidx.navigation.NavHostController
import com.example.mobilna_aplikacija_paketnik.API.FaceLogin.FaceLoginRequest
import com.example.mobilna_aplikacija_paketnik.API.FaceLogin.FaceLoginResponse
import com.example.mobilna_aplikacija_paketnik.API.Login.LoginRequest
import com.example.mobilna_aplikacija_paketnik.API.Login.LoginInterface
import com.example.mobilna_aplikacija_paketnik.API.Register.RegisterResponse
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Response
import java.io.ByteArrayOutputStream
import java.io.File

fun Bitmap.toByteArray1(): ByteArray {
    val stream = ByteArrayOutputStream()
    this.compress(Bitmap.CompressFormat.JPEG, 100, stream)
    return stream.toByteArray()
}

@OptIn(ExperimentalUnsignedTypes::class)
@ExperimentalMaterial3Api
@Composable
fun LoginForm(loginInter: LoginInterface, navController: NavHostController, faceLogInter: FaceLoginInterface) {
    val username = remember { mutableStateOf("") }
    val password = remember { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()
    val capturedPictures = remember { mutableStateListOf<Bitmap?>() } // List to store the captured pictures

    Column(modifier = Modifier.padding(16.dp)) {
        Text(text = "Username")
        TextField(
            value = username.value,
            onValueChange = { newValue: String -> username.value = newValue },
            modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.textFieldColors(containerColor = Color.LightGray)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(text = "Password")
        TextField(
            value = password.value,
            onValueChange = { newValue: String -> password.value = newValue },
            modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.textFieldColors(containerColor = Color.LightGray),
            visualTransformation = PasswordVisualTransformation()
        )

        Spacer(modifier = Modifier.height(16.dp))
        val tempImageFile = remember { mutableStateOf<File?>(null) }
        val context = LocalContext.current
        val takePictureLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.TakePicture()
        ) { success ->
            if (success) {
                tempImageFile.value?.let { file ->
                    val bitmap = BitmapFactory.decodeFile(file.absolutePath)
                    capturedPictures.add(bitmap)
                    println("Zajete slike ${capturedPictures.size}")
                }
            }
        }

        Button(
            onClick = {
                coroutineScope.launch {
                    repeat(3) {
                        withContext(Dispatchers.IO) {
                            val imageFile = File(
                                context.cacheDir,
                                "temp_image_${System.currentTimeMillis()}.jpg"
                            )
                            tempImageFile.value = imageFile
                            val imageUri = FileProvider.getUriForFile(
                                context,
                                context.packageName + ".fileprovider",
                                imageFile
                            )
                            takePictureLauncher.launch(imageUri)
                        }
                        kotlinx.coroutines.delay(5000)
                    }
                }
            },
            modifier = Modifier.fillMaxWidth() ,
            colors = ButtonDefaults.buttonColors(containerColor = Color(0x30, 0x30, 0x36)),

        ) {
            Text("Capture Picture")
        }

        Button(
            onClick = {
                val loginRequest = LoginRequest(username.value, password.value)

                val parts = capturedPictures.mapIndexedNotNull { index, bitmap ->
                    bitmap?.let {
                        val byteArray = it.toByteArray1()
                        val requestBody = byteArray.toRequestBody("image/png".toMediaTypeOrNull())
                        MultipartBody.Part.createFormData("images", "image_$index.jpeg", requestBody)
                    }

                }

                // Inside the Button onClick block for login
                coroutineScope.launch {
                    try {
                        val loginResponse = withContext(Dispatchers.IO) { loginInter.login(loginRequest) }

                        val faceLoginresponse: Response<FaceLoginResponse> = withContext(Dispatchers.IO) {
                            faceLogInter.loginFace(username.value,parts)
                        }

                        if (faceLoginresponse.isSuccessful) {
                            val responseBody = faceLoginresponse.body()
                            println("Face login successful: $responseBody")
                            navController.navigate("home")
                        } else {
                            println("Face login failed")
                        }
                        val gson = Gson()
                        val sharedPreferences = withContext(Dispatchers.IO) {
                            context.getSharedPreferences("MyAppPreferences", Context.MODE_PRIVATE)
                        }
                        sharedPreferences.edit().putString("_id", loginResponse._id).apply()
                        sharedPreferences.edit().putString("username", loginResponse.username).apply()
                        println("Login successful: ${loginResponse.username}")
                        println("Login successful: ${loginResponse._id}")

                        navController.navigate("home")
                    } catch (t: Throwable) {
                        println("Login failed: ${t.message}")
                        /*capturedPictures.forEachIndexed { index, bitmap ->
                            val sizeInBytes = bitmap?.byteCount ?: 0
                            val sizeInKb = sizeInBytes / 1024.0
                            val sizeInMb = sizeInBytes / (1024.0 * 1024.0)
                            println("Captured image $index size: $sizeInBytes bytes ($sizeInKb KB, $sizeInMb MB)")
                        }*/
                    }
                }

            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor =Color(0x30, 0x30, 0x36)),

        ) {
            Text(text = "Login")
        }
        Button(
            onClick = { navController.navigate("register") },
            modifier = Modifier.fillMaxWidth(),

            colors = ButtonDefaults.buttonColors(Color(0x30, 0x30, 0x36)),
            shape = CircleShape,
            contentPadding = PaddingValues(8.dp)
        ) {
            Text("Register", color = Color.White)
        }

        //Spacer(modifier = Modifier.height(16.dp))


    }
}

@ExperimentalMaterial3Api
@Composable
fun LoginScreen(
    loginInter: LoginInterface,
    navController: NavHostController,
    faceLogInter: FaceLoginInterface
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(modifier = Modifier.padding(bottom = 16.dp)) {
            Text(
                text = "Login",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
            LoginForm(loginInter, navController, faceLogInter)

        }
    }
}






