import android.content.SharedPreferences
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CardElevation
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.mobilna_aplikacija_paketnik.API.Log.LogItem
import com.example.mobilna_aplikacija_paketnik.API.Log.LogRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.ZoneId
import java.util.Date
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security

@RequiresApi(Build.VERSION_CODES.O)
@ExperimentalMaterial3Api
@Composable
fun LogScreen(
    navController: NavController, logInter: LogInterface, sharedPreferences: SharedPreferences
) {

    val GetLogResponse = remember { mutableStateOf(listOf<LogItem>()) }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        try {
            val opend = LocalDate.now()
            val boxid = sharedPreferences.getInt("boxId", 0)
            val user = sharedPreferences.getString("username", "") ?: ""

            val getlogRequest = LogRequest(
                user,
                Date.from(opend.atStartOfDay(ZoneId.systemDefault()).toInstant()),
                boxid,
                false
            )

            val logResponse = logInter.getLogs(user)
            withContext(Dispatchers.Main) {
                GetLogResponse.value = logResponse //Assuming logResponse is a list of LogItem
            }
        } catch (e: Exception) {
            println("Error while getting logs" + e.message)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        LazyColumn {
            items(GetLogResponse.value) { log ->
                    Card(
                        shape = MaterialTheme.shapes.medium,
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp, pressedElevation = 8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp) // Add padding as per your requirement
                    )
                 {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Person, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = "User: ${log.user}", style = MaterialTheme.typography.bodySmall)
                        }

                        Divider(modifier = Modifier.padding(vertical = 8.dp))

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Refresh, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = "Opened: ${log.opend}", style = MaterialTheme.typography.bodySmall)
                        }

                        Divider(modifier = Modifier.padding(vertical = 8.dp))

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Security, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = "Force: ${log.force}", style = MaterialTheme.typography.bodySmall)
                        }

                        Divider(modifier = Modifier.padding(vertical = 8.dp))

                        Text(text = "Box ID: ${log.boxId}", style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        }
    }
    }
