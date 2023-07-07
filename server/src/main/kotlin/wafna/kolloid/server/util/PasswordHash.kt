package wafna.kolloid.server.util

import java.security.MessageDigest
import java.security.SecureRandom

private const val HashLength = 64

@Suppress("ArrayInDataClass") // We'll never compare th
data class PasswordHash(val salt: ByteArray, val hash: ByteArray)

private fun messageDigest() = MessageDigest.getInstance("SHA-256")

fun hashPassword(password: String): PasswordHash {
    val salt = ByteArray(HashLength) { 0 }.also {
        SecureRandom().nextBytes(it)
    }
    val hash = messageDigest()
        .digest(password.toByteArray(Charsets.UTF_8) + salt)
    return PasswordHash(salt, hash)
}

fun verifyPassword(given: String, stored: PasswordHash): Boolean {
    val hash = messageDigest()
        .digest(given.toByteArray(Charsets.UTF_8) + stored.salt)
    // Constant time comparison.
    return hash.withIndex().fold(true) { v, h ->
        stored.hash[h.index] == h.value && v
    }
}
