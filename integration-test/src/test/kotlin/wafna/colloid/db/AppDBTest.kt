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
    @Test
    fun test() {
        val huey = RecordWIP("Huey").commit().also { db.recordsDAO.create(it) }
        val dewey = RecordWIP("Dewey").commit().also { db.recordsDAO.create(it) }
        val louie = RecordWIP("Louie").commit().also { db.recordsDAO.create(it) }

        db.recordsDAO.fetchAll().size shouldEqual 3
        db.recordsDAO.byId(huey.id)!! shouldEqual huey
        db.recordsDAO.byId(dewey.id)!! shouldEqual dewey
        db.recordsDAO.byId(louie.id)!! shouldEqual louie
    }
}
