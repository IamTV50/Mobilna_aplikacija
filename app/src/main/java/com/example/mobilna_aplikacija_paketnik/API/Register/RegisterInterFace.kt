import android.service.autofill.UserData
import com.example.mobilna_aplikacija_paketnik.API.Register.RegisterResponse
import com.example.mobilna_aplikacija_paketnik.API.Register.RegisterUser
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface RegisterInterFace {
    @POST("/users")
    suspend fun register(
      @Body requestBody: RegisterUser
    ): Response<RegisterResponse>

    @Multipart
    @POST("/users/register")
    suspend fun uploadImages(
        @Part images: List<MultipartBody.Part>
    ): Response<RegisterResponse>
}

