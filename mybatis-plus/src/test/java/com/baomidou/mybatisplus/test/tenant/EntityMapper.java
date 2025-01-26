package com.baomidou.mybatisplus.test.tenant;

import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.plugins.IgnoreStrategy;
import com.baomidou.mybatisplus.core.plugins.InterceptorIgnoreHelper;
import org.apache.ibatis.annotations.CacheNamespace;

import java.io.Serializable;

/**
 * @author miemie
 * @since 2020-06-24
 */
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
        return InterceptorIgnoreHelper.execute(ignoreStrategy, ()-> selectById(id));
    }

    default Entity selectByIdWithIgnore7(IgnoreStrategy ignoreStrategy, Serializable id) {
        return InterceptorIgnoreHelper.execute(ignoreStrategy, ()-> selectByIdWithIgnore(id));
    }

    default Entity selectByIdWithIgnore8(IgnoreStrategy ignoreStrategy, Serializable id) {
        return InterceptorIgnoreHelper.execute(ignoreStrategy, ()-> selectByIdWithIgnore2(id));
    }



//    /**
//     *  //TODO 由于是对ms级别的忽略,所以不考虑重载方法, 忽略deleteById方法
//     */
//    @InterceptorIgnore(tenantLine = "true")
//    int deleteById(Entity entity);

}
