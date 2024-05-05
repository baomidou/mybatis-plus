package com.baomidou.mybatisplus.test.reflection;

import org.apache.ibatis.reflection.factory.DefaultObjectFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.List;
import java.util.Properties;

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
