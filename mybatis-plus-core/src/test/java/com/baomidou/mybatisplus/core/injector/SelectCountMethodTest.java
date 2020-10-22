package com.baomidou.mybatisplus.core.injector;

import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.core.MybatisXMLLanguageDriver;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.enums.SqlMethod;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import org.apache.ibatis.builder.MapperBuilderAssistant;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlSource;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.defaults.DefaultSqlSessionFactory;
import org.junit.jupiter.api.Test;
import org.springframework.util.Assert;

/**
 * @author wangye
 * @classname MethodTest
 * @date 2020/10/22 9:23
 **/
public class SelectCountMethodTest extends  AbstractMethod {

    @Test
    public void test(){
        configuration = new MybatisConfiguration();
        builderAssistant = new MapperBuilderAssistant(configuration, "");
        languageDriver = new MybatisXMLLanguageDriver();
        TableInfo tableInfo = TableInfoHelper.initTableInfo(builderAssistant,Entity.class);
        MappedStatement mappedStatement = injectMappedStatement(TestMapper.class, Entity.class, tableInfo);
        BoundSql boundSql = mappedStatement.getSqlSource().getBoundSql(null);
        System.out.println(boundSql);
    }

    @Override
    public MappedStatement injectMappedStatement(Class<?> mapperClass, Class<?> modelClass, TableInfo tableInfo) {
        SqlMethod sqlMethod = SqlMethod.SELECT_COUNT;
        String sql = String.format(sqlMethod.getSql(), sqlFirst(), sqlCount(), tableInfo.getTableName(),
            sqlWhereEntityWrapper(true, tableInfo), sqlComment());
        SqlSource sqlSource = languageDriver.createSqlSource(configuration, sql, modelClass);
        return this.addSelectMappedStatementForOther(mapperClass, getMethod(sqlMethod), sqlSource, Integer.class);
    }

    public interface TestMapper extends BaseMapper {

    }

    public class Entity{
        private Long id;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }
    }
}
