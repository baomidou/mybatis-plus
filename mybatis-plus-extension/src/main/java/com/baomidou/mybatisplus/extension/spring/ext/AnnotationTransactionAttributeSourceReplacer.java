package com.baomidou.mybatisplus.extension.spring.ext;

import com.baomidou.mybatisplus.extension.spring.MybatisSqlSessionFactoryBean;
import org.mybatis.logging.Logger;
import org.mybatis.logging.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessor;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.PriorityOrdered;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.transaction.interceptor.TransactionAttributeSource;

/**
 * @author dyu 2020/12/21 15:32
 */
@Component
public class AnnotationTransactionAttributeSourceReplacer implements InstantiationAwareBeanPostProcessor, PriorityOrdered, EnvironmentAware {

    private static final Logger LOGGER = LoggerFactory.getLogger(AnnotationTransactionAttributeSourceReplacer.class);

    private Environment environment;

    @Override
    public Object postProcessBeforeInstantiation(Class<?> beanClass, String beanName) throws BeansException {
        Boolean isAllow = environment.getProperty("mybatis-plus.configuration-properties.allow-rebind-tm", Boolean.class, false);
        if(!isAllow){
            return null;
        }
         LOGGER.trace(() -> String.format("postProcessBeforeInstantiation - beanName: {%s}, beanClass: {%s})", beanName, beanClass));
        if (beanName.equals("transactionAttributeSource") && TransactionAttributeSource.class.isAssignableFrom(beanClass)) {
            LOGGER.debug(() -> String.format("instantiating bean {%s} as {%s}", beanName, MergeAnnotationTransactionAttributeSource.class.getName()));
            return new MergeAnnotationTransactionAttributeSource();
        } else {
            return null;
        }
    }


    @Override
    public int getOrder() {
        return 0;
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }
}
