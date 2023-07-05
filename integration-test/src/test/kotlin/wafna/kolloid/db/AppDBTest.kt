package wafna.kolloid.db

import org.testng.Assert
import org.testng.annotations.Test
import wafna.kolloid.UserWIP

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

        db.passwords.create(Password(huey.id, "salt".toByteArray(), "hash".toByteArray()))
    }
}
