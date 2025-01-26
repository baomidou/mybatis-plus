package com.baomidou.mybatisplus.core;

import org.apache.ibatis.scripting.xmltags.XMLLanguageDriver;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MybatisXMLLanguageDriverTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(MybatisXMLLanguageDriver.class);

    @Test
    void test1() {
        assertSql("""
            <script>
                            select * from `user`
                            <where>
                                <if test="true">
                                    1=1
                                </if>
                                and 2=2
                                <if test="true">
                                    and 3=3
                                </if>
                            </where>
                        </script>
            """, "select * from `user`  WHERE 1=1  and 2=2  and 3=3");
    }

    @Test
    void test2() {
        assertSql("""
            <script>
                            select * from `user`
                            <where>
                                <if test="false">
                                    1=1
                                </if>
                                and 2=2
                                <if test="true">
                                    and 3=3
                                </if>
                            </where>
                        </script>
            """, "select * from `user`  WHERE  2=2  and 3=3");
    }

    @Test
    void test3() {
        assertSql("""
            <script>
                            select * from `user`
                            <where>
                                <if test="false">
                                    1=1
                                </if>
                                and 2=2
                                <if test="true">
                                    and 3=3
                                </if>
                            </where>
                        </script>
            """, "select * from `user`  WHERE  2=2  and 3=3");
    }

    @Test
    void test4() {
        assertSql("""
            <script>
                            select * from `user`
                            <where>
                                <if test="true">1=1</if>and 2=2<if test="true">and 3=3</if>
                            </where>
                        </script>
            """, "select * from `user`  WHERE 1=1and 2=2and 3=3");

    }

    @Test
    void test5() {
        assertSql("""
            <script>
                            select * from `user`
                            where 1=1
                            <if test="1==1">and id is not null</if>
                            <if test="1==1">and name is not null</if>
                        </script>
            """, "select * from `user`\n" +
            "                where 1=1  and id is not null   and name is not null");
    }

    @Test
    void test6() {
        assertSql("""
            <script>
                            select * from `user`
                            where 1=1
                            <if test="1==1">and id is not null</if><if test="1==1">and name is not null</if>
                        </script>
            """, "select * from `user`\n" +
            "                where 1=1  and id is not null and name is not null");
    }

    @Test
    void test7() {
        assertSql("""
            <script>
                            select * from `user`
                            <where>
                            <if test="1==1">and id is not null</if><if test="1==1">and name is not null</if>
                            </where>
                        </script>
            """, "select * from `user`  WHERE  id is not nulland name is not null");
    }

    @Test
    void test8() {
        assertSql("""
            <script>
                            select * from `user`
                            <where>
                            <if test="1==1">and id is not null</if>
                            <if test="1==1">and name is not null</if>
                            </where>
                        </script>
            """, "select * from `user`  WHERE  id is not null and name is not null");
    }

    @Test
    void test9() {
        assertSql("""
            <script>
                            select * from `user`
                            <where>
                            <if test="1==1">and id is not null</if>
                            <if test="1==1">and name is not null</if>
                            </where>
                        </script>
            """, "select * from `user`  WHERE  id is not null and name is not null");
    }

    @Test
    void test10() {
        assertSql("""
            <script>
                            select * from `user` where 1 = 1
                            <if test="1==1">and id is not null</if><if test="1==1">and name is not null</if>
                        </script>
            """, "select * from `user` where 1 = 1  and id is not null and name is not null");
    }

    @Test
    void test11() {
        assertSql("""
            <script>
                            select * from `user` where 1 = 1
                            <if test="1==1">and id is not null</if>
                            <if test="1==1">and name is not null</if>
                        </script>
            """, "select * from `user` where 1 = 1  and id is not null   and name is not null");
    }

    @Test
    void test12() {
        assertSql("""
            <script>
                            select * from `user` where 1 = 1 and id in
                            <foreach collection='@java.util.Arrays@asList(1,2,3,4,5)' item='item' separator=',' open='(' close=')'>
                                #{item}
                            </foreach>
                        </script>
            """, "select * from `user` where 1 = 1 and id in  (   ?  ,  ?  ,  ?  ,  ?  ,  ?  )");
    }

    @Test
    void test13() {
        assertSql("""
            <script>
                            select * from `user` where 1 = 1 and
                            <foreach collection='@java.util.Arrays@asList(1,2,3,4,5)' item='item' separator='and'>
                                <if test="item == 1">id is not null</if>
                                <if test="item == 2">name is not null</if>
                                <if test="item == 3">age is not null</if>
                            </foreach>
                        </script>
            """, "select * from `user` where 1 = 1 and     id is not null           and name is not null           and age is not null");
    }

    @Test
    void test14() {
        assertSql("""
            <script>
                   update user
                   <set>
                         <if test="true">username=#{username},</if>
                         <if test="false">password=#{password},</if>
                         <if test="true">email=#{email},</if>
                         <if test="true">bio=#{bio},</if>
                       </set>
                     where id=#{id}
            </script>
            """, "update user  SET username=?,  email=?, bio=?  where id=?");
    }

    @Test
    void test15() {
        assertSql("""
            <script>
                   update user
                   <!--这是一条更新语句-->
                   <set>
                         <if test="true">username=#{username},</if>
                         <if test="false">password=#{password},</if>
                         <if test="true">email=#{email},</if>
                         <if test="true">bio=#{bio},</if>
                       </set>
                     where id=#{id}
            </script>
            """, "update user  SET username=?,  email=?, bio=?  where id=?");
    }

    @Test
    void test16() {
        assertSql("""
            <script>
                   update user
                   <!--这是一条更新语句-->
                   <bind name="name" value="test" />
                   <set>
                         <if test="true">username=#{username},</if>
                         <if test="false">password=#{password},</if>
                         <if test="true">email=#{email},</if>
                         <if test="true">bio=#{bio},</if>
                       </set>
                     where id=#{id}
            </script>
            """, "update user    SET username=?,  email=?, bio=?  where id=?");
    }

    @Test
    void test17() {
        assertSql("""
            <script>
                            select * from `user`
                            <where>
                            <!--
                                 查了点东西啊
                                 12345
                                 789
                            -->
                                <if test="true">1=1</if>and 2=2<if test="true">and 3=3</if>
                            </where>
                        </script>
            """, "select * from `user`  WHERE 1=1and 2=2and 3=3");
    }

    @Test
    void test18() {
        assertSql("""
            <script>
                            select * from `user`
                            <where>
                                <choose>
                                    <when test="false">
                                        and age > #{age}
                                    </when>
                                    <when test="false">
                                        and name like concat(#{name},'%')
                                    </when>
                                    <otherwise>
                                        and sex = '男'
                                    </otherwise>
                                </choose>
                            </where>
                        </script>
            """, "select * from `user`  WHERE  sex = '男'");
    }

    @Test
    void test19() {
        assertSql("""
            <script>
                            select * from `user`
                            <where>
                                <choose>
                                    <when test="false">
                                        and age > #{age}
                                    </when>
                                    <when test="true">
                                        and name like concat(#{name},'%')
                                    </when>
                                    <otherwise>
                                        and sex = '男'
                                    </otherwise>
                                </choose>
                            </where>
                        </script>
            """, "select * from `user`  WHERE  name like concat(?,'%')");
    }

    @Test
    void test20() {
        assertSql("""
            <script>
                            select * from `user`
                            <where>
                                <choose>
                                    <when test="false">
                                        and age > #{age}
                                    </when>
                                    <when test="true">and name like concat(#{name},'%')</when>
                                    <otherwise>
                                        and sex = '男'
                                    </otherwise>
                                </choose>
                            </where>
                            and 1=1
                        </script>
            """, "select * from `user`  WHERE  name like concat(?,'%')  and 1=1");
    }



    void runMybatis(String script, String sql) {
        var languageDriver = new XMLLanguageDriver();
        var sqlSource = languageDriver.createSqlSource(new MybatisConfiguration(), script, Object.class);
        LOGGER.info("mybatis parse :{}", sqlSource.getBoundSql(null).getSql());
    }


    void assertSql(String script, String sql) {
        var languageDriver = new MybatisXMLLanguageDriver();
        var sqlSource = languageDriver.createSqlSource(new MybatisConfiguration(), script, Object.class);
        runMybatis(script, sql);
        var boundSql = sqlSource.getBoundSql(null).getSql();
        LOGGER.info("mybatis-plus parse :{}", boundSql);
        Assertions.assertEquals(sql, boundSql);
    }

}
