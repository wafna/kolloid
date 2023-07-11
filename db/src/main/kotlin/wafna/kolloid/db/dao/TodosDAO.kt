package wafna.kolloid.db.dao

import java.util.UUID
import org.ktorm.dsl.Query
import org.ktorm.dsl.and
import org.ktorm.dsl.delete
import org.ktorm.dsl.eq
import org.ktorm.dsl.from
import org.ktorm.dsl.insert
import org.ktorm.dsl.like
import org.ktorm.dsl.map
import org.ktorm.dsl.select
import org.ktorm.dsl.update
import org.ktorm.dsl.where
import wafna.kolloid.Todo
import wafna.kolloid.db.TodosDAO
import wafna.kolloid.db.schema.TodosTable

private fun Query.marshal(): List<Todo> = map { row ->
    Todo(
        row[TodosTable.id]!!,
        row[TodosTable.owner]!!,
        row[TodosTable.title]!!,
        row[TodosTable.description],
        row[TodosTable.created]!!,
        row[TodosTable.due],
    )
}

context(org.ktorm.database.Database)
internal fun createTodosDAO() = object : TodosDAO {
    private val selector = from(TodosTable).select()

    override fun create(todo: Todo): Boolean =
        1 == insert(TodosTable) {
            set(TodosTable.id, todo.id)
            set(TodosTable.owner, todo.owner)
            set(TodosTable.title, todo.title)
            set(TodosTable.description, todo.description)
            set(TodosTable.created, todo.created)
            set(TodosTable.due, todo.due)
        }

    override fun byOwner(ownerId: UUID): List<Todo> = selector
        .where { TodosTable.owner eq ownerId }.marshal()

    override fun search(ownerId: UUID, token: String): List<Todo> = selector
        .where { (TodosTable.owner eq ownerId) and (TodosTable.title like "%$token%") }
        .marshal()

    override fun update(todo: Todo): Boolean =
        1 == update(TodosTable) {
            set(TodosTable.title, todo.title)
            where { TodosTable.id eq todo.id }
        }

    override fun delete(id: UUID): Boolean =
        1 == delete(TodosTable) { TodosTable.id eq id }
}
