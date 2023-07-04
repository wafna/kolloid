package wafna.kolloid.db

data class DatabaseConfig(val jdbcUrl: String, val username: String, val password: String, val maximumPoolSize: Int)

fun <T>List<T>.unique(): T? = when (size) {
    0 -> null
    1 -> first()
    else -> throw IllegalStateException("Expected 0 or 1 elements, found $size.")
}
