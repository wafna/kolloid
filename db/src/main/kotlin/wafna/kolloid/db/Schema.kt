package wafna.kolloid.db

import org.ktorm.entity.Entity
import org.ktorm.schema.Table
import org.ktorm.schema.bytes
import org.ktorm.schema.uuid
import org.ktorm.schema.varchar
import java.util.UUID

interface UserEntity : Entity<UserEntity> {
    companion object : Entity.Factory<UserEntity>()
    val id: UUID
    var username: String
}

object UsersTable : Table<UserEntity>("users") {
    val id = uuid("id").primaryKey().bindTo { it.id }
    val username = varchar("username").bindTo { it.username }
}

interface PasswordEntity : Entity<PasswordEntity> {
    companion object : Entity.Factory<PasswordEntity>()
    val userId: UUID
    var salt: ByteArray
    var hash: ByteArray
}

object PasswordsTable : Table<PasswordEntity>("passwords") {
    val userId = uuid("user_id").primaryKey().bindTo { it.userId }
    val salt = bytes("salt").bindTo { it.salt }
    val hash = bytes("hash").bindTo { it.hash }
}
