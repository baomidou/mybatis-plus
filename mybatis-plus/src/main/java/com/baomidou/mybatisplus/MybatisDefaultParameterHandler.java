/**
 * Copyright (c) 2011-2014, hubin (jobob@qq.com).
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.baomidou.mybatisplus;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.scripting.defaults.DefaultParameterHandler;

import com.baomidou.mybatisplus.entity.MybatisGlobalCache;
import com.baomidou.mybatisplus.entity.TableInfo;
import com.baomidou.mybatisplus.enums.IdType;
import com.baomidou.mybatisplus.mapper.IMetaObjectHandler;
import com.baomidou.mybatisplus.toolkit.IdWorker;
import com.baomidou.mybatisplus.toolkit.StringUtils;
import com.baomidou.mybatisplus.toolkit.TableInfoHelper;

/**
 * <p>
 * 自定义 ParameterHandler 重装构造函数，填充插入方法主键 ID
 * </p>
 * 
 * @author hubin
 * @Date 2016-03-11
 */
public class MybatisDefaultParameterHandler extends DefaultParameterHandler {

	public MybatisDefaultParameterHandler(MappedStatement mappedStatement, Object parameterObject, BoundSql boundSql) {
		super(mappedStatement, processBatch(mappedStatement, parameterObject), boundSql);
	}

	/**
	 * <p>
	 * 批量（填充主键 ID）
	 * </p>
	 * 
	 * @param ms
	 * @param parameterObject
	 *            插入数据库对象
	 * @return
	 */
	protected static Object processBatch(MappedStatement ms, Object parameterObject) {
		if (ms.getSqlCommandType() == SqlCommandType.INSERT) {
			/**
			 * 只处理插入操作
			 */
			Collection<Object> parameters = getParameters(parameterObject);
			if (null != parameters) {
				List<Object> objList = new ArrayList<Object>();
				for (Object parameter : parameters) {
					TableInfo tableInfo = TableInfoHelper.getTableInfo(parameter.getClass());
					if (null != tableInfo) {
						objList.add(populateKeys(tableInfo, ms, parameter));
					} else {
						/*
						 * 非表映射类不处理
						 */
						objList.add(parameter);
					}
				}
				return objList;
			} else {
				TableInfo tableInfo = TableInfoHelper.getTableInfo(parameterObject.getClass());
				return populateKeys(tableInfo, ms, parameterObject);
			}
		}
		return parameterObject;
	}

	/**
	 * <p>
	 * 处理正常批量插入逻辑
	 * </p>
	 * <p>
	 * org.apache.ibatis.session.defaults.DefaultSqlSession$StrictMap 该类方法
	 * wrapCollection 实现 StrictMap 封装逻辑
	 * </p>
	 * 
	 * @param parameter
	 *            插入数据库对象
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected static Collection<Object> getParameters(Object parameter) {
		Collection<Object> parameters = null;
		if (parameter instanceof Collection) {
			parameters = (Collection) parameter;
		} else if (parameter instanceof Map) {
			Map parameterMap = (Map) parameter;
			if (parameterMap.containsKey("collection")) {
				parameters = (Collection) parameterMap.get("collection");
			} else if (parameterMap.containsKey("list")) {
				parameters = (List) parameterMap.get("list");
			} else if (parameterMap.containsKey("array")) {
				parameters = Arrays.asList((Object[]) parameterMap.get("array"));
			}
		}
		return parameters;
	}

	/**
	 * <p>
	 * 填充主键 ID
	 * </p>
	 * 
	 * @param tableInfo
	 * @param ms
	 * @param parameterObject
	 *            插入数据库对象
	 * @return
	 */
	protected static Object populateKeys(TableInfo tableInfo, MappedStatement ms, Object parameterObject) {
		if (null != tableInfo && null != tableInfo.getIdType() && tableInfo.getIdType().getKey() >= 2) {
			MetaObject metaObject = ms.getConfiguration().newMetaObject(parameterObject);
			Object idValue = metaObject.getValue(tableInfo.getKeyProperty());
			/* 自定义 ID */
			if (StringUtils.checkValNull(idValue)) {
				if (tableInfo.getIdType() == IdType.ID_WORKER) {
					metaObject.setValue(tableInfo.getKeyProperty(), IdWorker.getId());
				} else if (tableInfo.getIdType() == IdType.UUID) {
					metaObject.setValue(tableInfo.getKeyProperty(), get32UUID());
				}
			}
			/* 自定义元对象填充控制器 */
			IMetaObjectHandler metaObjectHandler = MybatisGlobalCache.getMetaObjectHandler(ms.getConfiguration());
			if (null != metaObjectHandler) {
				metaObjectHandler.insertFill(metaObject);
			}
			return metaObject.getOriginalObject();
		}
		/*
		 * 不处理
		 */
		return parameterObject;
	}

	/**
	 * <p>
	 * 获取去掉"-" UUID
	 * </p>
	 */
	protected static synchronized String get32UUID() {
		return UUID.randomUUID().toString().replace("-", "");
	}
}
