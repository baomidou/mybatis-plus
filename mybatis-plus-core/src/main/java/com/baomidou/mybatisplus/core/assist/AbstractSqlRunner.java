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

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import org.apache.ibatis.parsing.GenericTokenParser;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import static com.baomidou.mybatisplus.core.toolkit.StringPool.DOT;
import static com.baomidou.mybatisplus.core.toolkit.StringPool.HASH_LEFT_BRACE;
import static com.baomidou.mybatisplus.core.toolkit.StringPool.LEFT_BRACE;
import static com.baomidou.mybatisplus.core.toolkit.StringPool.LEFT_SQ_BRACKET;
import static com.baomidou.mybatisplus.core.toolkit.StringPool.RIGHT_BRACE;
import static com.baomidou.mybatisplus.core.toolkit.StringPool.RIGHT_SQ_BRACKET;

/**
 * @author nieqiurong
 * @since 3.5.12
 */
public abstract class AbstractSqlRunner implements ISqlRunner {

    /**
     * 默认分词处理器
     *
     * @since 3.5.12
     */
    private static final GenericTokenParser DEFAULT_TOKEN_PARSER = new GenericTokenParser(LEFT_BRACE, RIGHT_BRACE,
            content -> HASH_LEFT_BRACE + content + RIGHT_BRACE);

    /**
     * 校验索引正则
     *
     * @since 3.5.12
     */
    private static final Pattern INDEX_PATTERN = Pattern.compile("^\\d+$");

    /**
     * 第一个值参数key
     *
     * @since 3.5.12
     */
    private static final String ARG0 = "arg0";

    /**
     * 获取执行语句 (将原始占位符语句转换为标准占位符语句)
     *
     * @param sql 原始sql
     * @param args 参数
     * @return 执行语句 (带参数占位符)
     * @since 3.5.12
     */
    protected String parse(String sql, Object... args) {
        if (args != null && args.length == 1 && args[0] != null) {
            Object arg = args[0];
            Class<?> clazz = arg.getClass();
            return new GenericTokenParser(LEFT_BRACE, RIGHT_BRACE, content -> {
                if (INDEX_PATTERN.matcher(content).matches()) {
                    if (arg instanceof Collection || clazz.isArray()) {
                        return HASH_LEFT_BRACE + ARG0 + LEFT_SQ_BRACKET + content + RIGHT_SQ_BRACKET + RIGHT_BRACE;
                    }
                    return HASH_LEFT_BRACE + content + RIGHT_BRACE;
                } else {
                    return HASH_LEFT_BRACE + ARG0 + DOT + content + RIGHT_BRACE;
                }
            }).parse(sql);
        }
        return DEFAULT_TOKEN_PARSER.parse(sql);
    }

    /**
     * 获取参数列表
     *
     * @param args 参数(单参数时,支持使用Map,List,Array,JavaBean访问)
     * @return 参数map
     * @since 3.5.12
     */
    protected Map<String, Object> getParams(Object... args) {
        if (args != null && args.length > 0) {
            Map<String, Object> params = CollectionUtils.newHashMapWithExpectedSize(args.length);
            for (int i = 0; i < args.length; i++) {
                Object arg = args[i];
                if (i == 0) {
                    params.put(ARG0, arg);
                }
                params.put(String.valueOf(i), arg);
            }
            return params;
        }
        return new HashMap<>();
    }

    /**
     * 获取sqlMap参数
     * <p>
     * 自3.5.12开始,(当传入的参数是单参数时,支持使用Map,Array,List,JavaBean)
     * <li>当参数为 Map 时可通过{key}进行属性访问
     * <li>当参数为 JavaBean 时可通过{property}进行属性访问
     * <li>当参数为 List 时直接访问索引 {0}</li>
     * <li>当参数为 Array 时直接访问索引 {0}</li>
     * </p>
     *
     * @param sql 指定参数的格式: {0}, {1} 或者 {property1}, {property2}
     * @param args 参数
     * @return 参数集合
     */
    protected Map<String, Object> sqlMap(String sql, Object... args) {
        Map<String, Object> sqlMap = getParams(args);
        sqlMap.put(SQL, parse(sql, args));
        return sqlMap;
    }

    /**
     * <p>
     * 自3.5.12开始,(当传入的参数是单参数时,支持使用Map,Array,List,JavaBean)
     * <li>当参数为 Map 时可通过{key}进行属性访问
     * <li>当参数为 JavaBean 时可通过{property}进行属性访问
     * <li>当参数为 List 时直接访问索引 {0}</li>
     * <li>当参数为 Array 时直接访问索引 {0}</li>
     * </p>
     *
     * @param sql 指定参数的格式: {0}, {1} 或者 {property1}, {property2}
     * @param page 分页模型
     * @param args 参数
     * @return 参数集合
     */
    protected Map<String, Object> sqlMap(String sql, IPage<?> page, Object... args) {
        Map<String, Object> sqlMap = getParams(args);
        sqlMap.put(PAGE, page);
        sqlMap.put(SQL, parse(sql, args));
        return sqlMap;
    }

}
