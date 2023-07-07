@file:Suppress("ConstructorParameterNaming", "ClassNaming")

package wafna.kolloid

import java.time.Instant
import java.util.UUID

private fun newID(): UUID = UUID.randomUUID()

data class User(val id: UUID, val username: String)

data class UserWIP(val username: String) {
    fun commit(): User = User(newID(), username)
}

data class Todo(
    val id: UUID,
    val owner: UUID,
    val title: String,
    val description: String?,
    val created: Instant,
    val due: Instant?
)

data class TodoWIP(val owner: UUID, val title: String, val description: String?, val due: Instant?) {
    fun commit(): Todo = Todo(
        id = newID(),
        owner = owner,
        title = title,
        description = description,
        created = Instant.now(),
        due = due
    )
}
