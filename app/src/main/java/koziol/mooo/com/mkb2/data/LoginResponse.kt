package koziol.mooo.com.mkb2.data


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LoginResponse(
    @SerialName("session")
    val session: Session = Session()
) {
    @Serializable
    data class Session(
        @SerialName("token")
        val token: String = "",
        @SerialName("user_id")
        val userId: Int = 0
    )
}