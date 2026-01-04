package com.baomidou.mybatisplus.autoconfigure.aot;

import org.apache.ibatis.builder.xml.XMLStatementBuilder;
import org.apache.ibatis.cache.decorators.FifoCache;
import org.apache.ibatis.cache.decorators.LruCache;
import org.apache.ibatis.cache.decorators.SoftCache;
import org.apache.ibatis.cache.decorators.WeakCache;
import org.apache.ibatis.cache.impl.PerpetualCache;
import org.apache.ibatis.javassist.util.proxy.ProxyFactory;
import org.apache.ibatis.javassist.util.proxy.RuntimeSupport;
import org.apache.ibatis.logging.Log;
import org.apache.ibatis.logging.commons.JakartaCommonsLoggingImpl;
import org.apache.ibatis.logging.jdk14.Jdk14LoggingImpl;
import org.apache.ibatis.logging.log4j2.Log4j2Impl;
import org.apache.ibatis.logging.nologging.NoLoggingImpl;
import org.apache.ibatis.logging.slf4j.Slf4jImpl;
import org.apache.ibatis.logging.stdout.StdOutImpl;
import org.apache.ibatis.scripting.defaults.RawLanguageDriver;
import org.apache.ibatis.scripting.xmltags.XMLLanguageDriver;
import org.apache.ibatis.session.SqlSessionFactory;
import org.graalvm.nativeimage.hosted.Feature;
import org.graalvm.nativeimage.hosted.RuntimeSerialization;
import org.mybatis.spring.SqlSessionFactoryBean;

import java.io.IOException;
import java.lang.invoke.SerializedLambda;
import java.util.*;
import java.util.stream.Stream;

/**
 * 解决mybatis的native-image的运行问题
 *
 * @author xiaochen
 * @since 2025/8/20
 */
class MybatisPlusFeature implements Feature {

    @Override
    public void beforeAnalysis(BeforeAnalysisAccess access) {
        FeatureUtils featureUtils = new FeatureUtils(access.getApplicationClassLoader());
        serializedLambda(featureUtils, access);
        mybatis(featureUtils, access);
        mybatisPlus(featureUtils, access);
    }

    private void serializedLambda(FeatureUtils featureUtils, BeforeAnalysisAccess access) {
        access.registerReachabilityHandler(duringAnalysisAccess -> {
            RuntimeSerialization.register(SerializedLambda.class);
            try {
                Set<Class<?>> classes = featureUtils.collectClass(featureUtils.findMainPackages());
                classes.forEach(featureUtils::registerSerializationLambdaCapturingClass);
            } catch (Exception ignored) {
            }
        }, SerializedLambda.class);
    }

    private void mybatis(FeatureUtils featureUtils, BeforeAnalysisAccess access) {
        var sqlSessionFactory = featureUtils.loadClass("org.apache.ibatis.session.SqlSessionFactory");
        access.registerReachabilityHandler(duringAnalysisAccess -> {
            Stream.of(RawLanguageDriver.class,
                XMLLanguageDriver.class,
                RuntimeSupport.class,
                ProxyFactory.class,
                Slf4jImpl.class,
                Log.class,
                JakartaCommonsLoggingImpl.class,
                Log4j2Impl.class,
                Jdk14LoggingImpl.class,
                StdOutImpl.class,
                NoLoggingImpl.class,
                SqlSessionFactory.class,
                PerpetualCache.class,
                FifoCache.class,
                LruCache.class,
                SoftCache.class,
                WeakCache.class,
                SqlSessionFactoryBean.class,
                ArrayList.class,
                HashMap.class,
                TreeSet.class,
                HashSet.class
            ).forEach(featureUtils::registerReflection);
            try {
                featureUtils.registerResource(XMLStatementBuilder.class, featureUtils.findResources("org/apache/ibatis/builder/xml",
                    name -> name.endsWith(".dtd") || name.endsWith(".xsd")).toArray(FeatureUtils.EMPTY_STRING_ARRAY));
            } catch (IOException e) {
                e.printStackTrace();
            }
            featureUtils.registerProxyIfPresent("org.apache.ibatis.executor.Executor", "org.apache.ibatis.executor.statement.StatementHandler");
            // spring项目不需要下面的代码
            try {
                for (Class<?> mainClass : featureUtils.findMainClasses()) {
                    featureUtils.registerResource(mainClass.getModule(),
                        featureUtils.findResources("",
                                name -> name.endsWith(".xml"))
                            .toArray(FeatureUtils.EMPTY_STRING_ARRAY));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }, sqlSessionFactory);
    }

    private void mybatisPlus(FeatureUtils featureUtils, BeforeAnalysisAccess access) {
        Class<?> wrapper = featureUtils.loadClass("com.baomidou.mybatisplus.core.conditions.Wrapper");
        var sqlSessionFactory = featureUtils.loadClass("org.apache.ibatis.session.SqlSessionFactory");
        if (wrapper != null && sqlSessionFactory != null) {
            access.registerReachabilityHandler(duringAnalysisAccess -> {
                featureUtils.registerSerializationIfPresent("com.baomidou.mybatisplus.core.toolkit.support.SFunction");
                featureUtils.registerReflectionIfPresent("com.baomidou.mybatisplus.core.MybatisXMLLanguageDriver",
                    "com.baomidou.mybatisplus.core.conditions.ISqlSegment");
                for (Class<?> c : featureUtils.collectClass(wrapper::isAssignableFrom, "com.baomidou.mybatisplus")) {
                    featureUtils.registerReflection(c);
                }
                featureUtils.registerReflectionIfPresent("com.baomidou.mybatisplus.core.override.MybatisMapperProxy");
            }, sqlSessionFactory);
        }
    }

}
