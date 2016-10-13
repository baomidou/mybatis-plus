/**
 * Copyright (c) 2011-2020, hubin (jobob@qq.com).
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

import com.baomidou.mybatisplus.activerecord.conn.Connector;
import com.baomidou.mybatisplus.activerecord.conn.impl.ConnectorImpl;
import com.baomidou.mybatisplus.activerecord.DB;
import org.apache.ibatis.session.SqlSessionFactory;

/**
 * <p>
 * Mybatis开启ActiveRecord
 * </p>
 * 
 * @author Caratacus
 * @date 2016-10-13
 */
public class MybatisActiveRecord {

	/*
	 * 数据库连接
	 */
	private static final Connector connector = new ConnectorImpl();

	/**
	 * 通过spring配置获取DB
	 *
	 * @return DB
	 */
	public static DB open() {
		return connector.open();
	}

	/**
	 * 通过sqlSessionFactory获取DB
	 *
	 * @param sessionFactory
	 * @return DB
	 */
	public static DB open(SqlSessionFactory sessionFactory) {
		return connector.open(sessionFactory);
	}

	/**
	 * 通过jdbc原生获取DB
	 *
	 * @param driver
	 * @param url
	 * @param username
	 * @param password
	 * @return DB
	 */
	public DB open(String driver, String url, String username, String password) {
		return connector.open(driver, url, username, password);
	}

}
