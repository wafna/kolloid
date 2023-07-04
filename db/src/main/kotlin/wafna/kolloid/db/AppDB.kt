package wafna.kolloid.db

import org.flywaydb.core.Flyway
import org.ktorm.database.Database
import wafna.kolloid.Record
import java.util.UUID
import javax.sql.DataSource

fun createAppDB(dataSource: DataSource): AppDB {
    Flyway
        .configure()
        .dataSource(dataSource)
        .locations("flyway")
        .load()
        .migrate()
    with(Database.connect(dataSource)) {
        return object : AppDB {
            override val records: RecordsDAO = createRecordsDAO()
        }
    }
}

interface AppDB {
    val records: RecordsDAO
}

interface RecordsDAO {
    fun create(record: Record)
    fun fetchAll(): List<Record>
    fun byId(id: UUID): Record?
    fun update(record: Record): Boolean
    fun delete(id: UUID): Boolean
}
