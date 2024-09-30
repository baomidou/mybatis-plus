package com.baomidou.mybatisplus.solon.integration.aot;

import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.core.MybatisXMLLanguageDriver;
import com.baomidou.mybatisplus.core.conditions.AbstractLambdaWrapper;
import com.baomidou.mybatisplus.core.conditions.AbstractWrapper;
import com.baomidou.mybatisplus.core.conditions.ISqlSegment;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.interfaces.Compare;
import com.baomidou.mybatisplus.core.conditions.interfaces.Func;
import com.baomidou.mybatisplus.core.conditions.interfaces.Join;
import com.baomidou.mybatisplus.core.conditions.interfaces.Nested;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.Query;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import org.noear.solon.aot.RuntimeNativeMetadata;
import org.noear.solon.aot.RuntimeNativeRegistrar;
import org.noear.solon.aot.hint.ExecutableMode;
import org.noear.solon.aot.hint.MemberCategory;
import org.noear.solon.core.AppContext;

import java.lang.invoke.SerializedLambda;

/**
 * mybatis-plus aot 注册 native 元数据
 *
 * @author songyinyin
 * @since 2.3
 */
public class MybatisPlusRuntimeNativeRegistrar implements RuntimeNativeRegistrar {

    @Override
    public void register(AppContext context, RuntimeNativeMetadata metadata) {
        metadata.registerDefaultConstructor(MybatisXMLLanguageDriver.class);
        metadata.registerReflection(MybatisConfiguration.class, MemberCategory.DECLARED_FIELDS, MemberCategory.INTROSPECT_PUBLIC_CONSTRUCTORS);
        metadata.registerAllDeclaredMethod(MybatisConfiguration.class, ExecutableMode.INVOKE);

        metadata.registerReflection(AbstractLambdaWrapper.class, MemberCategory.DECLARED_FIELDS, MemberCategory.INTROSPECT_PUBLIC_CONSTRUCTORS);

        metadata.registerJdkProxy(AbstractWrapper.DoSomething.class);
        metadata.registerReflection(AbstractWrapper.DoSomething.class);
        metadata.registerReflection(AbstractWrapper.class, MemberCategory.DECLARED_FIELDS, MemberCategory.INTROSPECT_PUBLIC_CONSTRUCTORS);
        metadata.registerAllDeclaredMethod(AbstractWrapper.class, ExecutableMode.INVOKE);

        metadata.registerReflection(ISqlSegment.class, MemberCategory.DECLARED_FIELDS, MemberCategory.INTROSPECT_PUBLIC_CONSTRUCTORS);
        metadata.registerReflection(Wrapper.class, MemberCategory.DECLARED_FIELDS, MemberCategory.INTROSPECT_PUBLIC_CONSTRUCTORS);

        metadata.registerAllDeclaredMethod(Wrapper.class, ExecutableMode.INVOKE);

        metadata.registerReflection(Compare.class, MemberCategory.DECLARED_FIELDS, MemberCategory.INTROSPECT_PUBLIC_CONSTRUCTORS);
        metadata.registerReflection(Func.class, MemberCategory.DECLARED_FIELDS, MemberCategory.INTROSPECT_PUBLIC_CONSTRUCTORS);
        metadata.registerReflection(Join.class, MemberCategory.DECLARED_FIELDS, MemberCategory.INTROSPECT_PUBLIC_CONSTRUCTORS);
        metadata.registerReflection(Nested.class, MemberCategory.DECLARED_FIELDS, MemberCategory.INTROSPECT_PUBLIC_CONSTRUCTORS);

        metadata.registerReflection(LambdaQueryWrapper.class, MemberCategory.DECLARED_FIELDS, MemberCategory.INTROSPECT_PUBLIC_METHODS,
                MemberCategory.INTROSPECT_PUBLIC_METHODS, MemberCategory.INTROSPECT_DECLARED_CONSTRUCTORS);
        metadata.registerAllDeclaredMethod(LambdaQueryWrapper.class, ExecutableMode.INVOKE);

        metadata.registerReflection(Query.class, MemberCategory.DECLARED_FIELDS, MemberCategory.INTROSPECT_PUBLIC_CONSTRUCTORS);

        metadata.registerAllDeclaredMethod(BaseMapper.class, ExecutableMode.INVOKE);

        metadata.registerSerialization(SerializedLambda.class);
        metadata.registerSerialization(SFunction.class);
    }

}
