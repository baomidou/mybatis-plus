package demo;

import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.core.MybatisSqlSessionFactoryBuilder;
import com.baomidou.mybatisplus.core.config.GlobalConfig;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.zaxxer.hikari.HikariDataSource;
import demo.dso.MetaObjectHandlerImpl;
import demo.dso.MybatisSqlSessionFactoryBuilderImpl;
import okhttp3.Interceptor;
import org.apache.ibatis.solon.annotation.Db;
import org.noear.solon.Solon;
import org.noear.solon.annotation.Bean;
import org.noear.solon.annotation.Configuration;
import org.noear.solon.annotation.Inject;

import javax.sql.DataSource;

@Configuration
public class Config {
    @Bean("db1")
    public DataSource db1(@Inject("${dataSource.db1}") HikariDataSource hikariDataSource) {
        return hikariDataSource;
    }

//    @Bean
//    public Interceptor plusInterceptor() {
//        MybatisPlusInterceptor plusInterceptor = new MybatisPlusInterceptor();
//        plusInterceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));
//        return plusInterceptor;
//    }

    @Bean
    public void db1_ext(@Db("db1") GlobalConfig globalConfig) {
        MetaObjectHandler metaObjectHandler = new MetaObjectHandlerImpl();

        globalConfig.setMetaObjectHandler(metaObjectHandler);
    }

    @Bean
    public void db1_ext2(@Db("db1") MybatisConfiguration config){
        config.getTypeHandlerRegistry().register("xxx");
        config.setDefaultEnumTypeHandler(null);
    }

    @Bean
    public MybatisSqlSessionFactoryBuilder factoryBuilderNew(){
        return new MybatisSqlSessionFactoryBuilderImpl();
    }
}
