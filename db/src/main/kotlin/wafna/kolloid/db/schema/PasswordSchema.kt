package wafna.kolloid.db.schema

import java.util.UUID
import org.ktorm.entity.Entity
import org.ktorm.schema.Table
import org.ktorm.schema.bytes
import org.ktorm.schema.uuid

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
