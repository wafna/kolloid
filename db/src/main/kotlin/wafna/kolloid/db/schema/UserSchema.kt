package wafna.kolloid.db.schema

import java.util.UUID
import org.ktorm.entity.Entity
import org.ktorm.schema.Table
import org.ktorm.schema.uuid
import org.ktorm.schema.varchar

interface UserEntity : Entity<UserEntity> {
    companion object : Entity.Factory<UserEntity>()
    val id: UUID
    var username: String
}

object UsersTable : Table<UserEntity>("users") {
    val id = uuid("id").primaryKey().bindTo { it.id }
    val username = varchar("username").bindTo { it.username }
}
