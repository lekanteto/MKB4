package koziol.mooo.com.mkb2.data


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LoginRequest(
    @SerialName("password")
    val password: String = "",
    @SerialName("pp")
    val pp: String = "accepted",
    @SerialName("tou")
    val tou: String = "accepted",
    @SerialName("ua")
    val ua: String = "app",
    @SerialName("username")
    val username: String = ""
)