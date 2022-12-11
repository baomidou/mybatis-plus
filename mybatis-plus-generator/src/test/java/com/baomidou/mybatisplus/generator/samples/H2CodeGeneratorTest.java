package com.baomidou.mybatisplus.generator.samples;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.generator.AutoGenerator;
import com.baomidou.mybatisplus.generator.config.DataSourceConfig;
import com.baomidou.mybatisplus.generator.config.OutputFile;
import com.baomidou.mybatisplus.generator.config.StrategyConfig;
import com.baomidou.mybatisplus.generator.config.builder.CustomFile;
import com.baomidou.mybatisplus.generator.fill.Column;
import com.baomidou.mybatisplus.generator.fill.Property;
import com.baomidou.mybatisplus.generator.query.SQLQuery;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * H2 代码生成
 *
 * @author hubin, lanjerry
 * @since 3.5.3
 */
public class H2CodeGeneratorTest extends BaseGeneratorTest {

    /**
     * 执行初始化数据库脚本
     */
    @BeforeAll
    public static void before() throws SQLException {
        initDataSource(DATA_SOURCE_CONFIG);
    }

    /**
     * 策略配置
     */
    public static StrategyConfig.Builder strategyConfig() {
        return new StrategyConfig.Builder().addInclude("t_simple"); // 设置需要生成的表名
    }

    /**
     * 数据源配置
     */
    private static final DataSourceConfig DATA_SOURCE_CONFIG = new DataSourceConfig
        .Builder("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE;CASE_INSENSITIVE_IDENTIFIERS=TRUE;MODE=MYSQL", "sa", "")
        .databaseQueryClass(SQLQuery.class) // 设置SQL查询方式，默认的是元数据查询方式
        .build();

    /**
     * 简单生成
     */
    @Test
    public void testSimple() {
        AutoGenerator generator = new AutoGenerator(DATA_SOURCE_CONFIG);
        generator.strategy(strategyConfig().build());
        generator.global(globalConfig().build());
        generator.execute();
    }

    /**
     * 过滤表前缀（后缀同理，支持多个）
     * result: t_simple -> simple
     */
    @Test
    public void testTablePrefix() {
        AutoGenerator generator = new AutoGenerator(DATA_SOURCE_CONFIG);
        generator.strategy(strategyConfig().addTablePrefix("t_", "c_").build());
        generator.global(globalConfig().build());
        generator.execute();
    }

    /**
     * 过滤字段后缀（前缀同理，支持多个）
     * result: deleted_flag -> deleted
     */
    @Test
    public void testFieldSuffix() {
        AutoGenerator generator = new AutoGenerator(DATA_SOURCE_CONFIG);
        generator.strategy(strategyConfig().addFieldSuffix("_flag").build());
        generator.global(globalConfig().build());
        generator.execute();
    }

    /**
     * 乐观锁字段设置
     * result: 新增@Version注解
     * 填充字段设置
     * result: 新增@TableField(value = "xxx", fill = FieldFill.xxx)注解
     */
    @Test
    public void testVersionAndFill() {
        AutoGenerator generator = new AutoGenerator(DATA_SOURCE_CONFIG);
        generator.strategy(strategyConfig().entityBuilder()
            .versionColumnName("version") // 基于数据库字段
            .versionPropertyName("version")// 基于模型属性
            .addTableFills(new Column("create_time", FieldFill.INSERT))    //基于数据库字段填充
            .addTableFills(new Property("updateTime", FieldFill.INSERT_UPDATE))    //基于模型属性填充
            .build());
        generator.global(globalConfig().build());
        generator.execute();
    }

    /**
     * 逻辑删除字段设置
     * result: 新增@TableLogic注解
     * 忽略字段设置
     * result: 不生成
     */
    @Test
    public void testLogicDeleteAndIgnoreColumn() {
        AutoGenerator generator = new AutoGenerator(DATA_SOURCE_CONFIG);
        generator.strategy(strategyConfig().entityBuilder()
            .logicDeleteColumnName("deleted") // 基于数据库字段
            .logicDeletePropertyName("deleteFlag")// 基于模型属性
            .addIgnoreColumns("age") // 基于数据库字段
            .build());
        generator.global(globalConfig().build());
        generator.execute();
    }

    /**
     * 重命名模板生成的文件名称
     * result: TSimple -> TSimpleEntity
     */
    @Test
    public void testCustomTemplateName() {
        AutoGenerator generator = new AutoGenerator(DATA_SOURCE_CONFIG);
        generator.strategy(strategyConfig()
            .entityBuilder().formatFileName("%sEntity")
            .mapperBuilder().formatMapperFileName("%sDao").formatXmlFileName("%sXml")
            .controllerBuilder().formatFileName("%sAction")
            .serviceBuilder().formatServiceFileName("%sService").formatServiceImplFileName("%sServiceImp")
            .build());
        generator.global(globalConfig().build());
        generator.execute();
    }

    /**
     * 更改模板生成的文件路径
     *
     * @see OutputFile
     */
    @Test
    public void testCustomTemplatePath() {
        // 设置自定义路径
        Map<OutputFile, String> pathInfo = new HashMap<>();
        pathInfo.put(OutputFile.xml, "D://");
        pathInfo.put(OutputFile.entity, "D://entity//");
        AutoGenerator generator = new AutoGenerator(DATA_SOURCE_CONFIG);
        generator.strategy(strategyConfig().build());
        generator.packageInfo(packageConfig().pathInfo(pathInfo).build());
        generator.global(globalConfig().build());
        generator.execute();
    }

    /**
     * 替换模板
     */
    @Test
    public void testCustomTemplate() {
        AutoGenerator generator = new AutoGenerator(DATA_SOURCE_CONFIG);
        generator.strategy(strategyConfig().build());
        generator.template(templateConfig()
            .entity("/templates/entity1.java")
            .build());
        generator.global(globalConfig().build());
        generator.execute();
    }

    /**
     * 注入自定义属性
     */
    @Test
    public void testCustomMap() {
        // 设置自定义属性
        Map<String, Object> map = new HashMap<>();
        map.put("abc", 123);
        AutoGenerator generator = new AutoGenerator(DATA_SOURCE_CONFIG);
        generator.strategy(strategyConfig().build());
        generator.template(templateConfig()
            .entity("/templates/entity1.java")
            .build());
        generator.injection(injectionConfig().customMap(map).build());
        generator.global(globalConfig().build());
        generator.execute();
    }

    /**
     * 自定义模板（Map）
     * key为文件名称，value为文件路径
     */
    @Test
    public void testCustomFileByMap() {
        // 设置自定义输出文件
        Map<String, String> customFile = new HashMap<>();
        customFile.put("DTO.java", "/templates/dto.java.vm");
        AutoGenerator generator = new AutoGenerator(DATA_SOURCE_CONFIG);
        generator.strategy(strategyConfig().build());
        generator.injection(injectionConfig().customFile(customFile).build());
        generator.global(globalConfig().build());
        generator.execute();
    }

    /**
     * 自定义模板（单个文件）
     */
    @Test
    public void testCustomFileBySingle() {
        // 设置自定义输出文件
        CustomFile customFile = new CustomFile.Builder().fileName("DTO.java").templatePath("/templates/dto.java.vm").packageName("dto").build();
        AutoGenerator generator = new AutoGenerator(DATA_SOURCE_CONFIG);
        generator.strategy(strategyConfig().build());
        generator.injection(injectionConfig().customFile(customFile).build());
        generator.global(globalConfig().build());
        generator.execute();
    }

    /**
     * 自定义模板（列表）
     */
    @Test
    public void testCustomFileByList() {
        // 设置自定义输出文件
        List<CustomFile> customFiles = new ArrayList<>();
        customFiles.add(new CustomFile.Builder().fileName("DTO.java").templatePath("/templates/dto.java.vm").packageName("dto").build());
        customFiles.add(new CustomFile.Builder().fileName("VO.java").templatePath("/templates/vo.java.vm").packageName("vo").build());
        AutoGenerator generator = new AutoGenerator(DATA_SOURCE_CONFIG);
        generator.strategy(strategyConfig().build());
        generator.injection(injectionConfig().customFile(customFiles).build());
        generator.global(globalConfig().build());
        generator.execute();
    }

    /**
     * 自定义模板（列表）
     */
    @Test
    public void testCustomFileByLambda() {
        // 设置自定义属性
        Map<String, Object> map = new HashMap<>();
        map.put("abc", 118);
        // 设置自定义输出文件
        AutoGenerator generator = new AutoGenerator(DATA_SOURCE_CONFIG);
        generator.strategy(strategyConfig().build());
        generator.injection(injectionConfig()
            .customMap(map)
            .customFile(file ->
                file.fileName("DTO.java")
                    .templatePath("/templates/dto.java.vm")
                    .filePath("D://"))
            .customFile(file ->
                file.fileName("VO.java")
                    .templatePath("/templates/vo.java.vm")
                    .enableFileOverride()
                    .filePath("D://"))
            .build());
        generator.global(globalConfig().build());
        generator.execute();
    }
}
