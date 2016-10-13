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

	// 连接
	private static final Connector connector = new ConnectorImpl();

	/**
	 * 根据sping配置的数据源打开,在配置了mybatis的完整配置后 使用SpringManagedTransaction,spring的标准配置.
	 * 需用SqlSessionFactoryBeanExt代替SqlSessionFactoryBean在spring中进行配置
	 *
	 * @return
	 */
	public static DB open() {
		return connector.open();
	}

	/**
	 * 支持多个session工厂
	 *
	 * @param sessionFactory
	 * @return
	 */
	public static DB open(SqlSessionFactory sessionFactory) {
		return connector.open(sessionFactory);
	}

	public DB open(String driver, String url, String username, String password) {
		return connector.open(driver, url, username, password);
	}

}
