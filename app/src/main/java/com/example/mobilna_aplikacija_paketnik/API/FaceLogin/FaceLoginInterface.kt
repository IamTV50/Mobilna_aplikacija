import com.example.mobilna_aplikacija_paketnik.API.FaceLogin.FaceLoginRequest
import com.example.mobilna_aplikacija_paketnik.API.FaceLogin.FaceLoginResponse
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Headers
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Body

interface FaceLoginInterface {
    @POST("users/loginFace/{username}")
    @Headers("Content-Type: application/json")
    suspend fun loginFace(
        @Path("username") username: String,
        @Body compressedData: FaceLoginRequest
    ): Response<FaceLoginResponse>
}
