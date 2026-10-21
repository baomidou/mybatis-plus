package com.baomidou.mybatisplus.test.kotlin

import com.baomidou.mybatisplus.extension.toolkit.Db
import org.apache.ibatis.exceptions.PersistenceException
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test
import java.util.*


class DbTest : DbTestSupport<UserMapper>() {

    @Test
    fun testQuery() {
        val list = Db.ktQuery(User::class.java).eq(User::id, 1).list()
        assertThat(list).hasSize(1)
        val user1 = Db.ktQuery(User::class.java).eq(User::id, 1).one()
        assertThat(user1.name).isEqualTo("gozei")
        val user2 = Db.ktQuery(User::class.java).eq(User::name, "gozei").eq(User::id, 1).one()
        assertThat(user2::id.get()).isEqualTo(1)
        val user3 = Db.ktQuery(User::class.java).list().first()
        assertThat(user3.name).isEqualTo("gozei")
        val exists = Db.ktQuery(User::class.java).eq(User::id, 1).exists()
        assertThat(exists).isTrue()
    }

    @Test
    fun testUpdate() {
        Db.ktUpdate(User::class.java).eq(User::id, 1).set(User::name, "sheep").update()
        assertThat(Db.ktQuery(User::class.java).eq(User::id, 1).one().name).isEqualTo("sheep")
        Db.ktUpdate(User::class.java).eq(User::name, "gozei").set(User::name, "sheep").update()
        assertThat(Db.ktQuery(User::class.java).eq(User::id, 1).one().name).isEqualTo("sheep")
    }

    @Test
    fun exceptionTest() {
        assertThatThrownBy {
            Db.ktQuery(User::class.java).eq(null, 1).one()
        }.isInstanceOf(PersistenceException::class.java)
    }

    @Test
    fun remove() {
        val isSuccessId = Db.remove(Db.ktQuery(User::class.java).eq(User::id, 1).wrapper)
        assertThat(isSuccessId).isTrue()
        val isSuccessName = Db.remove(Db.ktQuery(User::class.java).eq(User::name, "gozei").wrapper)
        assertThat(isSuccessName).isFalse()
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
