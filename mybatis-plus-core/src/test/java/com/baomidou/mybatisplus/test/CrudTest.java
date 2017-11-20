/**
 * Copyright (c) 2011-2020, hubin (jobob@qq.com).
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.baomidou.mybatisplus.test;

import java.io.InputStream;

import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.ibatis.session.SqlSessionFactory;

import com.baomidou.mybatisplus.MybatisSessionFactoryBuilder;
import com.baomidou.mybatisplus.entity.GlobalConfiguration;
import com.baomidou.mybatisplus.toolkit.GlobalConfigUtils;

/**
 * <p>
 * CRUD 测试
 * </p>
 *
 * @author hubin
 * @date 2017-06-18
 */
public class CrudTest {

    public GlobalConfiguration globalConfiguration() {
        GlobalConfiguration global = GlobalConfigUtils.defaults();
        // global.setAutoSetDbType(true);
        // 设置全局校验机制为FieldStrategy.Empty
        global.setFieldStrategy(2);
        return global;
    }

    public SqlSessionFactory sqlSessionFactory() {
        return sqlSessionFactory("mysql-config.xml");
    }

    public SqlSessionFactory sqlSessionFactory(String configXml) {
        GlobalConfiguration global = this.globalConfiguration();
        BasicDataSource dataSource = new BasicDataSource();
        dataSource.setDriverClassName("com.mysql.jdbc.Driver");
        dataSource.setUrl("jdbc:mysql://127.0.0.1:3306/mybatis-plus?characterEncoding=UTF-8");
        dataSource.setUsername("root");
        dataSource.setPassword("521");
        dataSource.setMaxTotal(1000);
        GlobalConfigUtils.setMetaData(dataSource, global);
        // 加载配置文件
        InputStream inputStream = CrudTest.class.getClassLoader().getResourceAsStream(configXml);
        MybatisSessionFactoryBuilder factoryBuilder = new MybatisSessionFactoryBuilder();
        factoryBuilder.setGlobalConfig(global);
        return factoryBuilder.build(inputStream);
    }

}
