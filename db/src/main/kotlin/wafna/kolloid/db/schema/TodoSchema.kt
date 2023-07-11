package wafna.kolloid.db.schema

import java.time.Instant
import java.util.UUID
import org.ktorm.entity.Entity
import org.ktorm.schema.Table
import org.ktorm.schema.timestamp
import org.ktorm.schema.uuid
import org.ktorm.schema.varchar

interface TodoEntity : Entity<TodoEntity> {
    companion object : Entity.Factory<TodoEntity>()

    val id: UUID
    var owner: UUID
    var title: String
    var description: String?
    var created: Instant
    var due: Instant?
}

object TodosTable : Table<TodoEntity>("todos") {
    val id = uuid("id").primaryKey().bindTo { it.id }
    val owner = uuid("owner").bindTo { it.owner }
    val title = varchar("title").bindTo { it.title }
    val description = varchar("description").bindTo { it.description }
    val created = timestamp("created_date").bindTo { it.created }
    val due = timestamp("due_date").bindTo { it.due }
}
