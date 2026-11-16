package com.baomidou.mybatisplus.test.reflection;

import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.factory.DefaultObjectFactory;

import java.util.Collection;
import java.util.List;
import java.util.Properties;

/**
 * 对象工厂
 * 
 * @author nieqiurong 2018/8/14 13:12.
 */
@Slf4j
public class ExampleObjectFactory extends DefaultObjectFactory {

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = -2878759377109110945L;

    public <T> T create(Class<T> type) {
        log.debug("生成一个对象 type = [" + type + "]");
        return super.create(type);
    }

    @Override
    public <T> T create(Class<T> type, List<Class<?>> constructorArgTypes, List<Object> constructorArgs) {
        log.info(
                "生成一个对象 type = [" + type + "], constructorArgTypes = [" + constructorArgTypes + "], constructorArgs = ["
                        + constructorArgs + "]");
        return super.create(type, constructorArgTypes, constructorArgs);
    }

    public void setProperties(Properties properties) {
        log.debug("设置属性 properties = [" + properties + "]");
        super.setProperties(properties);
    }
    public <T> boolean isCollection(Class<T> type) {
        return Collection.class.isAssignableFrom(type);
    }

}
