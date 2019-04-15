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
package com.baomidou.mybatisplus.test.reflection;

import java.util.Collection;
import java.util.List;
import java.util.Properties;

import org.apache.ibatis.reflection.factory.DefaultObjectFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 对象工厂
 * @author nieqiurong 2018/8/14 13:12.
 */
public class ExampleObjectFactory extends DefaultObjectFactory {

    /**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = -2878759377109110945L;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ExampleObjectFactory.class);

    public <T> T create(Class<T> type) {
        LOGGER.debug("生成一个对象 type = [" + type + "]");
        return super.create(type);
    }

    @Override
    public <T> T create(Class<T> type, List<Class<?>> constructorArgTypes, List<Object> constructorArgs) {
        LOGGER.info("生成一个对象 type = [" + type + "], constructorArgTypes = [" + constructorArgTypes + "], constructorArgs = [" + constructorArgs + "]");
        return super.create(type, constructorArgTypes, constructorArgs);
    }

    public void setProperties(Properties properties) {
        LOGGER.debug("设置属性 properties = [" + properties + "]");
        super.setProperties(properties);
    }
    public <T> boolean isCollection(Class<T> type) {
        return Collection.class.isAssignableFrom(type);
    }

}
