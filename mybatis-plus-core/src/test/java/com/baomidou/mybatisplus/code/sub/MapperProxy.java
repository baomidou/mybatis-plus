package com.baomidou.mybatisplus.code.sub;

import com.baomidou.mybatisplus.code.Overwrite;
import com.baomidou.mybatisplus.code.OverwriteFile;

/**
 * {@link org.apache.ibatis.binding.MapperProxy}
 *
 * @author miemie
 * @since 2025/9/16
 */
public class MapperProxy extends OverwriteFile {

    public MapperProxy() {
        addStep(i -> i
            .front(new Overwrite.Point("import java.util.Map;"))
            .imports("""
                import com.baomidou.mybatisplus.core.metadata.MapperProxyMetadata;
                import com.baomidou.mybatisplus.core.plugins.IgnoreStrategy;
                import com.baomidou.mybatisplus.core.plugins.InterceptorIgnoreHelper;
                import com.baomidou.mybatisplus.core.toolkit.MybatisUtils;
                """));
        addStep(i -> i
            .front(new Overwrite.Point(148, "public DefaultMethodInvoker(MethodHandle methodHandle) {"))
            .behind(new Overwrite.Point(154, true, "return methodHandle.bindTo(proxy).invokeWithArguments(args);"))
            .content(Overwrite.Content.builder()
                .code("""
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
                    """)
                .frontDown(5).build())
            .content(Overwrite.Content.builder()
                .code("""
                    } finally {
                            InterceptorIgnoreHelper.clearIgnoreStrategy();
                        }
                    }
                    """)
                .behindUp(-1).build())
        );
    }
}
