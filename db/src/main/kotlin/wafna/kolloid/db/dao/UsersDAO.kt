package wafna.kolloid.db.dao

import java.util.UUID
import org.ktorm.database.Database
import org.ktorm.dsl.Query
import org.ktorm.dsl.delete
import org.ktorm.dsl.eq
import org.ktorm.dsl.from
import org.ktorm.dsl.insert
import org.ktorm.dsl.map
import org.ktorm.dsl.select
import org.ktorm.dsl.update
import org.ktorm.dsl.where
import wafna.kolloid.User
import wafna.kolloid.db.UsersDAO
import wafna.kolloid.db.UsersTable
import wafna.kolloid.db.unique

private fun Query.marshal(): List<User> = map { row ->
    User(
        row[UsersTable.id]!!,
        row[UsersTable.username]!!
    )
}

context (Database)
internal fun createUsersDAO(): UsersDAO = object : UsersDAO {
    private val selector = from(UsersTable).select()

    override fun create(user: User): Boolean =
        1 == insert(UsersTable) {
            set(UsersTable.id, user.id)
            set(UsersTable.username, user.username)
        }

    override fun fetchAll(): List<User> = selector.marshal()

    override fun byId(id: UUID): User? =
        selector.where { UsersTable.id eq id }
            .marshal().unique()

    override fun update(user: User): Boolean =
        1 == update(UsersTable) {
            set(UsersTable.username, user.username)
            where { UsersTable.id eq user.id }
        }

    override fun delete(id: UUID): Boolean = 1 == delete(UsersTable) {
        UsersTable.id eq id
    }
}
