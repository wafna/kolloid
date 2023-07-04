package wafna.colloid.db

import arrow.core.Either
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.testcontainers.containers.PostgreSQLContainer
import org.testng.annotations.AfterTest
import org.testng.annotations.BeforeTest
import wafna.kolloid.db.AppDB
import wafna.kolloid.db.createAppDB
import wafna.kolloid.util.LazyLogger

private const val ContainerDelayStart = 3_000L
private const val MaximumPoolSize = 3

open class PGContainer(
    private val containerDelayStart: Long = ContainerDelayStart,
    private val maximumConnections: Int = MaximumPoolSize,
) {
    private val log = LazyLogger(this::class)
    private val postgres = PostgreSQLContainer("postgres:15")
    private lateinit var dataSource: HikariDataSource
    lateinit var db: AppDB

    // Log an error and press on.
    private fun damnTheTorpedoes(f: () -> Unit) = Either.catch(f).onLeft {
        log.error(it) { "Error in Postgres container." }
    }

    @BeforeTest
    fun beforeTest() = runBlocking {
        Either.catch {
            log.info { "Starting Postgres container." }
            postgres.start()
            delay(containerDelayStart)
            log.info { "Starting data source." }
            dataSource = HikariDataSource(
                HikariConfig().apply {
                    jdbcUrl = postgres.jdbcUrl
                    username = postgres.username
                    password = postgres.password
                    maximumPoolSize = maximumConnections
                },
            )
            log.info { "Creating App DB" }
            db = createAppDB(dataSource)
        }.onLeft {
            // No need to log the error; the test fx will note it.
            log.error { "Failed to initialize test Postgres container." }
            throw it
        }
    }

    @AfterTest
    fun afterTest() {
        damnTheTorpedoes { dataSource.close() }
        damnTheTorpedoes { postgres.stop() }
    }
}
