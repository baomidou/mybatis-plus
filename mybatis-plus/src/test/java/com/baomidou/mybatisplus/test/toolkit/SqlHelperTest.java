package com.baomidou.mybatisplus.test.toolkit;

import com.baomidou.mybatisplus.core.toolkit.Assert;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.toolkit.SqlHelper;
import com.baomidou.mybatisplus.test.BaseDbTest;
import com.baomidou.mybatisplus.test.rewrite.Entity;
import com.baomidou.mybatisplus.test.rewrite.EntityMapper;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

/**
 * SqlHelper 工具类测试
 *
 * @author <achao1441470436@gmail.com>
 * @since 2020-09-15
 */
public class SqlHelperTest extends BaseDbTest<EntityMapper> {

    @Test
    public void testGetMapperAndExecute() {

        List<Entity> entityList = SqlHelper.execute(Entity.class, m -> m.selectList(Wrappers.lambdaQuery()));

        Entity ruben = new Entity();
        ruben.setId(1L);
        ruben.setName("ruben");
        Entity aChao = new Entity();
        aChao.setId(2L);
        aChao.setName("a chao");
        Assert.isTrue(entityList.equals(Arrays.asList(ruben, aChao)), "There is something wrong,please check your environment!");
    }

    @Override
    protected String tableDataSql() {
        return "insert into entity(id,name) values(1,'ruben'),(2,'a chao');";
    }

    @Override
    protected List<String> tableSql() {
        return Arrays.asList("drop table if exists entity", "CREATE TABLE IF NOT EXISTS entity (" +
            "id BIGINT NOT NULL," +
            "name VARCHAR(30) NULL DEFAULT NULL," +
            "PRIMARY KEY (id))");
    }

}
