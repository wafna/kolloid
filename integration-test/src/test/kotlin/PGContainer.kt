package wafna.kjs.db

import arrow.core.Either
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import java.io.Closeable
import org.testcontainers.containers.PostgreSQLContainer
import org.testng.annotations.AfterTest
import org.testng.annotations.BeforeTest

private const val MaximumPoolSize = 8

private fun damnTheTorpedoes(action: () -> Unit) {
    Either.catch(action).onLeft {
        it.printStackTrace()
    }
}

open class PGContainer : Closeable {
    private val postgres = PostgreSQLContainer("postgres:15")
    private lateinit var dataSource: HikariDataSource
    lateinit var db: AppDB

    @BeforeTest
    fun start() {
        postgres.start()
        dataSource = HikariDataSource(HikariConfig().apply {
            jdbcUrl = postgres.jdbcUrl
            username = postgres.username
            password = postgres.password
            maximumPoolSize = MaximumPoolSize
        })
        db = createAppDB(dataSource)
    }

    @AfterTest
    override fun close() {
        damnTheTorpedoes { dataSource.close() }
        damnTheTorpedoes { postgres.stop() }
    }
}
