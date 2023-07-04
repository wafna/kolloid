package wafna.kolloid.db

import java.util.UUID
import org.ktorm.entity.Entity
import org.ktorm.schema.Table
import org.ktorm.schema.uuid
import org.ktorm.schema.varchar
import wafna.kolloid.Record

interface RecordEntity : Entity<RecordEntity> {
    companion object : Entity.Factory<RecordEntity>()
    val id: UUID
    var data: String
    fun toDomain(): Record = Record(id, data)
}

object Records : Table<RecordEntity>("record") {
    val id = uuid("id").primaryKey().bindTo { it.id }
    val data = varchar("data").bindTo { it.data }
}
