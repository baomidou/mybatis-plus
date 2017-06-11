/**
 * Copyright (c) 2011-2020, hubin (jobob@qq.com).
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
package com.baomidou.mybatisplus.spring;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.ibatis.binding.MapperRegistry;
import org.apache.ibatis.builder.xml.XMLMapperBuilder;
import org.apache.ibatis.builder.xml.XMLMapperEntityResolver;
import org.apache.ibatis.executor.ErrorContext;
import org.apache.ibatis.executor.keygen.SelectKeyGenerator;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.logging.Log;
import org.apache.ibatis.logging.LogFactory;
import org.apache.ibatis.parsing.XNode;
import org.apache.ibatis.parsing.XPathParser;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.util.ResourceUtils;

import com.baomidou.mybatisplus.entity.GlobalConfiguration;
import com.baomidou.mybatisplus.toolkit.SystemClock;

/**
 * <p>
 * 切莫用于生产环境（后果自负）<br>
 * Mybatis 映射文件热加载（发生变动后自动重新加载）.<br>
 * 方便开发时使用，不用每次修改xml文件后都要去重启应用.<br>
 * </p>
 *
 * @author nieqiurong
 * @Date 2016-08-25
 */
public class MybatisMapperRefresh implements Runnable {

    private static final Log logger = LogFactory.getLog(MybatisMapperRefresh.class);
    /**
     * 记录jar包存在的mapper
     */
    private static final Map<String, List<Resource>> jarMapper = new HashMap<>();
    private SqlSessionFactory sqlSessionFactory;
    @Deprecated
    private Resource[] mapperLocations;
    private Long beforeTime = 0L;
    private Configuration configuration;
    /**
     * 是否开启刷新mapper
     */
    private boolean enabled;
    /**
     * xml文件目录
     */
    private Set<String> fileSet;
    /**
     * 延迟加载时间
     */
    private int delaySeconds = 10;
    /**
     * 刷新间隔时间
     */
    private int sleepSeconds = 20;

    /**
     * see  com.baomidou.mybatisplus.spring.MybatisMapperRefresh#MybatisMapperRefresh(org.apache.ibatis.session.SqlSessionFactory, int, int, boolean)
     * @param mapperLocations
     * @param sqlSessionFactory
     * @param delaySeconds
     * @param sleepSeconds
     * @param enabled
     */
    @Deprecated
    public MybatisMapperRefresh(Resource[] mapperLocations, SqlSessionFactory sqlSessionFactory, int delaySeconds,
                                int sleepSeconds, boolean enabled) {
        this.mapperLocations = mapperLocations.clone();
        this.sqlSessionFactory = sqlSessionFactory;
        this.delaySeconds = delaySeconds;
        this.enabled = enabled;
        this.sleepSeconds = sleepSeconds;
        this.configuration = sqlSessionFactory.getConfiguration();
        this.run();
    }

    /**
     * see com.baomidou.mybatisplus.spring.MybatisMapperRefresh#MybatisMapperRefresh(org.apache.ibatis.session.SqlSessionFactory, boolean)
     * @param mapperLocations
     * @param sqlSessionFactory
     * @param enabled
     */
    @Deprecated
    public MybatisMapperRefresh(Resource[] mapperLocations, SqlSessionFactory sqlSessionFactory, boolean enabled) {
        this.mapperLocations = mapperLocations.clone();
        this.sqlSessionFactory = sqlSessionFactory;
        this.enabled = enabled;
        this.configuration = sqlSessionFactory.getConfiguration();
        this.run();
    }

    public MybatisMapperRefresh(SqlSessionFactory sqlSessionFactory, boolean enabled) throws Exception {
        this.sqlSessionFactory = sqlSessionFactory;
        this.enabled = enabled;
        this.configuration = sqlSessionFactory.getConfiguration();
        this.run();
    }

    public MybatisMapperRefresh(SqlSessionFactory sqlSessionFactory,int delaySeconds, int sleepSeconds,boolean enabled) throws Exception {
        this.sqlSessionFactory = sqlSessionFactory;
        this.delaySeconds = delaySeconds;
        this.sleepSeconds = sleepSeconds;
        this.enabled = enabled;
        this.configuration = sqlSessionFactory.getConfiguration();
        this.run();
    }

    public void run() {
        final GlobalConfiguration globalConfig = GlobalConfiguration.getGlobalConfig(configuration);
        /*
         * 启动 XML 热加载
		 */
        if (enabled) {
            beforeTime = SystemClock.now();
            final MybatisMapperRefresh runnable = this;
            new Thread(new Runnable() {

                public void run() {
                    if (fileSet == null) {
                        fileSet = new HashSet<>();
                        for (Resource mapperLocation : mapperLocations) {
                            try {
                                if (ResourceUtils.isJarURL(mapperLocation.getURL())) {
                                    String key = new UrlResource(ResourceUtils.extractJarFileURL(mapperLocation.getURL()))
                                            .getFile().getPath();
                                    fileSet.add(key);
                                    if (jarMapper.get(key) != null) {
                                        jarMapper.get(key).add(mapperLocation);
                                    } else {
                                        List<Resource> resourcesList = new ArrayList<>();
                                        resourcesList.add(mapperLocation);
                                        jarMapper.put(key, resourcesList);
                                    }
                                } else {
                                    fileSet.add(mapperLocation.getFile().getPath());
                                }
                            } catch (IOException ioException) {
                                ioException.printStackTrace();
                            }
                        }
                    }
                    try {
                        Thread.sleep(delaySeconds * 1000);
                    } catch (InterruptedException interruptedException) {
                        interruptedException.printStackTrace();
                    }
                    do {
                        try {
                            for (String filePath : fileSet) {
                                File file = new File(filePath);
                                if (file.isFile() && file.lastModified() > beforeTime) {
                                    globalConfig.setRefresh(true);
                                    List<Resource> removeList = jarMapper.get(filePath);
                                    if (removeList != null && !removeList.isEmpty()) {// 如果是jar包中的xml，将刷新jar包中存在的所有xml，后期再修改加载jar中修改过后的xml
                                        for (Resource resource : removeList) {
                                            runnable.refresh(resource);
                                        }
                                    } else {
                                        runnable.refresh(new FileSystemResource(file));
                                    }
                                }
                            }
                            if (globalConfig.isRefresh()) {
                                beforeTime = SystemClock.now();
                            }
                            globalConfig.setRefresh(true);
                        } catch (Exception exception) {
                            exception.printStackTrace();
                        }
                        try {
                            Thread.sleep(sleepSeconds * 1000);
                        } catch (InterruptedException interruptedException) {
                            interruptedException.printStackTrace();
                        }

                    } while (true);
                }
            }, "mybatis-plus MapperRefresh").start();
        }
    }

    /**
     * 刷新mapper
     *
     * @throws Exception
     */
    @SuppressWarnings("rawtypes")
    private void refresh(Resource resource) throws ClassNotFoundException, NoSuchFieldException, IllegalAccessException {
        this.configuration = sqlSessionFactory.getConfiguration();
        boolean isSupper = configuration.getClass().getSuperclass() == Configuration.class;
        try {
            Field loadedResourcesField = isSupper ? configuration.getClass().getSuperclass().getDeclaredField("loadedResources")
                    : configuration.getClass().getDeclaredField("loadedResources");
            loadedResourcesField.setAccessible(true);
            Set loadedResourcesSet = ((Set) loadedResourcesField.get(configuration));
            XPathParser xPathParser = new XPathParser(resource.getInputStream(), true, configuration.getVariables(),
                    new XMLMapperEntityResolver());
            XNode context = xPathParser.evalNode("/mapper");
            String namespace = context.getStringAttribute("namespace");
            Field field = MapperRegistry.class.getDeclaredField("knownMappers");
            field.setAccessible(true);
            Map mapConfig = (Map) field.get(configuration.getMapperRegistry());
            mapConfig.remove(Resources.classForName(namespace));
            loadedResourcesSet.remove(resource.toString());
            configuration.getCacheNames().remove(namespace);
            cleanParameterMap(context.evalNodes("/mapper/parameterMap"), namespace);
            cleanResultMap(context.evalNodes("/mapper/resultMap"), namespace);
            cleanKeyGenerators(context.evalNodes("insert|update"), namespace);
            cleanSqlElement(context.evalNodes("/mapper/sql"), namespace);
            XMLMapperBuilder xmlMapperBuilder = new XMLMapperBuilder(resource.getInputStream(),
                    sqlSessionFactory.getConfiguration(), // 注入的sql先不进行处理了
                    resource.toString(), sqlSessionFactory.getConfiguration().getSqlFragments());
            xmlMapperBuilder.parse();
            logger.debug("refresh: '" + resource + "', success!");
        } catch (IOException e) {
            logger.error("Refresh IOException :" + e.getMessage());
        } finally {
            ErrorContext.instance().reset();
        }
    }

    /**
     * 清理parameterMap
     *
     * @param list
     * @param namespace
     */
    private void cleanParameterMap(List<XNode> list, String namespace) {
        for (XNode parameterMapNode : list) {
            String id = parameterMapNode.getStringAttribute("id");
            configuration.getParameterMaps().remove(namespace + "." + id);
        }
    }

    /**
     * 清理resultMap
     *
     * @param list
     * @param namespace
     */
    private void cleanResultMap(List<XNode> list, String namespace) {
        for (XNode resultMapNode : list) {
            String id = resultMapNode.getStringAttribute("id", resultMapNode.getValueBasedIdentifier());
            configuration.getResultMapNames().remove(id);
            configuration.getResultMapNames().remove(namespace + "." + id);
            clearResultMap(resultMapNode, namespace);
        }
    }

    private void clearResultMap(XNode xNode, String namespace) {
        for (XNode resultChild : xNode.getChildren()) {
            if ("association".equals(resultChild.getName()) || "collection".equals(resultChild.getName())
                    || "case".equals(resultChild.getName())) {
                if (resultChild.getStringAttribute("select") == null) {
                    configuration.getResultMapNames().remove(
                            resultChild.getStringAttribute("id", resultChild.getValueBasedIdentifier()));
                    configuration.getResultMapNames().remove(
                            namespace + "." + resultChild.getStringAttribute("id", resultChild.getValueBasedIdentifier()));
                    if (resultChild.getChildren() != null && !resultChild.getChildren().isEmpty()) {
                        clearResultMap(resultChild, namespace);
                    }
                }
            }
        }
    }

    /**
     * 清理selectKey
     *
     * @param list
     * @param namespace
     */
    private void cleanKeyGenerators(List<XNode> list, String namespace) {
        for (XNode context : list) {
            String id = context.getStringAttribute("id");
            configuration.getKeyGeneratorNames().remove(id + SelectKeyGenerator.SELECT_KEY_SUFFIX);
            configuration.getKeyGeneratorNames().remove(namespace + "." + id + SelectKeyGenerator.SELECT_KEY_SUFFIX);
        }
    }

    /**
     * 清理sql节点缓存
     *
     * @param list
     * @param namespace
     */
    private void cleanSqlElement(List<XNode> list, String namespace) {
        for (XNode context : list) {
            String id = context.getStringAttribute("id");
            configuration.getSqlFragments().remove(id);
            configuration.getSqlFragments().remove(namespace + "." + id);
        }
    }

}