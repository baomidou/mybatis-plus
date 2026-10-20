package com.baomidou.mybatisplus.core;

import org.apache.ibatis.scripting.LanguageDriver;
import org.apache.ibatis.scripting.xmltags.XMLLanguageDriver;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * {@link MybatisXMLLanguageDriver} 与 {@link MybatisXMLScriptBuilder} 的动态 SQL 行为测试。<br>
 * 断言前压平空白（{@link #collapse}），因为换行与缩进的数量取决于 XML 排版，会随格式化漂移。<br>
 * 真正要钉住的是各标签保留了哪些片段，以及节点拼接处会不会漏空格。
 */
class MybatisXMLLanguageDriverTest {

    private static final XMLLanguageDriver MYBATIS = new XMLLanguageDriver();

    private static final MybatisXMLLanguageDriver MYBATIS_PLUS = new MybatisXMLLanguageDriver();

    private static final Pattern WHITESPACE = Pattern.compile("\\s+");

    private record Script(String name, String xml, String sql) {
    }

    private static List<Script> scripts() {
        return List.of(
                new Script("whereKeepsEveryTrueClause", """
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
                        """, "select * from `user` WHERE 1=1 and 2=2 and 3=3"),

                new Script("whereStripsLeadingAndOfFirstKeptClause", """
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
                        """, "select * from `user` WHERE 2=2 and 3=3"),

                new Script("whereWithoutWhitespaceBetweenNodes", """
                        <script>
                                        select * from `user`
                                        <where>
                                            <if test="true">1=1</if>and 2=2<if test="true">and 3=3</if>
                                        </where>
                                    </script>
                        """, "select * from `user` WHERE 1=1and 2=2and 3=3"),

                new Script("plainWhereKeepsConditionsOnSeparateLines", """
                        <script>
                                        select * from `user`
                                        where 1=1
                                        <if test="1==1">and id is not null</if>
                                        <if test="1==1">and name is not null</if>
                                    </script>
                        """, "select * from `user` where 1=1 and id is not null and name is not null"),

                new Script("plainWhereKeepsAdjacentConditions", """
                        <script>
                                        select * from `user`
                                        where 1=1
                                        <if test="1==1">and id is not null</if><if test="1==1">and name is not null</if>
                                    </script>
                        """, "select * from `user` where 1=1 and id is not null and name is not null"),

                new Script("whereWithAdjacentIfNodes", """
                        <script>
                                        select * from `user`
                                        <where>
                                        <if test="1==1">and id is not null</if><if test="1==1">and name is not null</if>
                                        </where>
                                    </script>
                        """, "select * from `user` WHERE id is not nulland name is not null"),

                new Script("whereWithIfNodesOnSeparateLines", """
                        <script>
                                        select * from `user`
                                        <where>
                                        <if test="1==1">and id is not null</if>
                                        <if test="1==1">and name is not null</if>
                                        </where>
                                    </script>
                        """, "select * from `user` WHERE id is not null and name is not null"),

                new Script("explicitWhereWithAdjacentIfNodes", """
                        <script>
                                        select * from `user` where 1 = 1
                                        <if test="1==1">and id is not null</if><if test="1==1">and name is not null</if>
                                    </script>
                        """, "select * from `user` where 1 = 1 and id is not null and name is not null"),

                new Script("explicitWhereWithIfNodesOnSeparateLines", """
                        <script>
                                        select * from `user` where 1 = 1
                                        <if test="1==1">and id is not null</if>
                                        <if test="1==1">and name is not null</if>
                                    </script>
                        """, "select * from `user` where 1 = 1 and id is not null and name is not null"),

                new Script("foreachExpandsCollectionToPlaceholders",
                        """
                                <script>
                                                        select * from `user` where 1 = 1 and id in
                                                        <foreach collection='@java.util.Arrays@asList(1,2,3,4,5)' item='item' separator=',' open='(' close=')'>
                                                            #{item}
                                                        </foreach>
                                                    </script>
                                """,
                        "select * from `user` where 1 = 1 and id in ( ? , ? , ? , ? , ? )"),

                new Script("foreachWithWordSeparator",
                        """
                                <script>
                                                        select * from `user` where 1 = 1 and
                                                        <foreach collection='@java.util.Arrays@asList(1,2,3,4,5)' item='item' separator='and'>
                                                            <if test="item == 1">id is not null</if>
                                                            <if test="item == 2">name is not null</if>
                                                            <if test="item == 3">age is not null</if>
                                                        </foreach>
                                                    </script>
                                """,
                        "select * from `user` where 1 = 1 and id is not null and name is not null and age is not null"),

                new Script("setRemovesTrailingComma", """
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
                        """, "update user SET username=?, email=?, bio=? where id=?"),

                new Script("setIgnoresXmlComment", """
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
                        """, "update user SET username=?, email=?, bio=? where id=?"),

                new Script("setKeepsBindDeclaration", """
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
                        """, "update user SET username=?, email=?, bio=? where id=?"),

                new Script("whereIgnoresBlockComment", """
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
                        """, "select * from `user` WHERE 1=1and 2=2and 3=3"),

                new Script("chooseFallsBackToOtherwise", """
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
                        """, "select * from `user` WHERE sex = '男'"),

                new Script("chooseKeepsFirstMatchingWhen", """
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
                        """, "select * from `user` WHERE name like concat(?,'%')"),

                new Script("chooseFollowedByTextAfterWhere", """
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
                        """, "select * from `user` WHERE name like concat(?,'%') and 1=1"));
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("scripts")
    void dynamicSqlKeepsExpectedFragments(Script script) {
        assertThat(collapse(boundSql(MYBATIS_PLUS, script))).isEqualTo(collapse(script.sql()));
    }

    /**
     * {@link MybatisXMLScriptBuilder} 只是给重复的字符串节点加了弱引用缓存，<br>
     * 语义必须与上游 {@link XMLLanguageDriver} 逐字符一致，否则说明缓存改动改变了拼接结果。
     */
    @ParameterizedTest(name = "{0}")
    @MethodSource("scripts")
    void plusBuilderMatchesUpstreamBehaviour(Script script) {
        assertThat(boundSql(MYBATIS_PLUS, script)).isEqualTo(boundSql(MYBATIS, script));
    }

    private static String boundSql(LanguageDriver driver, Script script) {
        return driver.createSqlSource(new MybatisConfiguration(), script.xml(), Object.class).getBoundSql(null)
                .getSql();
    }

    private static String collapse(String sql) {
        return WHITESPACE.matcher(sql).replaceAll(" ").trim();
    }

}
