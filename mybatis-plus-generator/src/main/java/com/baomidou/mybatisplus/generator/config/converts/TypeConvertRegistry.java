/*
 * Copyright (c) 2011-2020, baomidou (jobob@qq.com).
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * <p>
 * https://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.baomidou.mybatisplus.generator.config.converts;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.generator.config.ITypeConvert;

import java.util.EnumMap;
import java.util.Map;

/**
 * @author nieqiuqiu
 * @date 2020-01-09
 * @since 3.3.1
 */
public class TypeConvertRegistry {

    private final Map<DbType, ITypeConvert> type_convert_enum_map = new EnumMap<>(DbType.class);

    public TypeConvertRegistry() {
        type_convert_enum_map.put(DbType.ORACLE, new OracleTypeConvert());
        type_convert_enum_map.put(DbType.SQL_SERVER, new SqlServerTypeConvert());
        type_convert_enum_map.put(DbType.POSTGRE_SQL, new PostgreSqlTypeConvert());
        type_convert_enum_map.put(DbType.DB2, new DB2TypeConvert());
        type_convert_enum_map.put(DbType.SQLITE, new SqliteTypeConvert());
        type_convert_enum_map.put(DbType.DM, new DmTypeConvert());
        type_convert_enum_map.put(DbType.MARIADB, new MySqlTypeConvert());
        type_convert_enum_map.put(DbType.KINGBASE_ES, new KingbaseESTypeConvert());
        type_convert_enum_map.put(DbType.MYSQL, new MySqlTypeConvert());
    }

    public ITypeConvert getTypeConvert(DbType dbType) {
        return type_convert_enum_map.get(dbType);
    }
}
