import com.example.mobilna_aplikacija_paketnik.API.Log.LogResponse
import com.example.mobilna_aplikacija_paketnik.API.Log.LogRequest
import org.bson.types.ObjectId
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST

interface LogInterface {
    @POST("log/")
    suspend fun sendLog(
        @Body logRequest: LogRequest,
        @Header("Session") userId: String
    ): LogResponse
}