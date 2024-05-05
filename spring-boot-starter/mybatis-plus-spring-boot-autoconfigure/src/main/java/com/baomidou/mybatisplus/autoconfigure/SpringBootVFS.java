/*
 * Copyright (c) 2011-2024, baomidou (jobob@qq.com).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.baomidou.mybatisplus.autoconfigure;


import org.apache.ibatis.io.VFS;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.util.ClassUtils;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.text.Normalizer;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Hans Westerbeek
 * @author Eddú Meléndez
 * @author Kazuki Shimizu
 */
public class SpringBootVFS extends VFS {

    private static Charset urlDecodingCharset;
    private static Supplier<ClassLoader> classLoaderSupplier;
    private final ResourcePatternResolver resourceResolver;

    static {
        setUrlDecodingCharset(Charset.defaultCharset());
        setClassLoaderSupplier(ClassUtils::getDefaultClassLoader);
    }

    public SpringBootVFS() {
        this.resourceResolver = new PathMatchingResourcePatternResolver(classLoaderSupplier.get());
    }

    @Override
    public boolean isValid() {
        return true;
    }

    @Override
    protected List<String> list(URL url, String path) throws IOException {
        String urlString = URLDecoder.decode(url.toString(), urlDecodingCharset.name());
        String baseUrlString = urlString.endsWith("/") ? urlString : urlString.concat("/");
        Resource[] resources = resourceResolver.getResources(baseUrlString + "**/*.class");
        return Stream.of(resources).map(resource -> preserveSubpackageName(baseUrlString, resource, path))
            .collect(Collectors.toList());
    }

    /**
     * Set the charset for decoding an encoded URL string.
     * <p>
     * Default is system default charset.
     * </p>
     *
     * @param charset the charset for decoding an encoded URL string
     * @since 2.3.0
     */
    public static void setUrlDecodingCharset(Charset charset) {
        urlDecodingCharset = charset;
    }

    /**
     * Set the supplier for providing {@link ClassLoader} to used.
     * <p>
     * Default is a returned instance from {@link ClassUtils#getDefaultClassLoader()}.
     * </p>
     *
     * @param supplier the supplier for providing {@link ClassLoader} to used
     * @since 3.0.2
     */
    public static void setClassLoaderSupplier(Supplier<ClassLoader> supplier) {
        classLoaderSupplier = supplier;
    }

    private static String preserveSubpackageName(final String baseUrlString, final Resource resource,
                                                 final String rootPath) {
        try {
            return rootPath + (rootPath.endsWith("/") ? "" : "/") + Normalizer
                .normalize(URLDecoder.decode(resource.getURL().toString(), urlDecodingCharset.name()), Normalizer.Form.NFC)
                .substring(baseUrlString.length());
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
