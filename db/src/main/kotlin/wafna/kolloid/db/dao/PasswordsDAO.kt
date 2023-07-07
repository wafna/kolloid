package wafna.kolloid.db.dao

import org.ktorm.database.Database
import org.ktorm.dsl.Query
import org.ktorm.dsl.eq
import org.ktorm.dsl.from
import org.ktorm.dsl.insert
import org.ktorm.dsl.map
import org.ktorm.dsl.select
import org.ktorm.dsl.update
import org.ktorm.dsl.where
import wafna.kolloid.db.UserPasswordHash
import wafna.kolloid.db.PasswordsDAO
import wafna.kolloid.db.schema.PasswordsTable
import java.util.UUID

private fun Query.marshal(): List<UserPasswordHash> = map { row ->
    UserPasswordHash(
        row[PasswordsTable.userId]!!,
        row[PasswordsTable.salt]!!,
        row[PasswordsTable.hash]!!,
    )
}

context (Database)
internal fun createPasswordsDAO(): PasswordsDAO = object : PasswordsDAO {
    private val selector = from(PasswordsTable).select()

    override fun create(password: UserPasswordHash): Boolean =
        1 == insert(PasswordsTable) {
            set(PasswordsTable.userId, password.userId)
            set(PasswordsTable.salt, password.salt)
            set(PasswordsTable.hash, password.hash)
        }

    override fun byUserId(userId: UUID): UserPasswordHash? =
        selector.where { PasswordsTable.userId eq userId }
            .marshal().singleOrNull()

    override fun update(password: UserPasswordHash): Boolean =
        1 == update(PasswordsTable) {
            set(PasswordsTable.salt, password.salt)
            set(PasswordsTable.hash, password.hash)
            where { PasswordsTable.userId eq password.userId }
        }

    override fun delete(userId: UUID): Boolean = TODO()
}
