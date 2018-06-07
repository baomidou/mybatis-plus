package com.baomidou.mybatisplus.autoconfigure;


import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URL;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.ibatis.io.VFS;

import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;

/**
 * @author Hans Westerbeek
 * @author Eddú Meléndez
 * @author Kazuki Shimizu
 */
public class SpringBootVFS extends VFS {

    private final ResourcePatternResolver resourceResolver;

    public SpringBootVFS() {
        this.resourceResolver = new PathMatchingResourcePatternResolver(getClass().getClassLoader());
    }

    @Override
    public boolean isValid() {
        return true;
    }

    @Override
    protected List<String> list(URL url, String path) throws IOException {
        Resource[] resources = resourceResolver.getResources("classpath*:" + path + "/**/*.class");
        return Stream.of(resources)
            .map(resource -> preserveSubpackageName(resource, path))
            .collect(Collectors.toList());
    }

    private static String preserveSubpackageName(final Resource resource, final String rootPath) {
        try {
            final String uriStr = resource.getURI().toString();
            final int start = uriStr.indexOf(rootPath);
            return uriStr.substring(start);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

}
