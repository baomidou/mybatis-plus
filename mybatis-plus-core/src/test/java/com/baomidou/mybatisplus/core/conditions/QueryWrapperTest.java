package com.baomidou.mybatisplus.core.conditions;

import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.exceptions.MybatisPlusException;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import org.apache.ibatis.builder.MapperBuilderAssistant;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.*;

/**
 * @author miemie
 * @since 2021-01-27
 */
class QueryWrapperTest extends BaseWrapperTest {

    @Test
    void testCacheSqlSegment() {
        QueryWrapper<Entity> ew = new QueryWrapper<Entity>()
            .eq("xxx", 123)
            .and(i -> i.eq("andx", 65444).le("ande", 66666))
            .ne("xxx", 222);
        logSqlWhere("xx", ew, "(xxx = ? AND (andx = ? AND ande <= ?) AND xxx <> ?)");
        logSqlWhere("xx", ew, "(xxx = ? AND (andx = ? AND ande <= ?) AND xxx <> ?)");
        ew.gt("x22", 333);
        logSqlWhere("xx", ew, "(xxx = ? AND (andx = ? AND ande <= ?) AND xxx <> ? AND x22 > ?)");
        logSqlWhere("xx", ew, "(xxx = ? AND (andx = ? AND ande <= ?) AND xxx <> ? AND x22 > ?)");
        ew.orderByAsc("column");
        logSqlWhere("xx", ew, "(xxx = ? AND (andx = ? AND ande <= ?) AND xxx <> ? AND x22 > ?) ORDER BY column ASC");
        logSqlWhere("xx", ew, "(xxx = ? AND (andx = ? AND ande <= ?) AND xxx <> ? AND x22 > ?) ORDER BY column ASC");
        logParams(ew);
    }

    @Test
    void testQueryWrapper() {
        logSqlWhere("去除第一个 or,以及自动拼接 and,以及手动拼接 or,以及去除最后的多个or", new QueryWrapper<Entity>().or()
                .ge("age", 3).or().ge("age", 3).ge("age", 3).or().or().or().or(),
            "(age >= ? OR age >= ? AND age >= ?)");

        logSqlWhere("多个 or 相连接,去除多余的 or", new QueryWrapper<Entity>()
                .ge("age", 3).or().or().or().ge("age", 3).or().or().ge("age", 3),
            "(age >= ? OR age >= ? OR age >= ?)");

        logSqlWhere("嵌套,正常嵌套", new QueryWrapper<Entity>()
                .nested(i -> i.eq("id", 1)).eq("id", 1),
            "((id = ?) AND id = ?)");

        logSqlWhere("嵌套,第一个套外的 and 自动消除", new QueryWrapper<Entity>()
                .and(i -> i.eq("id", 1)).eq("id", 1),
            "((id = ?) AND id = ?)");

        logSqlWhere("嵌套,多层嵌套", new QueryWrapper<Entity>()
                .and(i -> i.eq("id", 1).and(j -> j.eq("id", 2))),
            "((id = ? AND (id = ?)))");

        logSqlWhere("嵌套,第一个套外的 or 自动消除", new QueryWrapper<Entity>()
                .or(i -> i.eq("id", 1)).eq("id", 1),
            "((id = ?) AND id = ?)");

        logSqlWhere("嵌套,套内外自动拼接 and", new QueryWrapper<Entity>()
                .eq("id", 11).and(i -> i.eq("id", 1)).eq("id", 1),
            "(id = ? AND (id = ?) AND id = ?)");

        logSqlWhere("嵌套,套内外手动拼接 or,去除套内第一个 or", new QueryWrapper<Entity>()
                .eq("id", 11).or(i -> i.or().eq("id", 1)).or().eq("id", 1),
            "(id = ? OR (id = ?) OR id = ?)");

        logSqlWhere("多个 order by 和 group by 拼接,自动优化顺序,last方法拼接在最后", new QueryWrapper<Entity>()
                .eq("id", 11)
                .last("limit 1")
                .orderByAsc("id", "name", "sex").orderByDesc("age", "txl")
                .groupBy("id", "name", "sex").groupBy("id", "name"),
            "(id = ?) GROUP BY id,name,sex,id,name ORDER BY id ASC,name ASC,sex ASC,age DESC,txl DESC limit 1");

        logSqlWhere("只存在 order by", new QueryWrapper<Entity>()
                .orderByAsc("id", "name", "sex").orderByDesc("age", "txl"),
            "ORDER BY id ASC,name ASC,sex ASC,age DESC,txl DESC");

        logSqlWhere("只存在 group by", new QueryWrapper<Entity>()
                .groupBy("id", "name", "sex").groupBy("id", "name"),
            "GROUP BY id,name,sex,id,name");
    }

    @Test
    void testCompare() {
        QueryWrapper<Entity> queryWrapper = new QueryWrapper<Entity>()
            .allEq(getMap()).allEq((k, v) -> true, getMap())
            .eq("id", 1).ne("id", 1)
            .or().gt("id", 1).ge("id", 1)
            .lt("id", 1).le("id", 1)
            .or().between("id", 1, 2).notBetween("id", 1, 3)
            .like("id", 1).notLike("id", 1).notLikeLeft("id", 3).notLikeRight("id", 4)
            .or().likeLeft("id", 1).likeRight("id", 1);
        logSqlWhere("测试 Compare 下的方法", queryWrapper, "(column1 = ? AND column0 = ? AND nullColumn IS NULL AND column1 = ? AND column0 = ? AND nullColumn IS NULL AND id = ? AND id <> ? OR id > ? AND id >= ? AND id < ? AND id <= ? OR id BETWEEN ? AND ? AND id NOT BETWEEN ? AND ? AND id LIKE ? AND id NOT LIKE ? AND id NOT LIKE ? AND id NOT LIKE ? OR id LIKE ? AND id LIKE ?)");
        logParams(queryWrapper);
    }

    @Test
    void testConditionCompareWhenCheckValIsNull() {
        QueryWrapper<Entity> queryWrapper1 = new QueryWrapper<Entity>()
            .conditionEq("id", null)
            .conditionNe("id", null)
            .or()
            .conditionGt("id", null)
            .conditionGe("id", null)
            .conditionLt("id", null)
            .conditionLe("id", null)
            .or()
            .conditionLike("id", null)
            .conditionNotLike("id", null)
            .conditionNotLikeLeft("id", null)
            .conditionNotLikeRight("id", null)
            .or()
            .conditionLikeLeft("id", null)
            .conditionLikeRight("id", null);
        logSqlWhere("测试 Compare 下非字符val checkValIsNull的方法", queryWrapper1, "");
        logParams(queryWrapper1);
        QueryWrapper<Entity> queryWrapper2 = new QueryWrapper<Entity>()
            .conditionEq("name", null)
            .conditionNe("name", null)
            .or()
            .conditionGt("name", null)
            .conditionGe("name", null)
            .conditionLt("name", null)
            .conditionLe("name", null)
            .or()
            .conditionLike("name", null)
            .conditionNotLike("name", null)
            .conditionNotLikeLeft("name", null)
            .conditionNotLikeRight("name", null)
            .or()
            .conditionLikeLeft("name", null)
            .conditionLikeRight("name", null);
        logSqlWhere("测试 Compare 下字符类型val checkValIsNull的方法", queryWrapper2, "");
        logParams(queryWrapper2);
        QueryWrapper<Entity> queryWrapper3 = new QueryWrapper<Entity>()
            .conditionEq("name", "")
            .conditionNe("name", "")
            .or()
            .conditionGt("name", "")
            .conditionGe("name", "")
            .conditionLt("name", "")
            .conditionLe("name", "")
            .or()
            .conditionLike("name", "")
            .conditionNotLike("name", "")
            .conditionNotLikeLeft("name", "")
            .conditionNotLikeRight("name", "")
            .or()
            .conditionLikeLeft("name", "")
            .conditionLikeRight("name", "");
        logSqlWhere("测试 Compare 下字符val为空字符串 condition的方法", queryWrapper3, "");
        logParams(queryWrapper3);
    }

    @Test
    void testConditionCompareWhenCheckValNotNull() {
        QueryWrapper<Entity> queryWrapper1 = new QueryWrapper<Entity>()
            .conditionEq("id", 1)
            .conditionNe("id", 1)
            .or()
            .conditionGt("id", 1)
            .conditionGe("id", 1)
            .conditionLt("id", 1)
            .conditionLe("id", 1)
            .or()
            .conditionLike("id", 1)
            .conditionNotLike("id", 1)
            .conditionNotLikeLeft("id", 1)
            .conditionNotLikeRight("id", 1)
            .or()
            .conditionLikeLeft("id", 1)
            .conditionLikeRight("id", 1);
        logSqlWhere("测试 Compare 下非字符val CheckValNotNull的方法", queryWrapper1, "(id = ? AND id <> ? OR id > ? AND id >= ? AND id < ? AND id <= ? OR id LIKE ? AND id NOT LIKE ? AND id NOT LIKE ? AND id NOT LIKE ? OR id LIKE ? AND id LIKE ?)");
        logParams(queryWrapper1);
        QueryWrapper<Entity> queryWrapper2 = new QueryWrapper<Entity>()
            .conditionEq("name", "我不是名字")
            .conditionNe("name", "我不是名字")
            .or()
            .conditionGt("name", "我不是名字")
            .conditionGe("name", "我不是名字")
            .conditionLt("name", "我不是名字")
            .conditionLe("name", "我不是名字")
            .or()
            .conditionLike("name", "我不是名字")
            .conditionNotLike("name", "我不是名字")
            .conditionNotLikeLeft("name", "我不是名字")
            .conditionNotLikeRight("name", "我不是名字")
            .or()
            .conditionLikeLeft("name", "我不是名字")
            .conditionLikeRight("name", "我不是名字");
        logSqlWhere("测试 Compare 下字符类型val checkValNotNull的方法", queryWrapper2, "(name = ? AND name <> ? OR name > ? AND name >= ? AND name < ? AND name <= ? OR name LIKE ? AND name NOT LIKE ? AND name NOT LIKE ? AND name NOT LIKE ? OR name LIKE ? AND name LIKE ?)");
        logParams(queryWrapper2);
    }

    @Test
    void testFunc() {
        Entity entity = new Entity();
        QueryWrapper<Entity> queryWrapper = new QueryWrapper<Entity>()
            .isNull("nullColumn").or().isNotNull("notNullColumn")
            .orderByAsc("id").orderByDesc("name", "name2")
            .groupBy("id").groupBy("name", "id2", "name2")
            .in("inColl", getList()).or().notIn("notInColl", getList())
            .in("inArray").notIn("notInArray", 5, 6, 7)
            .eqSql("eqSql", "1")
            .inSql("inSql", "1,2,3,4,5").notInSql("inSql", "1,2,3,4,5")
            .gtSql("gtSql", "1,2,3,4,5").ltSql("ltSql", "1,2,3,4,5")
            .geSql("geSql", "1,2,3,4,5").leSql("leSql", "1,2,3,4,5")
            .having("sum(age) > {0}", 1).having("id is not null")
            .func(entity.getId() != null, j -> j.eq("id", entity.getId()));// 不会npe,也不会加入sql
        logSqlWhere("测试 Func 下的方法", queryWrapper, "(nullColumn IS NULL OR notNullColumn IS NOT NULL AND inColl IN (?,?) OR notInColl NOT IN (?,?) AND inArray IN () AND notInArray NOT IN (?,?,?) AND eqSql = (1) AND inSql IN (1,2,3,4,5) AND inSql NOT IN (1,2,3,4,5) AND gtSql > (1,2,3,4,5) AND ltSql < (1,2,3,4,5) AND geSql >= (1,2,3,4,5) AND leSql <= (1,2,3,4,5)) GROUP BY id,name,id2,name2 HAVING sum(age) > ? AND id is not null ORDER BY id ASC,name DESC,name2 DESC");
        logParams(queryWrapper);
    }

    @Test
    void testConditionFuncWhenInConditionIsEmpty() {
        Object[] emptyObjects = new Object[0];
        QueryWrapper<Entity> queryWrapper = new QueryWrapper<Entity>()
            .conditionIn("inColl", Collections.emptyList())
            .or()
            .conditionNotIn("notInColl", Collections.emptyList())
            .conditionIn("inArray", emptyObjects)
            .conditionNotIn("notInArray", emptyObjects);
        logSqlWhere("测试 Func 下in条件isEmpty的conditionIn方法", queryWrapper, "");
        logParams(queryWrapper);
    }

    @Test
    void testConditionFuncWhenInConditionIsNotEmpty() {
        QueryWrapper<Entity> queryWrapper = new QueryWrapper<Entity>()
            .conditionIn("inColl", getList())
            .or()
            .conditionNotIn("notInColl", getList())
            .conditionIn("inArray", 1, 2, 3)
            .conditionNotIn("notInArray", 1, 2, 3, 4);
        logSqlWhere("测试 Func 下in条件isNotEmpty的conditionIn方法", queryWrapper, "(inColl IN (?,?) OR notInColl NOT IN (?,?) AND inArray IN (?,?,?) AND notInArray NOT IN (?,?,?,?))");
        logParams(queryWrapper);
    }

    @Test
    void testJoin() {
        QueryWrapper<Entity> queryWrapper = new QueryWrapper<Entity>()
            .last("limit 1").or()
            .apply("date_format(column,'%Y-%m-%d') = '2008-08-08'")
            .apply("date_format(column,'%Y-%m-%d') = {0}", LocalDate.now())
            .or().exists("select id from table where age = 1")
            .or().notExists("select id from table where age = 1");
        logSqlWhere("测试 Join 下的方法", queryWrapper, "(date_format(column,'%Y-%m-%d') = '2008-08-08' AND date_format(column,'%Y-%m-%d') = ? OR EXISTS (select id from table where age = 1) OR NOT EXISTS (select id from table where age = 1)) limit 1");
        logParams(queryWrapper);
    }

    @Test
    void testNested() {
        QueryWrapper<Entity> queryWrapper = new QueryWrapper<Entity>()
            .and(i -> i.eq("id", 1).nested(j -> j.ne("id", 2)))
            .or(i -> i.eq("id", 1).and(j -> j.ne("id", 2)))
            .nested(i -> i.eq("id", 1).or(j -> j.ne("id", 2)))
            .not(i -> i.eq("id", 1).or(j -> j.ne("id", 2)));
        logSqlWhere("测试 Nested 下的方法", queryWrapper, "((id = ? AND (id <> ?)) OR (id = ? AND (id <> ?)) AND (id = ? OR (id <> ?)) AND NOT (id = ? OR (id <> ?)))");
        logParams(queryWrapper);
    }

    @Test
    void testPluralLambda() {
        TableInfoHelper.initTableInfo(new MapperBuilderAssistant(new MybatisConfiguration(), ""), Entity.class);
        QueryWrapper<Entity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(Entity::getName, "sss");
        queryWrapper.lambda().eq(Entity::getName, "sss2");
        logSqlWhere("测试 PluralLambda", queryWrapper, "(username = ? AND username = ?)");
        logParams(queryWrapper);
    }

    @Test
    void testInEmptyColl() {
        QueryWrapper<Entity> queryWrapper = new QueryWrapper<Entity>().in("xxx", Collections.emptyList());
        logSqlWhere("测试 empty 的 coll", queryWrapper, "(xxx IN ())");
    }

    @Test
    void testExistsValue() {
        QueryWrapper<Entity> wrapper = new QueryWrapper<Entity>().eq("a", "b")
            .exists("select 1 from xxx where id = {0} and name = {1}", 1, "Bob");
        logSqlWhere("testExistsValue", wrapper, "(a = ? AND EXISTS (select 1 from xxx where id = ? and name = ?))");
        logParams(wrapper);
        wrapper = new QueryWrapper<Entity>().eq("a", "b")
            .notExists("select 1 from xxx where id = {0} and name = {1}", 1, "Bob");
        logSqlWhere("testNotExistsValue", wrapper, "(a = ? AND NOT EXISTS (select 1 from xxx where id = ? and name = ?))");
        logParams(wrapper);
    }

    @Test
    void testCheckSqlInjection() {
        QueryWrapper qw = new QueryWrapper<Entity>().checkSqlInjection().eq("a", "b");
        Assertions.assertEquals("WHERE (a = #{ew.paramNameValuePairs.MPGENVAL1})", qw.getCustomSqlSegment());

        qw.orderByAsc("select 1 from xxx");
        Assertions.assertThrows(MybatisPlusException.class, () -> qw.getCustomSqlSegment());
    }

    @Test
    void testGroupBy(){
        Assertions.assertEquals("GROUP BY id", new QueryWrapper<Entity>().groupBy("id").getSqlSegment().trim());
        Assertions.assertEquals("GROUP BY id,name", new QueryWrapper<Entity>().groupBy(Arrays.asList("id", "name")).getSqlSegment().trim());
        Assertions.assertEquals("GROUP BY id,name", new QueryWrapper<Entity>().groupBy(List.of("id", "name")).getSqlSegment().trim());
        Assertions.assertEquals("GROUP BY id,name", new QueryWrapper<Entity>().groupBy(new ArrayList<>() {{
            add("id");
            add("name");
        }}).getSqlSegment().trim());
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
}
