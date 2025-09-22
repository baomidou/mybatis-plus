package com.baomidou.mybatisplus.core;


import com.baomidou.mybatisplus.base.Overwrite;
import com.baomidou.mybatisplus.base.OverwriteFile;

/**
 * {@link org.apache.ibatis.binding.MapperRegistry}
 *
 * @author miemie
 * @since 2025/9/18
 */
public class MapperRegistry extends OverwriteFile {

    public MapperRegistry() {
        addStep(i -> i
            .source("""
                public <T> boolean hasMapper(Class<T> type) {
                    return knownMappers.containsKey(type);
                }
                """)
            .target("""
                /**
                 * 清空 Mapper 缓存信息
                 */
                public <T> void removeMapper(Class<T> type) {
                    knownMappers.entrySet().stream().filter(t -> t.getKey().getName().equals(type.getName()))
                        .findFirst().ifPresent(t -> knownMappers.remove(t.getKey()));
                }
                """)
        );
        addStep(i -> i
            .source("""
                throw new BindingException("Type " + type + " is already known to the MapperRegistry.");
                """)
            .target("""
                // throw new BindingException("Type " + type + " is already known to the MapperRegistry.");
                return;
                """)
            .operate(Overwrite.Operate.COVERAGE)
        );
    }
}
