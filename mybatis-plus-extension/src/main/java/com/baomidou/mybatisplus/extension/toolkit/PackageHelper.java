/*
 * Copyright (c) 2011-2014, hubin (jobob@qq.com).
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.baomidou.mybatisplus.extension.toolkit;

import com.baomidou.mybatisplus.core.toolkit.ExceptionUtils;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.util.ClassUtils;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

/**
 * <p>
 * 包扫描辅助类
 * </p>
 *
 * @author hubin
 * @since 2016-06-16
 */
public class PackageHelper {

    /**
     * <p>
     * 别名通配符设置
     * </p>
     * <p>
     * <property name="typeAliasesPackage" value="com.baomidou.*.entity"/>
     * </p>
     *
     * @param typeAliasesPackage 类别名包路径
     * @return
     */
    public static String[] convertTypeAliasesPackage(String typeAliasesPackage) {
        ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        MetadataReaderFactory metadataReaderFactory = new CachingMetadataReaderFactory(resolver);
        String pkg = ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX
            + ClassUtils.convertClassNameToResourcePath(typeAliasesPackage) + "/*.class";
        /*
         * 将加载多个绝对匹配的所有Resource
         * 将首先通过ClassLoader.getResource("META-INF")加载非模式路径部分，然后进行遍历模式匹配，排除重复包路径
         */
        try {
            Set<String> set = new HashSet<>();
            Resource[] resources = resolver.getResources(pkg);
            if (resources != null && resources.length > 0) {
                MetadataReader metadataReader;
                for (Resource resource : resources) {
                    if (resource.isReadable()) {
                        metadataReader = metadataReaderFactory.getMetadataReader(resource);
                        set.add(Class.forName(metadataReader.getClassMetadata().getClassName()).getPackage().getName());
                    }
                }
            }
            if (!set.isEmpty()) {
                return set.toArray(new String[]{});
            }
            return new String[0];
        } catch (Exception e) {
            throw ExceptionUtils.mpe("not find typeAliasesPackage: %s", e, pkg);
        }
    }


    /**
     * <p>
     * 扫描获取指定包路径所有类
     * </p>
     *
     * @param typePackage 扫描类包路径
     * @return
     */
    public static Set<Class> scanTypePackage(String typePackage) {
        ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        MetadataReaderFactory metadataReaderFactory = new CachingMetadataReaderFactory(resolver);
        String pkg = ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX
            + ClassUtils.convertClassNameToResourcePath(typePackage) + "/*.class";
        /*
         * 将加载多个绝对匹配的所有Resource
         * 将首先通过ClassLoader.getResource("META-INF")加载非模式路径部分，然后进行遍历模式匹配，排除重复包路径
         */
        try {
            Set<Class> set = new HashSet<>();
            Resource[] resources = resolver.getResources(pkg);
            if (resources != null && resources.length > 0) {
                MetadataReader metadataReader;
                for (Resource resource : resources) {
                    if (resource.isReadable()) {
                        metadataReader = metadataReaderFactory.getMetadataReader(resource);
                        set.add(Class.forName(metadataReader.getClassMetadata().getClassName()));
                    }
                }
            }
            return set;
        } catch (Exception e) {
            throw ExceptionUtils.mpe("not find scanTypePackage: %s", e, pkg);
        }
    }

    /**
     * <p>
     * 新建文件目录
     * </p>
     *
     * @param file 文件
     */
    public static void mkDir(File file) {
        if (file.getParentFile().exists()) {
            file.mkdir();
        } else {
            mkDir(file.getParentFile());
            file.mkdir();
        }
    }
}
