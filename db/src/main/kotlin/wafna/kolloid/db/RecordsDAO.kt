package wafna.kolloid.db

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
import wafna.kolloid.Record

private fun Query.marshal(): List<Record> = map { row -> Record(row[Records.id]!!, row[Records.data]!!) }

context (Database)
internal fun createRecordsDAO(): RecordsDAO = object : RecordsDAO {
    private val selector = from(Records).select()

    override fun create(record: Record): Boolean = 1 == insert(Records) {
        set(it.id, record.id)
        set(it.data, record.data)
    }

    override fun fetchAll(): List<Record> = selector.marshal()

    override fun byId(id: UUID): Record? =
        selector.where { Records.id eq id }
            .marshal().unique()

    override fun update(record: Record): Boolean = 1 == update(Records) {
        set(it.data, record.data)
        where { it.id eq record.id }
    }

    override fun delete(id: UUID): Boolean = 1 == delete(Records) {
        it.id eq id
    }
}
