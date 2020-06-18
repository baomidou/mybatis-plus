package com.baomidou.mybatisplus.autoconfigure.scripting;

import com.baomidou.mybatisplus.core.MybatisParameterHandler;
import lombok.NoArgsConstructor;
import org.apache.ibatis.executor.parameter.ParameterHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.mybatis.scripting.thymeleaf.ThymeleafLanguageDriver;
import org.mybatis.scripting.thymeleaf.ThymeleafLanguageDriverConfig;
import org.thymeleaf.ITemplateEngine;

/**
 * @author miemie
 * @since 2020-06-18
 */
@NoArgsConstructor
public class MybatisThymeleafLanguageDriver extends ThymeleafLanguageDriver {

    public MybatisThymeleafLanguageDriver(ThymeleafLanguageDriverConfig config) {
        super(config);
    }

    public MybatisThymeleafLanguageDriver(ITemplateEngine templateEngine) {
        super(templateEngine);
    }

    @Override
    public ParameterHandler createParameterHandler(MappedStatement mappedStatement, Object parameterObject, BoundSql boundSql) {
        return new MybatisParameterHandler(mappedStatement, parameterObject, boundSql);
    }
}
