package wafna.kolloid.db

import arrow.core.Either
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.testcontainers.containers.PostgreSQLContainer
import org.testng.annotations.AfterTest
import org.testng.annotations.BeforeTest
import wafna.kolloid.util.LazyLogger
import java.io.Closeable

private const val MaximumPoolSize = 8

private fun damnTheTorpedoes(action: () -> Unit) {
    Either.catch(action).onLeft {
        it.printStackTrace()
    }
}

private const val PostgresDelayStart = 3_000L

open class PGContainer : Closeable {
    private val log = LazyLogger(this::class)
    private val postgres = PostgreSQLContainer("postgres:15")
    private lateinit var dataSource: HikariDataSource
    lateinit var db: AppDB

    @BeforeTest
    fun start() = runBlocking {
        Either.catch {
            log.info { "Starting Postgres container." }
            postgres.start()
            delay(PostgresDelayStart)
            log.info { "Starting data source." }
            dataSource = HikariDataSource(
                HikariConfig().apply {
                    jdbcUrl = postgres.jdbcUrl
                    username = postgres.username
                    password = postgres.password
                    maximumPoolSize = MaximumPoolSize
                },
            )
            log.info { "Creating App DB" }
            db = createAppDB(dataSource)
        }.onLeft {
            // No need to pass on the error; the test fx will note it.
            log.error { "Failed to initialize test Postgres container." }
            throw it
        }
    }

    @AfterTest
    override fun close() {
        damnTheTorpedoes { dataSource.close() }
        damnTheTorpedoes { postgres.stop() }
    }
}
