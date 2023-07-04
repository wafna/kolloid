package wafna.kolloid.client

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.headers
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.http.HeadersBuilder
import io.ktor.http.HttpHeaders
import io.ktor.serialization.gson.gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import wafna.kolloid.Record
import wafna.kolloid.RecordWIP
import java.util.UUID

private fun HeadersBuilder.acceptJson() =
    append(HttpHeaders.Accept, "application/json")

private fun HeadersBuilder.sendJson() =
    append(HttpHeaders.ContentType, "application/json")

/**
 * Should be an integration test, but is just an ad hoc exercise of the API.
 */
@Suppress("MagicNumber")
fun main() = runBlocking(Dispatchers.IO) {
    HttpClient(CIO) {
        expectSuccess = true
        install(Logging) {
            level = LogLevel.ALL
        }
        install(ContentNegotiation) {
            gson {
                serializeNulls()
            }
        }
    }.use { client ->

        val baseURL = "http://localhost:8081/api"

        // Define the API.

        suspend fun list(): List<Record> = client
            .get("$baseURL/record") {
                headers {
                    acceptJson()
                }
            }
            .body<List<Record>>()
            .also { records ->
                println(
                    """RECORDS
                      |${records.joinToString("\n") { "   $it" }}
                    """.trimMargin(),
                )
            }

        suspend fun update(record: Record) = client
            .post("$baseURL/record") {
                headers {
                    sendJson()
                }
                setBody(record)
            }

        suspend fun create(record: RecordWIP) = client
            .put("$baseURL/record") {
                headers {
                    sendJson()
                    acceptJson()
                }
                setBody(record)
            }
            .body<Record>()

        suspend fun delete(id: UUID) = client
            .delete("$baseURL/record?id=$id")

        // Do stuff...

        // This leaves the database in the same state as the seed data provided by the server (except for ids).
        suspend fun lawyerUp() {
            list().forEach { record ->
                delete(record.id)
            }

            create(RecordWIP("Huey"))
            create(RecordWIP("Dewey"))
            create(RecordWIP("Louie"))
        }

        // Start in a known state.
        lawyerUp()

        list().also { records ->
            require(3 == records.size)
            update(records[0].let { it.copy(data = "UPDATED-${it.data}") })
        }
        list().also { records ->
            require(3 == records.size)
            delete(records[0].id)
            require(2 == list().size)
        }
        create(RecordWIP("CREATED")).also {
            println("CREATE $it")
        }
        list().also { records ->
            require(3 == records.size)
        }

        // End in a known state.
        lawyerUp()

        Unit
    }
}
