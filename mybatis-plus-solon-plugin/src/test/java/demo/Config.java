package demo;

import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.core.MybatisSqlSessionFactoryBuilder;
import com.baomidou.mybatisplus.core.config.GlobalConfig;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import demo.dso.MetaObjectHandlerImpl;
import demo.dso.MybatisSqlSessionFactoryBuilderImpl;
import org.apache.ibatis.solon.annotation.Db;
import org.noear.solon.Solon;
import org.noear.solon.annotation.Bean;
import org.noear.solon.annotation.Configuration;
import org.noear.solon.annotation.Inject;

import javax.sql.DataSource;

@Configuration
public class Config {
    @Bean
    public void db1_cfg(@Db("db1") MybatisConfiguration cfg,
                        @Db("db1") GlobalConfig globalConfig) {
        //增加 mybatis-plus 的自带分页插件
        MybatisPlusInterceptor plusInterceptor = new MybatisPlusInterceptor();
        plusInterceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));

        cfg.addInterceptor(plusInterceptor);
    }

//    @Bean
//    public MybatisSqlSessionFactoryBuilder factoryBuilderNew(){
//        return new MybatisSqlSessionFactoryBuilderImpl();
//    }
}

