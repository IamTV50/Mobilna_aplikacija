import com.example.mobilna_aplikacija_paketnik.API.Log.LogResponse
import com.example.mobilna_aplikacija_paketnik.API.Log.LogRequest
import retrofit2.http.Body
import retrofit2.http.POST

interface LogInterface {
    @POST("log/")
    suspend fun login(@Body logRequest: LogRequest): LogResponse
}