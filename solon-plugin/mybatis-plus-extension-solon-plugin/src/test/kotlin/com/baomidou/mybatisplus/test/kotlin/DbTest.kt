package com.baomidou.mybatisplus.test.kotlin

import com.baomidou.mybatisplus.extension.toolkit.Db
import org.apache.ibatis.exceptions.PersistenceException
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import java.util.*


class DbTest : BaseDbTest<UserMapper>() {

    @Test
    fun testQuery() {
        val list = Db.ktQuery(User::class.java).eq(User::id, 1).list()
        Assertions.assertEquals(1, list.size)
        val user1 = Db.ktQuery(User::class.java).eq(User::id, 1).one()
        Assertions.assertEquals("gozei", user1.name)
        val user2 = Db.ktQuery(User::class.java).eq(User::name, "gozei").eq(User::id, 1).one()
        Assertions.assertEquals(1, user2::id.get())
        val user3 = Db.ktQuery(User::class.java).list().first()
        Assertions.assertEquals("gozei", user3.name)
        val exists = Db.ktQuery(User::class.java).eq(User::id, 1).exists()
        Assertions.assertTrue(exists)
    }

    @Test
    fun testUpdate() {
        Db.ktUpdate(User::class.java).eq(User::id, 1).set(User::name, "sheep").update()
        Assertions.assertEquals("sheep", Db.ktQuery(User::class.java).eq(User::id, 1).one().name)
        Db.ktUpdate(User::class.java).eq(User::name, "gozei").set(User::name, "sheep").update()
        Assertions.assertEquals("sheep", Db.ktQuery(User::class.java).eq(User::id, 1).one().name)
    }

    @Test
    fun exceptionTest() {
        assertThrows(PersistenceException::class.java) {
            Db.ktQuery(User::class.java).eq(null, 1).one()
        }
    }

    @Test
    fun remove() {
        val isSuccessId = Db.remove(Db.ktQuery(User::class.java).eq(User::id, 1).wrapper)
        Assertions.assertTrue(isSuccessId)
        val isSuccessName = Db.remove(Db.ktQuery(User::class.java).eq(User::name, "gozei").wrapper)
        Assertions.assertFalse(isSuccessName)
    }

    override fun tableDataSql(): String {
        return "insert into `sys_user`(id,username,role_id) values(1,'gozei',null),(2,'chocolate',null)"
    }

    override fun tableSql(): List<String>? {
        return Arrays.asList(
            "drop table if exists `sys_user`", "CREATE TABLE IF NOT EXISTS `sys_user` (" +
                "id INT NOT NULL," +
                "username VARCHAR(30) NULL DEFAULT NULL," +
                "role_id INT NULL DEFAULT NULL," +
                "PRIMARY KEY (id))"
        )
    }
}
