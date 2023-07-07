package wafna.kolloid.db

import java.util.HexFormat
import java.util.UUID
import org.testng.Assert
import org.testng.annotations.Test
import wafna.kolloid.UserWIP
import wafna.kolloid.server.util.PasswordHash
import wafna.kolloid.server.util.hashPassword
import wafna.kolloid.server.util.verifyPassword

private infix fun Int.shouldEqual(i: Int) {
    Assert.assertEquals(this, i)
}

private infix fun Any.shouldEqual(i: Any) {
    Assert.assertEquals(this, i)
}

class AppDBTest : PGContainer() {
    @Test(groups = ["integration"])
    fun test() {
        val huey = UserWIP("Huey").commit().also { db.users.create(it) }
        val dewey = UserWIP("Dewey").commit().also { db.users.create(it) }
        val louie = UserWIP("Louie").commit().also { db.users.create(it) }

        db.users.fetchAll().size shouldEqual 4 // admin
        db.users.byId(huey.id)!! shouldEqual huey
        db.users.byId(dewey.id)!! shouldEqual dewey
        db.users.byId(louie.id)!! shouldEqual louie
        db.users.search("y").size shouldEqual 2

        val password = "password"
        val hashed = hashPassword(password)
        db.passwords.create(UserPasswordHash(huey.id, hashed.salt, hashed.hash))
        db.passwords.byUserId(huey.id)!!.also {
            it.salt shouldEqual hashed.salt
            it.hash shouldEqual hashed.hash
            Assert.assertTrue(verifyPassword(password, PasswordHash(it.salt, it.hash)))
        }

        // For pasting into the user bootstrap DDL.
//        hashPassword("root").let {
//            println(HexFormat.of().formatHex(it.salt))
//            println(HexFormat.of().formatHex(it.hash))
//        }

        db.users.byId(UUID.fromString("f0abd1a5-d9b9-4b15-bc35-41138dfb781d"))!!.also {
            it.username shouldEqual "admin"
            db.passwords.byUserId(it.id)!!.also {
                Assert.assertTrue(verifyPassword("root", PasswordHash(it.salt, it.hash)))
            }
        }

    }
}
