package com.baomidou.mybatisplus.core;


import com.baomidou.mybatisplus.base.Overwrite;
import com.baomidou.mybatisplus.base.OverwriteFile;

/**
 * {@link org.apache.ibatis.binding.MapperProxy}
 *
 * @author miemie
 * @since 2025/9/16
 */
public class MapperProxy extends OverwriteFile {

    public MapperProxy() {
        addStep(i -> i
            .source("""
                import org.apache.ibatis.util.MapUtil;
                """)
            .target("""
                import com.baomidou.mybatisplus.core.metadata.MapperProxyMetadata;
                import com.baomidou.mybatisplus.core.plugins.IgnoreStrategy;
                import com.baomidou.mybatisplus.core.plugins.InterceptorIgnoreHelper;
                import com.baomidou.mybatisplus.core.toolkit.MybatisUtils;
                """)
        );
        addStep(i -> i
            .source("""
                @Override
                public Object invoke(Object proxy, Method method, Object[] args, SqlSession sqlSession) throws Throwable {
                  return methodHandle.bindTo(proxy).invokeWithArguments(args);
                }
                """)
            .target("""
                @Override
                public Object invoke(Object proxy, Method method, Object[] args, SqlSession sqlSession) throws Throwable {
                    boolean hasIgnoreStrategy = InterceptorIgnoreHelper.hasIgnoreStrategy();
                    if (hasIgnoreStrategy) {
                        return methodHandle.bindTo(proxy).invokeWithArguments(args);
                    } else {
                        try {
                            MapperProxyMetadata mapperProxyMetadata = MybatisUtils.getMapperProxy(proxy);
                            Class<?> mapperInterface = mapperProxyMetadata.getMapperInterface();
                            IgnoreStrategy ignoreStrategy = InterceptorIgnoreHelper.findIgnoreStrategy(mapperInterface, method);
                            if (ignoreStrategy == null) {
                                ignoreStrategy = IgnoreStrategy.builder().build();
                            }
                            InterceptorIgnoreHelper.handle(ignoreStrategy);
                            return methodHandle.bindTo(proxy).invokeWithArguments(args);
                        } finally {
                            InterceptorIgnoreHelper.clearIgnoreStrategy();
                        }
                    }
                }
                """)
            .operate(Overwrite.Operate.COVERAGE)
        );
    }
}
