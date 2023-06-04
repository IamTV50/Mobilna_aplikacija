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
import com.example.mobilna_aplikacija_paketnik.screens.Footer
import com.example.mobilna_aplikacija_paketnik.screens.Header
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

        val context = LocalContext.current

        Button(
            onClick = {
                val loginRequest = LoginRequest(username.value, password.value)


                // Inside the Button onClick block for login
                coroutineScope.launch {
                    try {
                        val loginResponse = withContext(Dispatchers.IO) { loginInter.login(loginRequest) }


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
        modifier = Modifier.fillMaxSize()
    ) {
        Header() // Added Header composable

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
                .weight(1f), // Added weight to occupy remaining space
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

        Footer() // Added Footer composable
    }

}






