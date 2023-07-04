package wafna.kolloid.db

import org.flywaydb.core.Flyway
import org.ktorm.database.Database
import wafna.kolloid.Record
import java.util.UUID
import javax.sql.DataSource

fun initDB(url: String, username: String, password: String) {
    Flyway
        .configure()
        .dataSource(url, username, password)
        .locations("flyway")
        .load()
        .migrate()
}

fun createAppDB(dataSource: DataSource): AppDB {
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
