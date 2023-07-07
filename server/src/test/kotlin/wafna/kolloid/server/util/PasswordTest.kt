package wafna.kolloid.server.util

import org.testng.annotations.Test
import java.security.SecureRandom

@Test(groups = ["unit"])
class PasswordTest {

    private fun randomPassword(length: Int): String {
        require(8 <= length && length <= 64)
        return buildString {
            ByteArray(length) { 0 }.also { SecureRandom().nextBytes(it) }
                .forEach { append(it.toInt().toChar()) }
        }
    }

    fun testPasswordHashing() {
        (8..64).forEach { length ->
            repeat(32) {
                val password = randomPassword(length)
                val hashed = hashPassword(password)
                assert(verifyPassword(password, hashed))
            }
        }
    }
}
