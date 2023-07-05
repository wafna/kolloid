import kotlinx.serialization.Serializable

typealias UUID = String

@Serializable
data class User(val id: UUID, val username: String)

@Serializable
data class UserWIP(val username: String)
