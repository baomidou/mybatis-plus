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
package com.baomidou.mybatisplus.core.metadata;

import com.baomidou.mybatisplus.core.exceptions.MybatisPlusException;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.session.SqlSession;

/**
 * Mapper代理属性
 *
 * @author nieqiurong
 * @see com.baomidou.mybatisplus.core.override.MybatisMapperProxy
 * @see org.apache.ibatis.binding.MapperProxy
 * @since 3.5.12
 */
@SuppressWarnings("LombokGetterMayBeUsed")
public class MapperProxyMetadata {

    private final SqlSession sqlSession;

    private final Class<?> mapperInterface;

    public MapperProxyMetadata(MetaObject metaObject) {
        if (!metaObject.hasGetter("mapperInterface") || !metaObject.hasGetter("sqlSession")) {
            throw new MybatisPlusException("Unable to retrieve the mapperInterface and sqlSession properties from "
                    + metaObject.getOriginalObject());
        }
        this.mapperInterface = (Class<?>) metaObject.getValue("mapperInterface");
        this.sqlSession = (SqlSession) metaObject.getValue("sqlSession");
    }

    public Class<?> getMapperInterface() {
        return mapperInterface;
    }

    public SqlSession getSqlSession() {
        return sqlSession;
    }

    @Override
    public String toString() {
        return "MapperProxy{" + "mapperInterface=" + mapperInterface + ", sqlSession=" + sqlSession + '}';
    }

}
