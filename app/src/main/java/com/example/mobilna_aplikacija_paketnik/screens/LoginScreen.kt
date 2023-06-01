import android.content.Context
import android.content.SharedPreferences
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.mobilna_aplikacija_paketnik.API.Login.LoginRequest
import com.example.mobilna_aplikacija_paketnik.API.Login.LoginInterface
import com.google.gson.Gson
import kotlinx.coroutines.launch
import org.bson.types.ObjectId


@ExperimentalMaterial3Api
@Composable
fun LoginForm(loginInter: LoginInterface, navController: NavHostController,context: Context) {
    val username = remember { mutableStateOf("") }
    val password = remember { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()
    val context =
    Column(modifier = Modifier.padding(16.dp)) {
        Text(text = "Username")
        TextField(
            value = username.value,
            onValueChange = { newValue: String -> username.value = newValue },
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(text = "Password")
        TextField(
            value = password.value,
            onValueChange = { newValue: String -> password.value = newValue },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                val loginRequest = LoginRequest(username.value, password.value)
                coroutineScope.launch {
                    try {

                        val loginResponse = loginInter.login(loginRequest)
                        val gson = Gson()
                        val sharedPreferences = context.getSharedPreferences("MyAppPreferences", Context.MODE_PRIVATE)
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
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Login")
        }
    }
}

@ExperimentalMaterial3Api
@Composable
fun LoginScreen(loginInter: LoginInterface, navController: NavHostController,Context:Context) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Login",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        LoginForm(loginInter,navController,Context)
    }
}