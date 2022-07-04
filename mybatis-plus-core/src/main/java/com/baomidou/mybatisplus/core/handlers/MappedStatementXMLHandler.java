package com.baomidou.mybatisplus.core.handlers;

import com.baomidou.mybatisplus.annotation.MappedStatementXML;
import com.baomidou.mybatisplus.core.toolkit.ExceptionUtils;
import com.baomidou.mybatisplus.core.toolkit.ReflectionKit;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.session.Configuration;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collection;

/**
 * MappedStatementXML 注解处理程序
 *
 * @author 16K
 * @author yixuan.zhang
 * @since 2022/07/03
 */

public class MappedStatementXMLHandler {

    private static final String ID = "id";

    private static final Field mappedStatementIdField = ReflectionKit.getField(MappedStatement.class, ID);

    private final Configuration configuration;

    public MappedStatementXMLHandler(Configuration configuration) {
        this.configuration = configuration;
    }

    /**
     * MappedStatementXML注解处理程序
     * 重新映射使用注解MappedStatementXML的Method
     */
    public void apply() {
        configuration.getMapperRegistry().getMappers().forEach(mapper -> {
            for (Method method : mapper.getMethods()) {
                MappedStatementXML annotation = method.getDeclaredAnnotation(MappedStatementXML.class);
                if (null != annotation){
                    this.mappedStatementMethod(mapper, method, annotation);
                }
            }
        });
    }

    /**
     * 映射语句方法 (将类中的方法与注解中的id进行映射)
     * 注解中的overlay 可以对已有映射关系的方法进行强制覆盖映射
     *
     * @param clazz 类
     * @param method 方法
     * @param mappedStatementXML 注解
     */
    private void mappedStatementMethod(Class<?> clazz, Method method, MappedStatementXML mappedStatementXML){
        String id = mappedStatementXML.id();
        boolean overlay = mappedStatementXML.overlay();
        String className = clazz.getName();

        Collection<String> mappedStatementNames = configuration.getMappedStatementNames();


        String methodPath = String.join(".", className, method.getName());
        String templatePath = String.join(".", className, id);


        try {
            if (mappedStatementNames.contains(methodPath)){
                if (overlay) {
                    ReflectionUtils.setField(mappedStatementIdField, configuration.getMappedStatement(methodPath), templatePath);
                }
            }else {
                MappedStatement mappedStatement = configuration.getMappedStatement(templatePath);
                MappedStatement newMappedStatement = new MappedStatement.Builder(
                    configuration,
                    methodPath,
                    mappedStatement.getSqlSource(),
                    mappedStatement.getSqlCommandType()
                ).build();
                configuration.addMappedStatement(newMappedStatement);

                ReflectionUtils.setField(mappedStatementIdField, newMappedStatement, templatePath);
            }
        }catch (IllegalArgumentException exception){
            throw ExceptionUtils.mpe("XML template %s has no statement with ID %s", exception, className, id);
        }
    }


}
