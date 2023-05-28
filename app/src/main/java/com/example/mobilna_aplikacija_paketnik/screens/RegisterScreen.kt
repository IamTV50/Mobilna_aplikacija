import android.graphics.Bitmap
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
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
import com.example.mobilna_aplikacija_paketnik.API.Register.RegisterInterFace
import com.example.mobilna_aplikacija_paketnik.API.Register.RegisterRequest
import kotlinx.coroutines.launch

@Composable
fun RegisterScreen(registerInter: RegisterInterFace, navController: NavController) {
    val username = remember { mutableStateOf("") }
    val password = remember { mutableStateOf("") }
    val gmail = remember { mutableStateOf("") }
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
            value = gmail.value,
            onValueChange = { newValue -> gmail.value = newValue },
            modifier = Modifier.padding(bottom = 8.dp)
        )

        val takePictureLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.TakePicturePreview()
        ) { bitmap ->
            capturedPictures.add(bitmap) // Add the captured picture to the list
        }
        

        Button(
            onClick = {
                coroutineScope.launch {
                    repeat(30) {
                        takePictureLauncher.launch(null as Void?)
                        // Delay for a short time before capturing the next picture
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
                val registerRequest = RegisterRequest(username.value, gmail.value, password.value)
                coroutineScope.launch {
                    try {
                        val registerResponse = registerInter.register(registerRequest)
                        println("Register successful: ${registerResponse.username}")
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

        LazyColumn {
            items(capturedPictures.size) {index ->
                val capturedeImage: Bitmap? = capturedPictures[index]
                capturedeImage?.let {
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


/*if (capturedPictures.isNotEmpty()) {
    val image: Bitmap? = capturedPictures[0]
    val bitmap: Bitmap? = image as? Bitmap
    bitmap?.let {
        val imageBitmap: ImageBitmap = it.asImageBitmap()
        Image(
            bitmap = imageBitmap,
            contentDescription = "Slika"
        )
    }
}*/