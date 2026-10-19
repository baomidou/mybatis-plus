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
package com.baomidou.mybatisplus.core.assist;

import com.baomidou.mybatisplus.core.injector.SqlRunnerInjector;
import com.baomidou.mybatisplus.core.metadata.IPage;

import java.util.List;
import java.util.Map;

/**
 * SqlRunner执行接口
 * <p>
 * 自3.5.12开始,(当传入的参数是单参数时,支持使用Map,Array,List,JavaBean)
 * <li>当参数为 Map 时可通过{key}进行属性访问
 * <li>当参数为 JavaBean 时可通过{property}进行属性访问
 * <li>当参数为 List 时直接访问索引 {0}</li>
 * <li>当参数为 Array 时直接访问索引 {0}</li>
 * </p>
 *
 * @author yuxiaobin, nieqiurong
 * @since 2018/2/7
 */
public interface ISqlRunner {

    /**
     * INSERT 语句
     */
    String INSERT = "com.baomidou.mybatisplus.core.mapper.SqlRunner.Insert";

    /**
     * DELETE 语句
     */
    String DELETE = "com.baomidou.mybatisplus.core.mapper.SqlRunner.Delete";

    /**
     * UPDATE 语句
     */
    String UPDATE = "com.baomidou.mybatisplus.core.mapper.SqlRunner.Update";

    /**
     * SELECT_LIST 语句
     */
    String SELECT_LIST = "com.baomidou.mybatisplus.core.mapper.SqlRunner.SelectList";

    /**
     * SELECT_OBJS 语句
     */
    String SELECT_OBJS = "com.baomidou.mybatisplus.core.mapper.SqlRunner.SelectObjs";

    /**
     * COUNT 语句
     */
    String COUNT = "com.baomidou.mybatisplus.core.mapper.SqlRunner.Count";

    /**
     * 注入SQL脚本
     *
     * @deprecated 3.5.12 {@link SqlRunnerInjector#SQL_SCRIPT}
     */
    @Deprecated
    String SQL_SCRIPT = "${sql}";

    /**
     * sql访问参数
     */
    String SQL = "sql";

    /**
     * page访问参数
     */
    String PAGE = "page";

    /**
     * 执行插入语句
     *
     * @param sql 指定参数的格式: {0}, {1} 或者 {property1}, {property2}
     * @param args 参数
     * @return 插入结果
     */
    boolean insert(String sql, Object... args);

    /**
     * 执行删除语句
     *
     * @param sql 指定参数的格式: {0}, {1} 或者 {property1}, {property2}
     * @param args 参数
     * @return 删除结果
     */
    boolean delete(String sql, Object... args);

    /**
     * 执行更新语句
     *
     * @param sql 指定参数的格式: {0}, {1} 或者 {property1}, {property2}
     * @param args 参数
     * @return 更新结果
     */
    boolean update(String sql, Object... args);

    /**
     * 根据sql查询Map结果集
     * <p>
     * SqlRunner.db().selectList("select * from tbl_user where name={0}", "Caratacus")
     * </p>
     *
     * @param sql sql语句，可添加参数，指定参数的格式: {0}, {1} 或者 {property1}, {property2}
     * @param args 参数列表
     * @return 结果集
     */
    List<Map<String, Object>> selectList(String sql, Object... args);

    /**
     * 根据sql查询一个字段值的结果集
     * <p>
     * 注意：该方法只会返回一个字段的值， 如果需要多字段，请参考{@link #selectList(String, Object...)}
     * </p>
     *
     * @param sql sql语句，可添加参数，指定参数的格式: {0}, {1} 或者 {property1}, {property2}
     * @param args 参数
     * @return 结果集
     */
    List<Object> selectObjs(String sql, Object... args);

    /**
     * 根据sql查询一个字段值的一条结果
     * <p>
     * 注意：该方法只会返回一个字段的值， 如果需要多字段，请参考{@link #selectOne(String, Object...)}
     * </p>
     *
     * @param sql sql语句，可添加参数，指定参数的格式: {0}, {1} 或者 {property1}, {property2}
     * @param args 参数
     * @return 结果
     */
    Object selectObj(String sql, Object... args);

    /**
     * 查询总数
     *
     * @param sql sql语句，可添加参数，指定参数的格式: {0}, {1} 或者 {property1}, {property2}
     * @param args 参数
     * @return 总记录数
     */
    long selectCount(String sql, Object... args);

    /**
     * 获取单条记录
     *
     * @param sql sql语句，可添加参数，指定参数的格式: {0}, {1} 或者 {property1}, {property2}
     * @param args 参数
     * @return 单行结果集 (当执行语句返回多条记录时,只会选取第一条记录)
     */
    Map<String, Object> selectOne(String sql, Object... args);

    /**
     * 分页查询
     *
     * @param page 分页对象
     * @param sql sql语句，可添加参数，指定参数的格式: {0}, {1} 或者 {property1}, {property2}
     * @param args 参数
     * @param <E> E
     * @return 分页数据
     */
    <E extends IPage<Map<String, Object>>> E selectPage(E page, String sql, Object... args);
}
