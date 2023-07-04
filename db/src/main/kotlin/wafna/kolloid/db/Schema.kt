package wafna.kolloid.db

import org.ktorm.entity.Entity
import org.ktorm.schema.Table
import org.ktorm.schema.uuid
import org.ktorm.schema.varchar
import wafna.kolloid.Record
import java.util.UUID

interface RecordEntity : Entity<RecordEntity> {
    companion object : Entity.Factory<RecordEntity>()
    val id: UUID
    var data: String
}

object Records : Table<RecordEntity>("record") {
    val id = uuid("id").primaryKey().bindTo { it.id }
    val data = varchar("data").bindTo { it.data }
}
