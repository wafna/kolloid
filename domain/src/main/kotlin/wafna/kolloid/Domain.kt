@file:Suppress("ConstructorParameterNaming", "ClassNaming")

package wafna.kolloid

import java.util.UUID

private fun newID(): UUID = UUID.randomUUID()

data class Record(val id: UUID, val data: String)

// Tagging interface for the mangling.
interface Mangled

data class RecordWIP(val data: String) : Mangled {
    fun commit(): Record = Record(newID(), data)
}
