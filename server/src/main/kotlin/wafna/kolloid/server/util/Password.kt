package wafna.kolloid.server.util

import java.security.MessageDigest
import java.security.SecureRandom

private const val HashLength = 64

@Suppress("ArrayInDataClass") // We'll never compare th
data class Password(val salt: ByteArray, val hash: ByteArray)

private fun messageDigest() = MessageDigest.getInstance("SHA-256")

fun hashPassword(password: String): Password {
    val salt = ByteArray(HashLength) { 0 }.also {
        SecureRandom().nextBytes(it)
    }
    val hash = messageDigest()
        .digest(password.toByteArray(Charsets.UTF_8) + salt)
    return Password(salt, hash)
}

fun verifyPassword(given: String, stored: Password): Boolean {
    val hash = messageDigest()
        .digest(given.toByteArray(Charsets.UTF_8) + stored.salt)
    return hash.contentEquals(stored.hash)
}
