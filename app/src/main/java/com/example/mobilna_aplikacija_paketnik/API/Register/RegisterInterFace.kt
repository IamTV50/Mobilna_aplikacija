import com.example.mobilna_aplikacija_paketnik.API.Register.RegisterResponse
import okhttp3.RequestBody
import okhttp3.Response
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface RegisterInterFace {
    @Multipart
    @POST("users/register")
    suspend fun register(
        @Part requestBody: RequestBody
    ): RegisterResponse
}
