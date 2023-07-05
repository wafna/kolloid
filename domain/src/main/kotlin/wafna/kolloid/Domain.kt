@file:Suppress("ConstructorParameterNaming", "ClassNaming")

package wafna.kolloid

import java.util.UUID

private fun newID(): UUID = UUID.randomUUID()

data class User(val id: UUID, val username: String)

data class UserWIP(val username: String) {
    fun commit(): User = User(newID(), username)
}

@Suppress("ArrayInDataClass")
data class Password(val userId: UUID, val salt: ByteArray, val hash: ByteArray)
