package wafna.kolloid.server

import arrow.core.Either
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.sksamuel.hoplite.ConfigLoaderBuilder
import com.sksamuel.hoplite.addFileSource
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.ktor.http.HttpMethod
import io.ktor.serialization.gson.gson
import io.ktor.server.application.Application
import io.ktor.server.application.ApplicationCallPipeline
import io.ktor.server.application.call
import io.ktor.server.application.install
import io.ktor.server.engine.applicationEngineEnvironment
import io.ktor.server.engine.connector
import io.ktor.server.engine.embeddedServer
import io.ktor.server.http.content.staticFiles
import io.ktor.server.netty.Netty
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.plugins.cors.routing.CORS
import io.ktor.server.request.httpMethod
import io.ktor.server.request.uri
import io.ktor.server.routing.Route
import io.ktor.server.routing.RouteSelector
import io.ktor.server.routing.RouteSelectorEvaluation
import io.ktor.server.routing.RoutingResolveContext
import io.ktor.server.routing.route
import io.ktor.server.routing.routing
import kotlinx.coroutines.runBlocking
import wafna.kolloid.RecordWIP
import wafna.kolloid.db.AppDB
import wafna.kolloid.db.createAppDB
import wafna.kolloid.util.LazyLogger
import java.io.File
import java.lang.reflect.Type
import java.nio.file.Files
import java.nio.file.Paths
import java.util.UUID

private object Server

private val log = LazyLogger(Server::class)

fun DatabaseConfig.hikariConfig() = HikariConfig().also {
    it.jdbcUrl = jdbcUrl
    it.username = username
    it.password = password
    it.maximumPoolSize = maximumPoolSize
}

data class ServerContext(val db: AppDB)

fun main(args: Array<String>): Unit = runBlocking {
    Runtime.getRuntime().addShutdownHook(object : Thread() {
        override fun run() {
            log.warn { "Shutting down." }
        }
    })

    require(1 == args.size) {
        "Missing required argument: config file path."
    }

    val configFilePath = Paths.get(args[0]).also {
        require(Files.isRegularFile(it)) { "Config file not found: $it" }
    }

    val appConfig = ConfigLoaderBuilder.default()
        .addFileSource(configFilePath.toFile())
        .build()
        .loadConfigOrThrow<AppConfig>()

    val staticDir = File(appConfig.server.static).also {
        log.info { "Serving static directory: ${it.canonicalPath}" }
    }

    HikariDataSource(appConfig.database.hikariConfig()).use { dataSource ->
        val appDB = createAppDB(dataSource)
        // Populate the database with some demo data for the UI.
        with(appDB) {
            recordsDAO.create(RecordWIP("Huey").commit())
            recordsDAO.create(RecordWIP("Dewey").commit())
            recordsDAO.create(RecordWIP("Louie").commit())
        }
        // Send it.
        with(ServerContext(appDB)) {
            runServer(staticDir)
        }
    }
}

private object Access

private val accessLog = LazyLogger(Access::class)

/**
 * Wrap this around a route to get a look at the activity on that route.
 */
fun Route.accessLog(callback: Route.() -> Unit): Route =
    createChild(object : RouteSelector() {
        override fun evaluate(context: RoutingResolveContext, segmentIndex: Int): RouteSelectorEvaluation =
            RouteSelectorEvaluation.Constant
    }).also { accessLogRoute ->
        accessLogRoute.intercept(ApplicationCallPipeline.Plugins) {
            accessLog.info { "${call.request.httpMethod.value} ${call.request.uri}" }
            proceed()
        }
        callback(accessLogRoute)
    }

private const val DEFAULT_PORT = 8081

context(ServerContext)
fun runServer(staticDir: File) {
    check(staticDir.isDirectory)

    applicationEngineEnvironment {
        connector {
            port = DEFAULT_PORT
            host = "0.0.0.0"
        }
        module {
            installCORS()
            installContentNegotiation()
            installRoutes(staticDir)
        }
    }.let { environment ->
        embeddedServer(Netty, environment).apply {
            log.info { "Starting server..." }
            start(true)
        }
    }
}

context(ServerContext)
private fun Application.installRoutes(staticDir: File) {
    routing {
        accessLog {
            route("/api") {
                api()
            }
            route("/") {
                // https://ktor.io/docs/serving-static-content.html
                staticFiles("/", staticDir)
            }
        }
    }
}

// See https://ktor.io/docs/gson.html
private fun Application.installContentNegotiation() {
    install(ContentNegotiation) {
        gson {
            disableHtmlEscaping()
            serializeNulls()
            registerTypeAdapter(
                UUID::class.java,
                object : JsonDeserializer<UUID> {
                    override fun deserialize(
                        json: JsonElement?,
                        typeOfT: Type?,
                        context: JsonDeserializationContext?,
                    ): UUID {
                        return Either.catch {
                            json!!.asString
                                .let { UUID.fromString(it) }
                        }.fold(
                            { e ->
                                throw IllegalArgumentException(
                                    "Failed to serialize UUID from ${json?.asString}",
                                    e,
                                )
                            },
                            { it },
                        )
                    }
                },
            )
        }
    }
}

// See https://ktor.io/docs/cors.html
private fun Application.installCORS() {
    install(CORS) {
        anyHost()
        allowHeaders { true }
        allowNonSimpleContentTypes = true
        methods.addAll(listOf(HttpMethod.Get, HttpMethod.Delete, HttpMethod.Post, HttpMethod.Put))
    }
}
