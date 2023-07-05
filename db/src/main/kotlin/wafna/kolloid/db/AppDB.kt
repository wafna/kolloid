package wafna.kolloid.db

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.flywaydb.core.Flyway
import org.ktorm.database.Database
import wafna.kolloid.User
import java.util.UUID
import javax.sql.DataSource

private fun DatabaseConfig.hikariConfig() = HikariConfig().also {
    it.jdbcUrl = jdbcUrl
    it.username = username
    it.password = password
    it.maximumPoolSize = maximumPoolSize
}

// Exposed for testing.
fun createAppDB(dataSource: DataSource): AppDB {
    Flyway
        .configure()
        .dataSource(dataSource)
        .locations("flyway")
        .load()
        .migrate()
    return with(Database.connect(dataSource)) {
        object : AppDB {
            override val records: UsersDAO = createUsersDAO()
        }
    }
}

fun withAppDB(config: DatabaseConfig, borrow: (AppDB) -> Unit) {
    HikariDataSource(config.hikariConfig()).use { dataSource ->
        borrow(createAppDB(dataSource))
    }
}

interface AppDB {
    val records: UsersDAO
}

interface UsersDAO {
    fun create(user: User): Boolean
    fun fetchAll(): List<User>
    fun byId(id: UUID): User?
    fun update(user: User): Boolean
    fun delete(id: UUID): Boolean
}
