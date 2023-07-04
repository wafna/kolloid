package wafna.kolloid.server

import arrow.core.Either
import arrow.core.raise.either
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.ApplicationCall
import io.ktor.server.application.call
import io.ktor.server.request.httpMethod
import io.ktor.server.request.receive
import io.ktor.server.request.uri
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.put
import io.ktor.server.routing.route
import wafna.kolloid.Mangled
import wafna.kolloid.Record
import wafna.kolloid.RecordWIP
import wafna.kolloid.util.LazyLogger
import java.util.UUID

fun ApplicationCall.ok() = response.status(HttpStatusCode.OK)
fun ApplicationCall.internalServerError() = response.status(HttpStatusCode.InternalServerError)
fun ApplicationCall.badRequest() = response.status(HttpStatusCode.BadRequest)

private object API

private val log = LazyLogger(API::class)

/**
 * Sets the given status code into the response if the block returns normally.
 */
suspend fun ApplicationCall.bracket(
    status: HttpStatusCode = HttpStatusCode.OK,
    block: suspend ApplicationCall.() -> Unit,
) =
    either {
        Either.catch { block() }.bind()
        response.status(status)
    }.mapLeft { e ->
        log.error(e) { "HTTP Error: ${request.httpMethod.value} ${request.uri}" }
        internalServerError()
    }

// Hack to (temporarily) work around the mangling Kotlin does to the names of the fields in the browser.
data class Record_1(val id_1: UUID, val data_1: String) : Mangled {
    fun domain(): Record = Record(id_1, data_1)
}

/**
 * The browser API.
 */
context(ServerContext)
internal fun Route.api() {
    route("/record") {
        get("") {
            call.bracket {
                db.recordsDAO.fetchAllRecords().let { records ->
                    log.info { "LIST ${records.size}" }
                    respond(records)
                }
            }
        }
        delete("") {
            call.bracket {
                val id = UUID.fromString(parameters["id"])
                log.info { "DELETE $id" }
                if (db.recordsDAO.deleteRecord(id)) {
                    ok()
                } else {
                    badRequest()
                }
            }
        }
        put("") {
            call.bracket {
                val record = receive<RecordWIP>()
                db.recordsDAO.createRecord(record.commit())
                respond(record)
            }
        }
        post("") {
            call.bracket {
                val record = receive<Record_1>()
                db.recordsDAO.updateRecord(record.domain())
            }
        }
    }
}
