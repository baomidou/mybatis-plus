package com.baomidou.mybatisplus.test.kotlin

import com.baomidou.mybatisplus.core.metadata.IPage
import com.baomidou.mybatisplus.extension.plugins.pagination.Page
import com.baomidou.mybatisplus.extension.toolkit.ChainWrappers
import com.baomidou.mybatisplus.extension.toolkit.Db
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.util.*

class ChainWrappersTest : BaseDbTest<UserMapper>() {

    @Test
    fun testQueryChain() {
        val list = ChainWrappers.ktQueryChain(User::class.java).eq(User::id, 1).list()
        Assertions.assertEquals(1, list.size)
        val listRid = ChainWrappers.ktQueryChain(User::class.java).eq(User::roleId, 1).list()
        Assertions.assertEquals(2, listRid.size)
        val oneUser = ChainWrappers.ktQueryChain(User::class.java).eq(User::id, 1).one()
        Assertions.assertEquals("gozei", oneUser.name)
        val count = ChainWrappers.ktQueryChain(User::class.java).eq(User::id, 1).count()
        Assertions.assertEquals(1, count)
        val exists = ChainWrappers.ktQueryChain(User::class.java).eq(User::id, 1).exists()
        Assertions.assertEquals(true, exists)
        val oneIdName = ChainWrappers.ktQueryChain(User::class.java).eq(User::id, 1).eq(User::name, "gozei").one()
        Assertions.assertEquals(1, oneIdName.roleId)
        val page: IPage<User> = Db.page(Page(1, 3), User::class.java)
        val pageU = ChainWrappers.ktQueryChain(User::class.java).page(page)
        Assertions.assertEquals(3, pageU.size)
    }

    @Test
    fun testUpdate() {
        ChainWrappers.ktUpdateChain(User::class.java).eq(User::id, 3).set(User::name, "haku").update()
        Assertions.assertEquals("haku", Db.ktQuery(User::class.java).eq(User::id, 3).one().name)
        ChainWrappers.ktUpdateChain(User::class.java).eq(User::id, 2).set(User::name, "haku").set(User::roleId, 4)
            .update()
        Assertions.assertEquals(4, Db.ktQuery(User::class.java).eq(User::id, 2).one().roleId)
    }

    override fun tableDataSql(): String {
        return "insert into `sys_user`(id,username,role_id) values(1,'gozei',1),(2,'chocolate',2),(3,'sheep',1)"
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
