package wafna.kjs.db

import java.util.UUID
import org.ktorm.database.Database
import org.ktorm.dsl.delete
import org.ktorm.dsl.eq
import org.ktorm.dsl.from
import org.ktorm.dsl.insert
import org.ktorm.dsl.map
import org.ktorm.dsl.select
import org.ktorm.dsl.update
import org.ktorm.dsl.where
import wafna.kjs.Record

context (Database)
internal fun createRecordsDAO(): RecordsDAO = object : RecordsDAO {
    override fun createRecord(record: Record) {
        insert(Records) {
            set(it.id, record.id)
            set(it.data, record.data)
        }
    }

    override fun readRecords(): List<Record> = from(Records).select().map { row ->
        Record(row[Records.id]!!, row[Records.data]!!)
    }

    override fun byId(id: UUID): Record? {
        from(Records).select().where { Records.id eq id }.map { row ->
            Record(row[Records.id]!!, row[Records.data]!!)
        }.let { records ->
            return when (records.size) {
                0 -> null
                1 -> records[0]
                else -> throw IllegalStateException("Multiple records with id $id")
            }
        }
    }

    override fun updateRecord(record: Record): Boolean = 1 == update(Records) {
        set(it.data, record.data)
        where { it.id eq record.id }
    }

    override fun deleteRecord(id: UUID): Boolean = 1 == delete(Records) {
        it.id eq id
    }
}
