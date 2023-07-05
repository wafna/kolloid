package wafna.kolloid.db

import org.testng.Assert
import org.testng.annotations.Test
import wafna.colloid.db.PGContainer
import wafna.kolloid.RecordWIP

private infix fun Int.shouldEqual(i: Int) {
    Assert.assertEquals(this, i)
}

private infix fun Any.shouldEqual(i: Any) {
    Assert.assertEquals(this, i)
}

class AppDBTest : PGContainer() {
    @Test(groups = ["integration"])
    fun test() {
        val huey = RecordWIP("Huey").commit().also { db.records.create(it) }
        val dewey = RecordWIP("Dewey").commit().also { db.records.create(it) }
        val louie = RecordWIP("Louie").commit().also { db.records.create(it) }

        db.records.fetchAll().size shouldEqual 3
        db.records.byId(huey.id)!! shouldEqual huey
        db.records.byId(dewey.id)!! shouldEqual dewey
        db.records.byId(louie.id)!! shouldEqual louie
    }
}
