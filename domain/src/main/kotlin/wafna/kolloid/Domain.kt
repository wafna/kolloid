@file:Suppress("ConstructorParameterNaming", "ClassNaming")

package wafna.kolloid

import java.util.UUID

private fun newID(): UUID = UUID.randomUUID()

data class User(val id: UUID, val username: String)

// Tagging interface for the mangling.
interface Mangled

data class UserWIP(val username: String) : Mangled {
    fun commit(): User = User(newID(), username)
}
