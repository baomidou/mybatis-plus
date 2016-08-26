package com.baomidou.mybatisplus.refresh;

import com.baomidou.mybatisplus.MybatisConfiguration;
import org.apache.ibatis.binding.MapperRegistry;
import org.apache.ibatis.builder.xml.XMLMapperBuilder;
import org.apache.ibatis.builder.xml.XMLMapperEntityResolver;
import org.apache.ibatis.executor.ErrorContext;
import org.apache.ibatis.executor.keygen.SelectKeyGenerator;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.parsing.XNode;
import org.apache.ibatis.parsing.XPathParser;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.core.NestedIOException;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.*;
import java.util.logging.Logger;

/**
 * Created by nieqiurong on 2016/8/25 16:13.
 */
public class MapperRefresh implements Runnable{
    protected final Logger logger = Logger.getLogger(MapperRefresh.class.getName());
    private SqlSessionFactory sqlSessionFactory;
    private Resource[] mapperLocations;
    private Long beforeTime = 0L;
    private Configuration configuration;
    /**
     *  是否开启刷新mapper
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
     * 记录jar包存在的mapper
     */
    private static Map<String,List<Resource>> jarMapper = new HashMap<String, List<Resource>>();

    public MapperRefresh(Resource[] mapperLocations, SqlSessionFactory sqlSessionFactory, int delaySeconds, int sleepSeconds) {
        this.mapperLocations = mapperLocations;
        this.sqlSessionFactory = sqlSessionFactory;
        this.delaySeconds = delaySeconds;
        this.enabled = true;
        this.sleepSeconds = sleepSeconds;
        this.run();
    }

    public MapperRefresh(Resource[] mapperLocations, SqlSessionFactory sqlSessionFactory) {
        this.mapperLocations = mapperLocations;
        this.sqlSessionFactory = sqlSessionFactory;
        this.run();

    }

    public MapperRefresh(Resource[] mapperLocations, SqlSessionFactory sqlSessionFactory, boolean enabled) {
        this.mapperLocations = mapperLocations;
        this.sqlSessionFactory = sqlSessionFactory;
        this.enabled = enabled;
        this.run();
    }

    public void run() {
        beforeTime = System.currentTimeMillis();
        if (enabled) {
            final MapperRefresh runnable = this;
            new Thread(new Runnable(){
                @Override
                public void run() {
                    if (fileSet == null) {
                        fileSet = new HashSet<String>();
                        for (Resource mapperLocation : mapperLocations) {
                            try {
                                if (ResourceUtils.isJarURL(mapperLocation.getURL())){
                                    String key = new UrlResource(ResourceUtils.extractJarFileURL(mapperLocation.getURL())).getFile().getPath();
                                    fileSet.add(key);
                                    if(jarMapper.get(key)!=null){
                                        jarMapper.get(key).add(mapperLocation);
                                    }else{
                                        List<Resource> resourcesList = new ArrayList<Resource>();
                                        resourcesList.add(mapperLocation);
                                        jarMapper.put(key,resourcesList);
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
                    while (true) {
                        try {
                            for (String filePath : fileSet){
                                File file = new File(filePath);
                                if(file!=null&&file.isFile()&&file.lastModified()>beforeTime){
                                    MybatisConfiguration.IS_REFRESH = true;
                                    List<Resource> removeList = jarMapper.get(filePath);
                                    if(removeList!=null&&!removeList.isEmpty()){//如果是jar包中的xml，将刷新jar包中存在的所有xml，后期再修改加载jar中修改过后的xml
                                        for(Resource resource:removeList){
                                            runnable.refresh(resource);
                                        }
                                    }else{
                                        runnable.refresh(new FileSystemResource(file));
                                    }
                                }
                            }
                            if(MybatisConfiguration.IS_REFRESH)beforeTime = System.currentTimeMillis();
                            MybatisConfiguration.IS_REFRESH = false;
                        } catch (Exception exception) {
                            exception.printStackTrace();
                        }
                        try {
                            Thread.sleep(sleepSeconds * 1000);
                        } catch (InterruptedException interruptedException) {
                            interruptedException.printStackTrace();
                        }

                    }
                }
            },"mybatis-plus MapperRefresh").start();
        }
    }

    /**
     * 刷新mapper
     * @throws Exception
     */
    private void refresh(Resource resource) throws Exception {
        this.configuration = sqlSessionFactory.getConfiguration();
        boolean isSupper = configuration.getClass().getSuperclass()==Configuration.class;
        try {
            Field loadedResourcesField = isSupper?configuration.getClass().getSuperclass().getDeclaredField("loadedResources"):configuration.getClass().getDeclaredField("loadedResources");
            loadedResourcesField.setAccessible(true);
            Set loadedResourcesSet = ((Set) loadedResourcesField.get(configuration));
            XPathParser xPathParser = new XPathParser(resource.getInputStream(), true, configuration.getVariables(),new XMLMapperEntityResolver());
            XNode context = xPathParser.evalNode("/mapper");
            String namespace = context.getStringAttribute("namespace");
            Field field = MapperRegistry.class.getDeclaredField("knownMappers");
            field.setAccessible(true);
            Map mapConfig = (Map) field.get(configuration.getMapperRegistry());
            mapConfig.remove(Resources.classForName(namespace));
            loadedResourcesSet.remove(resource.toString());
            configuration.getCacheNames().remove(namespace);
            cleanParameterMap(context.evalNodes("/mapper/parameterMap"),namespace);
            cleanResultMap(context.evalNodes("/mapper/resultMap"),namespace);
            cleanKeyGenerators(context.evalNodes("insert|update"),namespace);
            cleanSqlElement(context.evalNodes("/mapper/sql"),namespace);
            XMLMapperBuilder xmlMapperBuilder = new XMLMapperBuilder(resource.getInputStream(), sqlSessionFactory.getConfiguration(),   //注入的sql先不进行处理了
                    resource.toString(), sqlSessionFactory.getConfiguration().getSqlFragments());
            xmlMapperBuilder.parse();
            logger.info("refresh:"+resource+",success!");
        } catch (Exception e) {
            throw new NestedIOException("Failed to parse mapping resource: '" + resource + "'", e);
        } finally {
            ErrorContext.instance().reset();
        }
    }

    /**
     * 清理parameterMap
     * @param list
     * @param namespace
     */
    private void cleanParameterMap(List<XNode> list,String namespace){
        for (XNode parameterMapNode : list) {
            String id = parameterMapNode.getStringAttribute("id");
            configuration.getParameterMaps().remove(namespace+"."+id);
        }
    }

    /**
     * 清理resultMap
     * @param list
     * @param namespace
     */
    private void cleanResultMap(List<XNode> list,String namespace){
        for (XNode resultMapNode : list) {
            String id = resultMapNode.getStringAttribute("id",
                    resultMapNode.getValueBasedIdentifier());
            configuration.getResultMapNames().remove(id);
            configuration.getResultMapNames().remove(namespace+"."+id);
        }
    }

    /**
     * 清理selectKey
     * @param list
     * @param namespace
     */
    private void cleanKeyGenerators(List<XNode> list,String namespace){
        for (XNode context : list) {
            String id = context.getStringAttribute("id");
            configuration.getKeyGeneratorNames().remove(id+SelectKeyGenerator.SELECT_KEY_SUFFIX);
            configuration.getKeyGeneratorNames().remove(namespace+"."+id+SelectKeyGenerator.SELECT_KEY_SUFFIX);
        }
    }

    /**
     * 清理sql节点缓存
     * @param list
     * @param namespace
     */
    private void cleanSqlElement(List<XNode> list,String namespace){
        for (XNode context : list) {
            String id = context.getStringAttribute("id");
            configuration.getSqlFragments().remove(id);
            configuration.getSqlFragments().remove(namespace+"."+id);
        }
    }

}