/*
 * Copyright (c) 2011-2020, hubin (jobob@qq.com).
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.baomidou.mybatisplus.core.test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import com.baomidou.mybatisplus.core.conditions.ISqlSegment;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.TableInfoHelper;

public class WrapperTest {

    private void log(String message) {
        System.out.println(message);
    }

    private void logSqlSegment(String explain, ISqlSegment sqlSegment) {
        System.out.println(String.format(" ↓ ↓ ↓ ↓ ↓ ↓ ↓ ↓ ↓ ↓ ↓ ↓ ↓ ↓   ->(%s)<-   ↓ ↓ ↓ ↓ ↓ ↓ ↓ ↓ ↓ ↓ ↓ ↓ ↓ ↓", explain));
        System.out.println(sqlSegment.getSqlSegment());
    }

    private <T> void logParams(QueryWrapper<T> wrapper) {
        wrapper.getParamNameValuePairs().forEach((k, v) ->
            System.out.println("key: '" + k + "'\t\tvalue: '" + v + "'"));
    }

    @Test
    public void test() {
        Wrapper<User> wrapper = new QueryWrapper<User>().lambda().eq(User::getName, 123)
            .or(c -> c.eq(User::getRoleId, 1).eq(User::getId, 2))
            .eq(User::getId, 1);
        log(wrapper.getSqlSegment());

    }

    @Test
    public void test1() {
        QueryWrapper<User> ew = new QueryWrapper<User>()
            .eq("xxx", 123)
            .and(i -> i.eq("andx", 65444).le("ande", 66666))
            .ne("xxx", 222);
        log(ew.getSqlSegment());
        ew.getParamNameValuePairs().forEach((k, v) -> System.out.println("key = " + k + " ; value = " + v));
    }

    @Test
    public void test2() {
        UpdateWrapper<User> ew = new UpdateWrapper<User>()
            .set("name", "三毛").set("id", 1)
            .eq("xxx", 123)
            .and(i -> i.eq("andx", 65444).le("ande", 66666))
            .ne("xxx", 222);
        log(ew.getSqlSet());
        log(ew.getSqlSegment());
    }

    @Test
    public void test3() {
        UpdateWrapper<User> ew = new UpdateWrapper<User>()
            .setSql("abc=1,def=2").eq("id", 1).ge("age", 3);
        log(ew.getSqlSet());
        log(ew.getSqlSegment());
    }

    @Test
    public void testQueryWrapper() {
        logSqlSegment("去除第一个 or,以及自动拼接 and,以及手动拼接 or,以及去除最后的多个or", new QueryWrapper<User>().or()
            .ge("age", 3).or().ge("age", 3).ge("age", 3).or().or().or().or());

        logSqlSegment("多个 or 相连接,去除多余的 or", new QueryWrapper<User>()
            .ge("age", 3).or().or().or().ge("age", 3).or().or().ge("age", 3));

        logSqlSegment("嵌套,正常嵌套", new QueryWrapper<User>()
            .nested(i -> i.eq("id", 1)).eq("id", 1));

        logSqlSegment("嵌套,第一个套外的 and 自动消除", new QueryWrapper<User>()
            .and(i -> i.eq("id", 1)).eq("id", 1));

        logSqlSegment("嵌套,多层嵌套", new QueryWrapper<User>()
            .and(i -> i.eq("id", 1).and(j -> j.eq("id", 1))));

        logSqlSegment("嵌套,第一个套外的 or 自动消除", new QueryWrapper<User>()
            .or(i -> i.eq("id", 1)).eq("id", 1));

        logSqlSegment("嵌套,套内外自动拼接 and", new QueryWrapper<User>()
            .eq("id", 11).and(i -> i.eq("id", 1)).eq("id", 1));

        logSqlSegment("嵌套,套内外手动拼接 or,去除套内第一个 or", new QueryWrapper<User>()
            .eq("id", 11).or(i -> i.or().eq("id", 1)).or().eq("id", 1));

        logSqlSegment("多个 order by 和 group by 拼接,自动优化顺序,last方法拼接在最后", new QueryWrapper<User>()
            .eq("id", 11)
            .last("limit 1")
            .orderByAsc("id", "name", "sex").orderByDesc("age", "txl")
            .groupBy("id", "name", "sex").groupBy("id", "name"));

        logSqlSegment("只存在 order by", new QueryWrapper<User>()
            .orderByAsc("id", "name", "sex").orderByDesc("age", "txl"));

        logSqlSegment("只存在 group by", new QueryWrapper<User>()
            .groupBy("id", "name", "sex").groupBy("id", "name"));
    }

    @Test
    public void testCompare() {
        QueryWrapper<User> queryWrapper = new QueryWrapper<User>()
            .allEq(getMap()).allEq((k, v) -> true, getMap())
            .eq("id", 1).ne("id", 1)
            .or().gt("id", 1).ge("id", 1)
            .lt("id", 1).le("id", 1)
            .or().between("id", 1, 2).notBetween("id", 1, 3)
            .like("id", 1).notLike("id", 1)
            .or().likeLeft("id", 1).likeRight("id", 1);
        logSqlSegment("测试 Compare 下的方法", queryWrapper);
        logParams(queryWrapper);
    }

    @Test
    public void testFunc() {
        QueryWrapper<User> queryWrapper = new QueryWrapper<User>()//todo in 方法是不是各个加个后缀好点
            .isNull("nullColumn").or().isNotNull("notNullColumn")
            .orderByAsc("id").orderByDesc("name")
            .groupBy("id", "name").groupBy("id2", "name2")
            .in("inColl", getList()).or().notIn("notInColl", getList())
            .in("inArray").notIn("notInArray", 1, 2, 3)
            .inSql("inSql", "1,2,3,4,5").notInSql("inSql", "1,2,3,4,5")
            .having("sum(age) > {0}", 1).having("id is not null");
        logSqlSegment("测试 Func 下的方法", queryWrapper);
        logParams(queryWrapper);
    }

    @Test
    public void testJoin() {
        QueryWrapper<User> queryWrapper = new QueryWrapper<User>()
            .last("limit 1").or()
            .apply("date_format(column,'%Y-%m-%d') = '2008-08-08'")
            .apply("date_format(column,'%Y-%m-%d') = {0}", LocalDate.now())
            .or().exists("select id from table where age = 1")
            .or().notExists("select id from table where age = 1");
        logSqlSegment("测试 Join 下的方法", queryWrapper);
        logParams(queryWrapper);
    }

    @Test
    public void testNested() {
        QueryWrapper<User> queryWrapper = new QueryWrapper<User>()
            .and(i -> i.eq("id", 1).nested(j -> j.ne("id", 2)))
            .or(i -> i.eq("id", 1).and(j -> j.ne("id", 2)))
            .nested(i -> i.eq("id", 1).or(j -> j.ne("id", 2)));
        logSqlSegment("测试 Nested 下的方法", queryWrapper);
        logParams(queryWrapper);
    }

    @Test
    public void testPluralLambda() {
        TableInfoHelper.initTableInfo(null, User.class);
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(User::getName,"sss");
        queryWrapper.lambda().eq(User::getName,"sss2");
        logSqlSegment("测试 PluralLambda", queryWrapper);
        logParams(queryWrapper);
    }

    private List<Object> getList() {
        List<Object> list = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            list.add(i);
        }
        return list;
    }

    private Map<String, Object> getMap() {
        Map<String, Object> map = new HashMap<>();
        for (int i = 0; i < 2; i++) {
            map.put("column" + i, i);
        }
        map.put("nullColumn", null);
        return map;
    }

//    public void test() {
//        String sql = new QueryWrapper()
//            .where("b.age > 18", condition ->
//                condition.and("b.type = 'rabid'")
//                    .or(nested -> nested.apply("name='12'").and("age=1"))
//                    .notIn("ads,2112,212")
//                    .last("LIMIT 1")
//            ).sqlSegment();
//
//        log(sql);
//        assertEquals("WHERE b.age > 18 AND b.type = 'rabid' OR ( name='12' AND age=1 ) NOT IN ( ads,2112,212 ) LIMIT 1", sql);
//    }

}
