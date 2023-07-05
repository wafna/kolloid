package wafna.kolloid.db

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

        db.users.fetchAll().size shouldEqual 3
        db.users.byId(huey.id)!! shouldEqual huey
        db.users.byId(dewey.id)!! shouldEqual dewey
        db.users.byId(louie.id)!! shouldEqual louie

        val password = "password"
        val hashed = hashPassword(password)
        db.passwords.create(UserPasswordHash(huey.id, hashed.salt, hashed.hash))
        db.passwords.byUserId(huey.id)!!.also {
            it.salt shouldEqual hashed.salt
            it.hash shouldEqual hashed.hash
            Assert.assertTrue(verifyPassword(password, PasswordHash(it.salt, it.hash)))
        }

        Assert.assertEquals(db.users.search("y").size, 2)
    }
}
