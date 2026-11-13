package com.baomidou.mybatisplus.test.tenant;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.plugins.IgnoreStrategy;
import com.baomidou.mybatisplus.core.plugins.InterceptorIgnoreHelper;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.TenantLineInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.test.DbTestSupport;
import lombok.Data;
import lombok.experimental.Accessors;
import net.sf.jsqlparser.expression.LongValue;
import org.apache.ibatis.annotations.CacheNamespace;
import org.apache.ibatis.cache.Cache;
import org.apache.ibatis.plugin.Interceptor;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author miemie
 * @since 2020-06-24
 */
@Tag("integration")
public class TenantTest extends DbTestSupport<TenantTest.EntityMapper> {

    @Test
    void shouldIsolateTenantsAndSupportIgnore() {
        Cache cache = sqlSessionFactory.getConfiguration().getCache(EntityMapper.class.getName());
        assertThat(cache).as("使用 @CacheNamespace 指定了使用缓存").isNotNull();

        Long id = 1L;
        doTestAutoCommit(m -> {
            int insert = m.insert(new Entity().setId(id));
            assertThat(insert).as("插入成功").isEqualTo(1);
        });

        doTest(m -> {
            Entity entity = m.selectById(id);
            assertThat(entity).as("插入成功").isNotNull();
            assertThat(entity.getTenantId()).as("有租户信息").isEqualTo(1);
        });
        assertThat(cache.getSize()).as("有一条缓存").isEqualTo(1);

        doTest(m -> {
            Entity entity = m.selectById(id);
            assertThat(entity).as("插入成功").isNotNull();
            assertThat(entity.getTenantId()).as("有租户信息").isEqualTo(1);
        });
        assertThat(cache.getSize()).as("依然只有一条缓存,命中了缓存").isEqualTo(1);

        doTestAutoCommit(m -> {
            int delete = m.deleteById(id);
            assertThat(delete).as("删除成功").isEqualTo(1);
        });
        assertThat(cache.getSize()).as("update操作清空了缓存").isEqualTo(0);

        doTestAutoCommit(m -> {
            int insert = m.insert(new Entity().setId(id).setTenantId(2));
            assertThat(insert).as("故意插入一个其他租户的信息,插入成功").isEqualTo(1);
        });

        doTest(m -> {
            Entity entity = m.selectById(id);
            assertThat(entity).as("搜索不到数据").isNull();
        });
        assertThat(cache.getSize()).as("缓存了个寂寞").isEqualTo(1);

        doTest(m -> {
            Entity entity = m.selectById(id);
            assertThat(entity).as("搜索不到数据").isNull();
        });
        assertThat(cache.getSize()).as("依然缓存了个寂寞,说明命中的缓存").isEqualTo(1);

        doTest(m -> {
            Page<Entity> page = m.selectPage(new Page<>(), null);
            assertThat(page.getTotal()).as("count 正常").isEqualTo(0);
        });

        doTest(m -> {
            Entity entity = new Entity().setName("秋秋").setTenantId(2);
            m.insert(entity);
            assertThat(m.selectById(entity.getId())).isNull();
            assertThat(m.selectByIdWithIgnore(entity.getId())).isNotNull();
            assertThat(m.selectByIdWithIgnore2(entity.getId())).isNull();
            assertThat(m.selectByIdWithIgnore3(entity.getId())).isNull();
            assertThat(m.selectByIdWithIgnore4(entity.getId())).isNull();
            assertThat(m.selectByIdWithIgnore5(entity.getId())).isNull();

            assertThat(m.selectByIdWithIgnore6(IgnoreStrategy.builder().tenantLine(true).build(), entity.getId()))
                    .isNotNull();
            assertThat(m.selectByIdWithIgnore6(IgnoreStrategy.builder().tenantLine(false).build(), entity.getId()))
                    .isNull();
            assertThat(m.selectByIdWithIgnore7(IgnoreStrategy.builder().tenantLine(true).build(), entity.getId()))
                    .isNotNull();
            assertThat(m.selectByIdWithIgnore7(IgnoreStrategy.builder().tenantLine(false).build(), entity.getId()))
                    .isNull();
            assertThat(m.selectByIdWithIgnore8(IgnoreStrategy.builder().tenantLine(true).build(), entity.getId()))
                    .isNotNull();
            assertThat(m.selectByIdWithIgnore8(IgnoreStrategy.builder().tenantLine(false).build(), entity.getId()))
                    .isNull();

            assertThat(
                    InterceptorIgnoreHelper.execute(
                            IgnoreStrategy.builder().tenantLine(true).build(),
                            () -> m.selectByIdWithIgnore(entity.getId())))
                    .isNotNull();
            assertThat(
                    InterceptorIgnoreHelper.execute(
                            IgnoreStrategy.builder().tenantLine(false).build(),
                            () -> m.selectByIdWithIgnore(entity.getId())))
                    .isNull();
            assertThat(
                    InterceptorIgnoreHelper.execute(
                            IgnoreStrategy.builder().tenantLine(true).build(),
                            () -> m.selectByIdWithIgnore2(entity.getId())))
                    .isNotNull();
            assertThat(
                    InterceptorIgnoreHelper.execute(
                            IgnoreStrategy.builder().tenantLine(false).build(),
                            () -> m.selectByIdWithIgnore2(entity.getId())))
                    .isNull();
            assertThat(
                    InterceptorIgnoreHelper.execute(
                            IgnoreStrategy.builder().tenantLine(true).build(),
                            () -> m.selectByIdWithIgnore3(entity.getId())))
                    .isNotNull();
            assertThat(
                    InterceptorIgnoreHelper.execute(
                            IgnoreStrategy.builder().tenantLine(false).build(),
                            () -> m.selectByIdWithIgnore3(entity.getId())))
                    .isNull();
            assertThat(
                    InterceptorIgnoreHelper.execute(
                            IgnoreStrategy.builder().tenantLine(true).build(),
                            () -> m.selectByIdWithIgnore4(entity.getId())))
                    .isNotNull();
            assertThat(
                    InterceptorIgnoreHelper.execute(
                            IgnoreStrategy.builder().tenantLine(false).build(),
                            () -> m.selectByIdWithIgnore4(entity.getId())))
                    .isNull();
            assertThat(
                    InterceptorIgnoreHelper.execute(
                            IgnoreStrategy.builder().tenantLine(true).build(),
                            () -> m.selectByIdWithIgnore5(entity.getId())))
                    .isNotNull();
            assertThat(
                    InterceptorIgnoreHelper.execute(
                            IgnoreStrategy.builder().tenantLine(false).build(),
                            () -> m.selectByIdWithIgnore5(entity.getId())))
                    .isNull();
            assertThat(m.deleteById(entity.getId())).isEqualTo(0);
            assertThat(m.deleteByIdWithIgnore(entity.getId())).isEqualTo(1);
        });

        doTest(m -> {
            Entity entity = new Entity().setName("秋秋").setTenantId(2);
            m.insert(entity);
            assertThat(m.selectById(entity.getId())).isNull();
            assertThat(
                    InterceptorIgnoreHelper.execute(
                            IgnoreStrategy.builder().tenantLine(true).build(), () -> m.selectById(entity.getId())))
                    .isNotNull();
            assertThat(m.deleteById(entity.getId())).isEqualTo(0);
            assertThat(
                    InterceptorIgnoreHelper.execute(
                            IgnoreStrategy.builder().tenantLine(true).build(), () -> m.deleteById(entity.getId())))
                    .isEqualTo(1);
        });

        doTest(m -> {
            Entity entity = new Entity().setName("秋秋").setTenantId(2);
            m.insert(entity);
            assertThat(m.selectById(entity.getId())).isNull();
            InterceptorIgnoreHelper.execute(IgnoreStrategy.builder().tenantLine(true).build(), () -> {
                assertThat(m.selectById(entity.getId())).isNotNull();
                assertThat(m.deleteById(entity.getId())).isEqualTo(1);
            });
        });
    }

    @Override
    protected List<Interceptor> interceptors() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        interceptor.addInnerInterceptor(new TenantLineInnerInterceptor(() -> new LongValue(1)));
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.H2));
        return Collections.singletonList(interceptor);
    }

    @Override
    protected String tableDataSql() {
        return "insert into entity values(1111,'娇妹',3)";
    }

    @Override
    protected List<String> tableSql() {
        return Arrays.asList(
                "drop table if exists entity",
                "CREATE TABLE IF NOT EXISTS entity (\n" + "id BIGINT(20) NOT NULL,\n"
                        + "name VARCHAR(30) NULL DEFAULT NULL,\n" + "tenant_id integer not NULL,\n" + "PRIMARY KEY (id)"
                        + ")");
    }

    @CacheNamespace
    public interface EntityMapper extends BaseMapper<Entity> {

        /*
         * 请注意: Mybatis的接口方法虽然支持重载,但是底层MappedStatement是只能有一份的,也就是MappedStatement(类名+方法名)组成唯一性.
         *
         * 低版本(<3.5.10)下,忽略BaseMapper上的方法,可通过重写其中方法来标记
         * 例如忽略deleteById方法,直接覆写 int deleteById(Entity entity); 这样就会把deleteById相关的重载方法都会重写掉,因为忽略方式是对MappedStatement级别生效的
         *
         * (高版本才支持)
         * 但是建议按照如果有需要跳过一些插件的方法,通过自定义方法标记作用跳过会好点.
         * 例如我调用deleteById,默认情况下是需要拼接租户条件的,但如果有些特殊需求,想忽略跳过租户的时候,可以直接自定义个默认方法(例如deleteByIdWithIgnore)来调用baseMapper方法得deleteById
         */

        @InterceptorIgnore(tenantLine = "true")
        default int deleteByIdWithIgnore(Serializable id) {
            return deleteById(id);
        }

        @InterceptorIgnore(tenantLine = "true")
        default Entity selectByIdWithIgnore(Serializable id) {
            return selectById(id);
        }

        @InterceptorIgnore(tenantLine = "false")
        default Entity selectByIdWithIgnore2(Serializable id) {
            return selectByIdWithIgnore(id);
        }

        default Entity selectByIdWithIgnore3(Serializable id) {
            return selectByIdWithIgnore(id);
        }

        default Entity selectByIdWithIgnore4(Serializable id) {
            return selectByIdWithIgnore2(id);
        }

        default Entity selectByIdWithIgnore5(Serializable id) {
            return selectById(id);
        }

        default Entity selectByIdWithIgnore6(IgnoreStrategy ignoreStrategy, Serializable id) {
            return InterceptorIgnoreHelper.execute(ignoreStrategy, () -> selectById(id));
        }

        default Entity selectByIdWithIgnore7(IgnoreStrategy ignoreStrategy, Serializable id) {
            return InterceptorIgnoreHelper.execute(ignoreStrategy, () -> selectByIdWithIgnore(id));
        }

        default Entity selectByIdWithIgnore8(IgnoreStrategy ignoreStrategy, Serializable id) {
            return InterceptorIgnoreHelper.execute(ignoreStrategy, () -> selectByIdWithIgnore2(id));
        }

        //    /**
        //     *  //TODO 由于是对ms级别的忽略,所以不考虑重载方法, 忽略deleteById方法
        //     */
        //    @InterceptorIgnore(tenantLine = "true")
        //    int deleteById(Entity entity);

    }

    @Data
    @Accessors(chain = true)
    public static class Entity implements Serializable {

        private Long id;

        private String name;

        private Integer tenantId;
    }
}
