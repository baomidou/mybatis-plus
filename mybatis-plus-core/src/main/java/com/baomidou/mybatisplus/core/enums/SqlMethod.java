/*
 * Copyright (c) 2011-2025, baomidou (jobob@qq.com).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.baomidou.mybatisplus.core.enums;

import com.baomidou.mybatisplus.core.toolkit.sql.SqlTemplate;

/**
 * MybatisPlus 支持 SQL 方法
 *
 * @author hubin
 * @since 2016-01-23
 */
public enum SqlMethod {

    /**
     * 插入
     */
    INSERT_ONE("insert", "插入一条数据（选择字段插入）", SqlTemplate.of3(
            (String a, String b, String c) -> "<script>\nINSERT INTO " + a + " " + b + " VALUES " + c + "\n</script>")),
    UPSERT_ONE("upsert", "Phoenix插入一条数据（选择字段插入）", SqlTemplate.of3(
            (String a, String b, String c) -> "<script>\nUPSERT INTO " + a + " " + b + " VALUES " + c + "\n</script>")),

    /**
     * 删除
     */
    DELETE_BY_ID("deleteById", "根据ID 删除一条数据",
            SqlTemplate.of3((String a, String b, String c) -> "DELETE FROM " + a + " WHERE " + b + "=#{" + c + "}")),
    @Deprecated
    DELETE_BY_MAP("deleteByMap", "根据columnMap 条件删除记录",
            SqlTemplate.of2((String a, String b) -> "<script>\nDELETE FROM " + a + " " + b + "\n</script>")),
    DELETE("delete", "根据 entity 条件删除记录", SqlTemplate
            .of3((String a, String b, String c) -> "<script>\nDELETE FROM " + a + " " + b + " " + c + "\n</script>")),

    /**
     * @deprecated 3.5.7 {@link #DELETE_BY_IDS}
     */
    @Deprecated
    DELETE_BATCH_BY_IDS("deleteBatchIds", "根据ID集合，批量删除数据",
            SqlTemplate.of3(
                    (String a, String b, String c) -> "<script>\nDELETE FROM " + a + " WHERE " + b + " IN (" + c
                            + ")\n</script>")),
    /**
     * @since 3.5.7
     */
    DELETE_BY_IDS("deleteByIds", "根据ID集合，批量删除数据",
            SqlTemplate.of3(
                    (String a, String b, String c) -> "<script>\nDELETE FROM " + a + " WHERE " + b + " IN (" + c
                            + ")\n</script>")),

    /**
     * 逻辑删除
     */
    LOGIC_DELETE_BY_ID("deleteById", "根据ID 逻辑删除一条数据",
            SqlTemplate.of5(
                    (String a, String b, String c, String d, String e) -> "<script>\nUPDATE " + a + " " + b + " WHERE "
                            + c + "=#{" + d + "} " + e + "\n</script>")),
    LOGIC_DELETE_BY_MAP("deleteByMap", "根据columnMap 条件逻辑删除记录",
            SqlTemplate.of3(
                    (String a, String b, String c) -> "<script>\nUPDATE " + a + " " + b + " " + c + "\n</script>")),
    LOGIC_DELETE("delete", "根据 entity 条件逻辑删除记录",
            SqlTemplate.of4(
                    (String a, String b, String c, String d) -> "<script>\nUPDATE " + a + " " + b + " " + c + " " + d
                            + "\n</script>")),
    /**
     * @deprecated 3.5.7 {@link #LOGIC_DELETE_BY_IDS}
     */
    @Deprecated
    LOGIC_DELETE_BATCH_BY_IDS("deleteBatchIds", "根据ID集合，批量逻辑删除数据",
            SqlTemplate.of5(
                    (String a, String b, String c, String d, String e) -> "<script>\nUPDATE " + a + " " + b + " WHERE "
                            + c + " IN (" + d + ") " + e + "\n</script>")),
    /**
     * @since 3.5.7
     */
    LOGIC_DELETE_BY_IDS("deleteByIds", "根据ID集合，批量逻辑删除数据",
            SqlTemplate.of5(
                    (String a, String b, String c, String d, String e) -> "<script>\nUPDATE " + a + " " + b + " WHERE "
                            + c + " IN (" + d + ") " + e + "\n</script>")),

    /**
     * 修改
     */
    UPDATE_BY_ID("updateById", "根据ID 选择修改数据",
            SqlTemplate.of5(
                    (String a, String b, String c, String d, String e) -> "<script>\nUPDATE " + a + " " + b + " WHERE "
                            + c + "=#{" + d + "} " + e + "\n</script>")),
    UPDATE("update", "根据 whereEntity 条件，更新记录",
            SqlTemplate.of4(
                    (String a, String b, String c, String d) -> "<script>\nUPDATE " + a + " " + b + " " + c + " " + d
                            + "\n</script>")),

    /**
     * 逻辑删除 -> 修改
     */
    LOGIC_UPDATE_BY_ID("updateById", "根据ID 修改数据",
            SqlTemplate.of5(
                    (String a, String b, String c, String d, String e) -> "<script>\nUPDATE " + a + " " + b + " WHERE "
                            + c + "=#{" + d + "} " + e + "\n</script>")),

    /**
     * 查询
     */
    SELECT_BY_ID("selectById", "根据ID 查询一条数据",
            SqlTemplate.of5(
                    (String a, String b, String c, String d, String e) -> "SELECT " + a + " FROM " + b + " WHERE " + c
                            + "=#{" + d + "} " + e)),
    @Deprecated
    SELECT_BY_MAP("selectByMap", "根据columnMap 查询一条数据",
            SqlTemplate.of3(
                    (String a, String b, String c) -> "<script>SELECT " + a + " FROM " + b + " " + c + "</script>")),
    /**
     * @deprecated 3.5.8 {@link #SELECT_BY_IDS}
     */
    @Deprecated
    SELECT_BATCH_BY_IDS("selectBatchIds", "根据ID集合，批量查询数据",
            SqlTemplate.of5(
                    (String a, String b, String c, String d, String e) -> "<script>SELECT " + a + " FROM " + b
                            + " WHERE " + c + " IN (" + d + ") " + e + " </script>")),

    /**
     * @since 3.5.8
     */
    SELECT_BY_IDS("selectByIds", "根据ID集合，批量查询数据",
            SqlTemplate.of5(
                    (String a, String b, String c, String d, String e) -> "<script>SELECT " + a + " FROM " + b
                            + " WHERE " + c + " IN (" + d + ") " + e + " </script>")),
    @Deprecated
    SELECT_ONE("selectOne", "查询满足条件一条数据",
            SqlTemplate.of5(
                    (String a, String b, String c, String d, String e) -> "<script>" + a + " SELECT " + b + " FROM " + c
                            + " " + d + " " + e + "\n</script>")),
    SELECT_COUNT("selectCount", "查询满足条件总记录数",
            SqlTemplate.of5(
                    (String a, String b, String c, String d, String e) -> "<script>" + a + " SELECT COUNT(" + b
                            + ") AS total FROM " + c + " " + d + " " + e + "\n</script>")),
    SELECT_LIST("selectList", "查询满足条件所有数据",
            SqlTemplate.of6(
                    (String a, String b, String c, String d, String e, String f) -> "<script>" + a + " SELECT " + b
                            + " FROM " + c + " " + d + " " + e + " " + f + "\n</script>")),
    @Deprecated
    SELECT_PAGE("selectPage", "查询满足条件所有数据（并翻页）",
            SqlTemplate.of6(
                    (String a, String b, String c, String d, String e, String f) -> "<script>" + a + " SELECT " + b
                            + " FROM " + c + " " + d + " " + e + " " + f + "\n</script>")),
    SELECT_MAPS("selectMaps", "查询满足条件所有数据",
            SqlTemplate.of6(
                    (String a, String b, String c, String d, String e, String f) -> "<script>" + a + " SELECT " + b
                            + " FROM " + c + " " + d + " " + e + " " + f + "\n</script>")),
    @Deprecated
    SELECT_MAPS_PAGE("selectMapsPage", "查询满足条件所有数据（并翻页）",
            SqlTemplate.of6(
                    (String a, String b, String c, String d, String e, String f) -> "<script>\n" + a + " SELECT " + b
                            + " FROM " + c + " " + d + " " + e + " " + f + "\n</script>")),
    SELECT_OBJS("selectObjs", "查询满足条件所有数据",
            SqlTemplate.of6(
                    (String a, String b, String c, String d, String e, String f) -> "<script>" + a + " SELECT " + b
                            + " FROM " + c + " " + d + " " + e + " " + f + "\n</script>"));

    private final String method;
    private final String desc;
    private final Object sqlTemplate;

    SqlMethod(String method, String desc, Object sqlTemplate) {
        this.method = method;
        this.desc = desc;
        this.sqlTemplate = sqlTemplate;
    }

    @SuppressWarnings("unchecked")
    public <A, B> String format(A a, B b) {
        return ((SqlTemplate.SqlTemplate2<A, B>) sqlTemplate).format(a, b);
    }

    @SuppressWarnings("unchecked")
    public <A, B, C> String format(A a, B b, C c) {
        return ((SqlTemplate.SqlTemplate3<A, B, C>) sqlTemplate).format(a, b, c);
    }

    @SuppressWarnings("unchecked")
    public <A, B, C, D> String format(A a, B b, C c, D d) {
        return ((SqlTemplate.SqlTemplate4<A, B, C, D>) sqlTemplate).format(a, b, c, d);
    }

    @SuppressWarnings("unchecked")
    public <A, B, C, D, E> String format(A a, B b, C c, D d, E e) {
        return ((SqlTemplate.SqlTemplate5<A, B, C, D, E>) sqlTemplate).format(a, b, c, d, e);
    }

    @SuppressWarnings("unchecked")
    public <A, B, C, D, E, F> String format(A a, B b, C c, D d, E e, F f) {
        return ((SqlTemplate.SqlTemplate6<A, B, C, D, E, F>) sqlTemplate).format(a, b, c, d, e, f);
    }

    public String getMethod() {
        return method;
    }

    public String getDesc() {
        return desc;
    }

    public Object getSqlTemplate() {
        return sqlTemplate;
    }
}
