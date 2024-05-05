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
package com.baomidou.mybatisplus.generator;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.generator.config.*;
import com.baomidou.mybatisplus.generator.engine.AbstractTemplateEngine;
import org.jetbrains.annotations.NotNull;

import java.util.Scanner;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * mybatis plus FastAutoGenerator
 *
 * @author L.cm, lanjerry
 * @since 2021-07-22
 */
public final class FastAutoGenerator {

    /**
     * 数据源配置 Builder
     */
    private final DataSourceConfig.Builder dataSourceConfigBuilder;

    /**
     * 全局配置 Builder
     */
    private final GlobalConfig.Builder globalConfigBuilder;

    /**
     * 包配置 Builder
     */
    private final PackageConfig.Builder packageConfigBuilder;

    /**
     * 策略配置 Builder
     */
    private final StrategyConfig.Builder strategyConfigBuilder;

    /**
     * 注入配置 Builder
     */
    private final InjectionConfig.Builder injectionConfigBuilder;

    /**
     * 模板配置 Builder
     * @deprecated 3.5.6 {@link #strategyConfigBuilder}
     */
    @Deprecated
    private final TemplateConfig.Builder templateConfigBuilder;

    /**
     * 模板引擎
     */
    private AbstractTemplateEngine templateEngine;

    private FastAutoGenerator(DataSourceConfig.Builder dataSourceConfigBuilder) {
        this.dataSourceConfigBuilder = dataSourceConfigBuilder;
        this.globalConfigBuilder = new GlobalConfig.Builder();
        this.packageConfigBuilder = new PackageConfig.Builder();
        this.strategyConfigBuilder = new StrategyConfig.Builder();
        this.injectionConfigBuilder = new InjectionConfig.Builder();
        this.templateConfigBuilder = new TemplateConfig.Builder();
    }

    public static FastAutoGenerator create(@NotNull String url, String username, String password) {
        return new FastAutoGenerator(new DataSourceConfig.Builder(url, username, password));
    }

    public static FastAutoGenerator create(@NotNull DataSourceConfig.Builder dataSourceConfigBuilder) {
        return new FastAutoGenerator(dataSourceConfigBuilder);
    }

    /**
     * 读取控制台输入内容
     */
    private final Scanner scanner = new Scanner(System.in);

    /**
     * 控制台输入内容读取并打印提示信息
     *
     * @param message 提示信息
     * @return String
     */
    public String scannerNext(String message) {
        System.out.println(message);
        String nextLine = scanner.nextLine();
        if (StringUtils.isBlank(nextLine)) {
            // 如果输入空行继续等待
            return scanner.next();
        }
        return nextLine;
    }

    /**
     * 全局配置
     *
     * @param consumer 自定义全局配置
     * @return FastAutoGenerator
     */
    public FastAutoGenerator dataSourceConfig(Consumer<DataSourceConfig.Builder> consumer) {
        consumer.accept(this.dataSourceConfigBuilder);
        return this;
    }

    public FastAutoGenerator dataSourceConfig(BiConsumer<Function<String, String>, DataSourceConfig.Builder> biConsumer) {
        biConsumer.accept(this::scannerNext, this.dataSourceConfigBuilder);
        return this;
    }

    /**
     * 全局配置
     *
     * @param consumer 自定义全局配置
     * @return FastAutoGenerator
     */
    public FastAutoGenerator globalConfig(Consumer<GlobalConfig.Builder> consumer) {
        consumer.accept(this.globalConfigBuilder);
        return this;
    }

    public FastAutoGenerator globalConfig(BiConsumer<Function<String, String>, GlobalConfig.Builder> biConsumer) {
        biConsumer.accept(this::scannerNext, this.globalConfigBuilder);
        return this;
    }

    /**
     * 包配置
     *
     * @param consumer 自定义包配置
     * @return FastAutoGenerator
     */
    public FastAutoGenerator packageConfig(Consumer<PackageConfig.Builder> consumer) {
        consumer.accept(this.packageConfigBuilder);
        return this;
    }

    public FastAutoGenerator packageConfig(BiConsumer<Function<String, String>, PackageConfig.Builder> biConsumer) {
        biConsumer.accept(this::scannerNext, this.packageConfigBuilder);
        return this;
    }

    /**
     * 策略配置
     *
     * @param consumer 自定义策略配置
     * @return FastAutoGenerator
     */
    public FastAutoGenerator strategyConfig(Consumer<StrategyConfig.Builder> consumer) {
        consumer.accept(this.strategyConfigBuilder);
        return this;
    }

    public FastAutoGenerator strategyConfig(BiConsumer<Function<String, String>, StrategyConfig.Builder> biConsumer) {
        biConsumer.accept(this::scannerNext, this.strategyConfigBuilder);
        return this;
    }

    /**
     * 注入配置
     *
     * @param consumer 自定义注入配置
     * @return FastAutoGenerator
     */
    public FastAutoGenerator injectionConfig(Consumer<InjectionConfig.Builder> consumer) {
        consumer.accept(this.injectionConfigBuilder);
        return this;
    }

    public FastAutoGenerator injectionConfig(BiConsumer<Function<String, String>, InjectionConfig.Builder> biConsumer) {
        biConsumer.accept(this::scannerNext, this.injectionConfigBuilder);
        return this;
    }

    /**
     * 模板配置
     *
     * @param consumer 自定义模板配置
     * @return FastAutoGenerator
     * @deprecated 3.5.6 {@link #strategyConfig(Consumer)}
     */
    @Deprecated
    public FastAutoGenerator templateConfig(Consumer<TemplateConfig.Builder> consumer) {
        consumer.accept(this.templateConfigBuilder);
        return this;
    }

    /**
     * @param biConsumer
     * @return FastAutoGenerator
     * @deprecated 3.5.6 {@link #strategyConfig(BiConsumer)}
     */
    @Deprecated
    public FastAutoGenerator templateConfig(BiConsumer<Function<String, String>, TemplateConfig.Builder> biConsumer) {
        biConsumer.accept(this::scannerNext, this.templateConfigBuilder);
        return this;
    }

    /**
     * 模板引擎配置
     *
     * @param templateEngine 模板引擎
     * @return FastAutoGenerator
     */
    public FastAutoGenerator templateEngine(AbstractTemplateEngine templateEngine) {
        this.templateEngine = templateEngine;
        return this;
    }

    public void execute() {
        new AutoGenerator(this.dataSourceConfigBuilder.build())
            // 全局配置
            .global(this.globalConfigBuilder.build())
            // 包配置
            .packageInfo(this.packageConfigBuilder.build())
            // 策略配置
            .strategy(this.strategyConfigBuilder.build())
            // 注入配置
            .injection(this.injectionConfigBuilder.build())
            // 模板配置
            .template(this.templateConfigBuilder.build())
            // 执行
            .execute(this.templateEngine);
    }
}
