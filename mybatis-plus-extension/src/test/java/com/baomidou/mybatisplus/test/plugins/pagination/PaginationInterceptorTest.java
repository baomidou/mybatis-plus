package com.baomidou.mybatisplus.test.plugins.pagination;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.parsers.BlockAttackSqlParser;
import com.baomidou.mybatisplus.extension.plugins.PaginationInterceptor;
import com.baomidou.mybatisplus.extension.plugins.pagination.dialects.DB2Dialect;
import com.baomidou.mybatisplus.extension.plugins.pagination.dialects.MySqlDialect;
import com.baomidou.mybatisplus.extension.plugins.tenant.TenantSqlParser;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Properties;


/**
 * @author nieqiurong 2020/4/10.
 */
@ExtendWith(MockitoExtension.class)
class PaginationInterceptorTest {
    
    @Test
    void testSetCountSqlParser() {
        PaginationInterceptor paginationInterceptor = new PaginationInterceptor();
        MetaObject metaObject = SystemMetaObject.forObject(paginationInterceptor);
        Assertions.assertNull(metaObject.getValue("countSqlParser"));
        Properties properties = new Properties();
        properties.setProperty("countSqlParser", BlockAttackSqlParser.class.getName());
        paginationInterceptor.setProperties(properties);
        Assertions.assertEquals(metaObject.getValue("countSqlParser").getClass().getName(), BlockAttackSqlParser.class.getName());
        paginationInterceptor.setCountSqlParser(new TenantSqlParser());
        Assertions.assertEquals(metaObject.getValue("countSqlParser").getClass().getName(), TenantSqlParser.class.getName());
    }
    
    @Test
    void testSetOverflow() {
        PaginationInterceptor paginationInterceptor = new PaginationInterceptor();
        MetaObject metaObject = SystemMetaObject.forObject(paginationInterceptor);
        Assertions.assertFalse((Boolean) metaObject.getValue("overflow"));
        Properties properties = new Properties();
        properties.setProperty("overflow", "true");
        paginationInterceptor.setProperties(properties);
        Assertions.assertTrue((Boolean) metaObject.getValue("overflow"));
        paginationInterceptor.setOverflow(false);
        Assertions.assertFalse((Boolean) metaObject.getValue("overflow"));
    }
    
    @Test
    void testSetDbType() {
        PaginationInterceptor paginationInterceptor = new PaginationInterceptor();
        MetaObject metaObject = SystemMetaObject.forObject(paginationInterceptor);
        Assertions.assertNull(metaObject.getValue("dialectType"));
        Properties properties = new Properties();
        properties.setProperty("dialectType", "mysql");
        paginationInterceptor.setProperties(properties);
        Assertions.assertEquals(DbType.MYSQL, metaObject.getValue("dbType"));
        paginationInterceptor.setDbType(DbType.DB2);
        Assertions.assertEquals(DbType.DB2, metaObject.getValue("dbType"));
        paginationInterceptor.setDialectType("mysql");
        Assertions.assertEquals(DbType.MYSQL, metaObject.getValue("dbType"));
    }
    
    @Test
    void testSetDialect() {
        PaginationInterceptor paginationInterceptor = new PaginationInterceptor();
        MetaObject metaObject = SystemMetaObject.forObject(paginationInterceptor);
        Assertions.assertNull(metaObject.getValue("dialectClazz"));
        Properties properties = new Properties();
        properties.setProperty("dialectClazz", MySqlDialect.class.getName());
        paginationInterceptor.setProperties(properties);
        Assertions.assertEquals(MySqlDialect.class.getName(), metaObject.getValue("dialect").getClass().getName());
        paginationInterceptor.setDialect(new DB2Dialect());
        Assertions.assertEquals(DB2Dialect.class.getName(), metaObject.getValue("dialect").getClass().getName());
        paginationInterceptor.setDialectClazz(MySqlDialect.class.getName());
        Assertions.assertEquals(MySqlDialect.class.getName(), metaObject.getValue("dialect").getClass().getName());
    }
    
    @Test
    void testSetLimit(){
        PaginationInterceptor paginationInterceptor = new PaginationInterceptor();
        MetaObject metaObject = SystemMetaObject.forObject(paginationInterceptor);
        Assertions.assertEquals(500L, metaObject.getValue("limit"));
        Properties properties = new Properties();
        properties.setProperty("limit", "10086");
        paginationInterceptor.setProperties(properties);
        Assertions.assertEquals(10086L, metaObject.getValue("limit"));
        paginationInterceptor.setLimit(10010L);
        Assertions.assertEquals(10010L, metaObject.getValue("limit"));
    }
    
}
