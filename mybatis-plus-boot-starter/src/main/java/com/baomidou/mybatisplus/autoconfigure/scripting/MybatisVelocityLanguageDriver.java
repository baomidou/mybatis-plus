package com.baomidou.mybatisplus.autoconfigure.scripting;

import com.baomidou.mybatisplus.core.MybatisParameterHandler;
import lombok.NoArgsConstructor;
import org.apache.ibatis.executor.parameter.ParameterHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.mybatis.scripting.velocity.VelocityLanguageDriver;
import org.mybatis.scripting.velocity.VelocityLanguageDriverConfig;

/**
 * @author miemie
 * @since 2020-06-18
 */
@NoArgsConstructor
public class MybatisVelocityLanguageDriver extends VelocityLanguageDriver {

    public MybatisVelocityLanguageDriver(VelocityLanguageDriverConfig driverConfig) {
        super(driverConfig);
    }

    @Override
    public ParameterHandler createParameterHandler(MappedStatement mappedStatement, Object parameterObject, BoundSql boundSql) {
        return new MybatisParameterHandler(mappedStatement, parameterObject, boundSql);
    }
}
