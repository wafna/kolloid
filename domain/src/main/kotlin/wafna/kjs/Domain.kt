@file:Suppress("ConstructorParameterNaming", "ClassNaming")

package wafna.kjs

import java.util.UUID

private fun newID(): UUID = UUID.randomUUID()

data class Record(val id: UUID, val data: String)

// Tagging interface for the mangling.
interface Mangled

data class RecordWIP(val data_1: String) : Mangled {
    fun commit(): Record = Record(newID(), data_1)
}
