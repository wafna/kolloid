package wafna.kjs.db

import wafna.kjs.Record
import java.util.UUID
import javax.sql.DataSource
import org.flywaydb.core.Flyway
import org.ktorm.database.Database

fun createAppDB(dataSource: DataSource): AppDB {
    Flyway
        .configure()
        .dataSource(dataSource)
        .locations("flyway")
        .load()
        .migrate()
    with(Database.connect(dataSource)) {
        return object : AppDB {
            override val recordsDAO: RecordsDAO = createRecordsDAO()
        }
    }
}

interface AppDB {
    val recordsDAO: RecordsDAO
}

interface RecordsDAO {
    fun createRecord(record: Record)
    fun readRecords(): List<Record>
    fun byId(id: UUID): Record?
    fun updateRecord(record: Record): Boolean
    fun deleteRecord(id: UUID): Boolean
}
