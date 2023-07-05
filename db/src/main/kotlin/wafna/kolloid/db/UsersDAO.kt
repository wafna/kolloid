package wafna.kolloid.db

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
import java.util.UUID

private fun Query.marshal(): List<User> = map { row -> User(row[UsersTable.id]!!, row[UsersTable.username]!!) }

context (Database)
internal fun createUsersDAO(): UsersDAO = object : UsersDAO {
    private val selector = from(UsersTable).select()

    override fun create(user: User): Boolean = 1 == insert(UsersTable) {
        set(it.id, user.id)
        set(it.username, user.username)
    }

    override fun fetchAll(): List<User> = selector.marshal()

    override fun byId(id: UUID): User? =
        selector.where { UsersTable.id eq id }
            .marshal().unique()

    override fun update(user: User): Boolean = 1 == update(UsersTable) {
        set(it.username, user.username)
        where { it.id eq user.id }
    }

    override fun delete(id: UUID): Boolean = 1 == delete(UsersTable) {
        it.id eq id
    }
}
