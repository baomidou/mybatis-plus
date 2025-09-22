package com.baomidou.mybatisplus.spring;


import com.baomidou.mybatisplus.base.Overwrite;
import com.baomidou.mybatisplus.base.OverwriteFile;

/**
 * {@link org.mybatis.spring.SqlSessionFactoryBean}
 *
 * @author miemie
 * @since 2025/9/18
 */
public class SqlSessionFactoryBean extends OverwriteFile {

    public SqlSessionFactoryBean() {
        addStep(i -> i
            .source("""
                import java.util.stream.Stream;
                """)
            .target("""
                import com.baomidou.mybatisplus.core.MpSqlSessionFactoryBuilder;
                import com.baomidou.mybatisplus.core.MybatisPlusVersion;
                import com.baomidou.mybatisplus.core.config.GlobalConfig;
                import com.baomidou.mybatisplus.core.spi.CompatibleHelper;
                import com.baomidou.mybatisplus.core.toolkit.GlobalConfigUtils;
                import com.baomidou.mybatisplus.extension.toolkit.SqlHelper;
                import lombok.Getter;
                import lombok.Setter;
                import org.springframework.context.ApplicationContextAware;
                """)
        );
        addStep(i -> i
            .source("""
                implements FactoryBean<SqlSessionFactory>, InitializingBean, ApplicationListener<ContextRefreshedEvent> {
                """)
            .target("""
                implements FactoryBean<SqlSessionFactory>, InitializingBean, ApplicationListener<ContextRefreshedEvent>, ApplicationContextAware {
                """)
            .operate(Overwrite.Operate.COVERAGE)
        );
        addStep(i -> i
            .source("""
                private SqlSessionFactoryBuilder sqlSessionFactoryBuilder = new SqlSessionFactoryBuilder();
                """)
            .target("""
                private SqlSessionFactoryBuilder sqlSessionFactoryBuilder = new MpSqlSessionFactoryBuilder();
                """)
            .operate(Overwrite.Operate.COVERAGE)
        );
        addStep(i -> i
            .source("""
                private ObjectWrapperFactory objectWrapperFactory;
                """)
            .target("""
                /**
                 * @since 3.5.13
                 */
                @Getter
                private ApplicationContext applicationContext;
                @Setter
                private GlobalConfig globalConfig;

                /**
                 * 设置上下文对象
                 * <p>注意: 手动构建的bean对象需要手动赋值,通过xml属性初始化的会自动赋值</p>
                 * @since 3.5.13
                 * @param applicationContext ApplicationContext
                 */
                @Override
                public void setApplicationContext(ApplicationContext applicationContext) {
                    this.applicationContext = applicationContext;
                    if (CompatibleHelper.hasCompatibleSet()) {
                        CompatibleHelper.getCompatibleSet().setContext(applicationContext);
                    }
                }
                """)
        );
        addStep(i -> i
            .source("""
                Optional.ofNullable(this.objectFactory).ifPresent(targetConfiguration::setObjectFactory);
                """)
            .target("""
                this.globalConfig = Optional.ofNullable(this.globalConfig).orElseGet(GlobalConfigUtils::defaults);
                this.globalConfig.setDbConfig(Optional.ofNullable(this.globalConfig.getDbConfig()).orElseGet(GlobalConfig.DbConfig::new));
                GlobalConfigUtils.setGlobalConfig(targetConfiguration, this.globalConfig);

                Optional.ofNullable(this.objectFactory).ifPresent(targetConfiguration::setObjectFactory);
                """)
            .operate(Overwrite.Operate.COVERAGE)
        );
        addStep(i -> i
            .source("""
                return this.sqlSessionFactoryBuilder.build(targetConfiguration);
                """)
            .target("""
                final SqlSessionFactory sqlSessionFactory = this.sqlSessionFactoryBuilder.build(targetConfiguration);
                SqlHelper.FACTORY = sqlSessionFactory;
                if (globalConfig.isBanner()) {
                    System.out.println(" _ _   |_  _ _|_. ___ _ |    _ ");
                    System.out.println("| | |\\\\/|_)(_| | |_\\\\  |_)||_|_\\\\ ");
                    System.out.println("     /               |         ");
                    System.out.println("                        " + MybatisPlusVersion.getVersion() + " ");
                }
                return sqlSessionFactory;
                """)
            .operate(Overwrite.Operate.COVERAGE)
        );
    }
}
