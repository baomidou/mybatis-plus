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

import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.scripting.defaults.DefaultParameterHandler;

import com.baomidou.mybatisplus.annotations.IdType;
import com.baomidou.mybatisplus.toolkit.IdWorker;
import com.baomidou.mybatisplus.toolkit.TableInfo;
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
		Collection<Object> parameters = getParameters(parameterObject);
		if (parameters != null) {
			List<Object> objList = new ArrayList<Object>();
			for (Object parameter : parameters) {
				objList.add(populateKeys(ms, parameter));
			}
			return objList;
		} else {
			return populateKeys(ms, parameterObject);
		}
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
	 * @param ms
	 * @param parameterObject
	 *            插入数据库对象
	 * @return
	 */
	protected static Object populateKeys(MappedStatement ms, Object parameterObject) {
		if (ms.getSqlCommandType() == SqlCommandType.INSERT) {
			TableInfo tableInfo = TableInfoHelper.getTableInfo(parameterObject.getClass());
			if (tableInfo != null && tableInfo.getIdType() == IdType.ID_WORKER) {
				MetaObject metaParam = ms.getConfiguration().newMetaObject(parameterObject);
				Object idValue = metaParam.getValue(tableInfo.getKeyProperty());
				if (idValue == null) {
					/* 自定义 ID */
					metaParam.setValue(tableInfo.getKeyProperty(), IdWorker.getId());
				}
				return metaParam.getOriginalObject();
			}
		}
		return parameterObject;
	}

}
