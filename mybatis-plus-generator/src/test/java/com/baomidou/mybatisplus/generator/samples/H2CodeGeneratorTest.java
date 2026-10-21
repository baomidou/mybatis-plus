package com.baomidou.mybatisplus.generator.samples;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.generator.AutoGenerator;
import com.baomidou.mybatisplus.generator.config.DataSourceConfig;
import com.baomidou.mybatisplus.generator.config.GlobalConfig;
import com.baomidou.mybatisplus.generator.config.OutputFile;
import com.baomidou.mybatisplus.generator.config.PackageConfig;
import com.baomidou.mybatisplus.generator.config.StrategyConfig;
import com.baomidou.mybatisplus.generator.config.builder.CustomFile;
import com.baomidou.mybatisplus.generator.config.rules.DateType;
import com.baomidou.mybatisplus.generator.config.rules.DbColumnType;
import com.baomidou.mybatisplus.generator.config.rules.IColumnType;
import com.baomidou.mybatisplus.generator.engine.AbstractTemplateEngine;
import com.baomidou.mybatisplus.generator.engine.BeetlTemplateEngine;
import com.baomidou.mybatisplus.generator.engine.EnjoyTemplateEngine;
import com.baomidou.mybatisplus.generator.engine.FreemarkerTemplateEngine;
import com.baomidou.mybatisplus.generator.engine.VelocityTemplateEngine;
import com.baomidou.mybatisplus.generator.fill.Column;
import com.baomidou.mybatisplus.generator.fill.Property;
import com.baomidou.mybatisplus.generator.query.DefaultQuery;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * H2 代码生成。<br>
 * 每个用例把产物写进自己独享的 {@link TempDir}：GlobalConfig 默认输出目录是 D:// 或 /tmp， 既依赖盘符，又会让后跑的用例被 isCreate() 静默跳过，断言分不清是谁生成的。<br>
 * 四个模板引擎的用例额外和仓库里已提交的产物目录逐文件比对，模板一改错立刻红， 也避免把生成结果覆盖回工作区（这批文件同时是参与编译的测试源文件）。
 *
 * @author hubin, lanjerry
 * @since 3.5.3
 */
public class H2CodeGeneratorTest extends BaseGeneratorSample {

    private static final Logger LOGGER = LoggerFactory.getLogger(H2CodeGeneratorTest.class);

    /**
     * 已提交的模板引擎产物目录，键为用例里的引擎名。
     */
    private static final Path COMMITTED_JAVA_OUTPUT = Path.of("src", "test", "java", "com", "baomidou", "demo");

    private static final Path COMMITTED_KOTLIN_OUTPUT = Path.of("src", "test", "kotlin", "com", "baomidou");

    private static final Pattern WHITESPACE = Pattern.compile("\\s+");

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

    private static final String H2URL = "jdbc:h2:mem:test-h2;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE;CASE_INSENSITIVE_IDENTIFIERS=TRUE;MODE=MYSQL;DATABASE_TO_LOWER=TRUE";

    /**
     * 数据源配置
     */
    private static final DataSourceConfig DATA_SOURCE_CONFIG = new DataSourceConfig.Builder(H2URL, "sa", "")
            // 设置SQL查询方式，默认的是元数据查询方式
            .databaseQueryClass(DefaultQuery.class).build();

    @TempDir
    private Path tempDir;

    /**
     * 输出到本用例私有目录，并禁止生成完自动打开文件管理器。
     */
    private GlobalConfig.Builder tempGlobalConfig() {
        return globalConfig().disableOpenDir().outputDir(tempDir.toString());
    }

    private List<Path> generatedFilesNamed(String fileName) throws IOException {
        try (Stream<Path> paths = Files.walk(tempDir)) {
            return paths.filter(Files::isRegularFile).filter(file -> fileName.equals(file.getFileName().toString()))
                    .collect(Collectors.toList());
        }
    }

    private Path assertGenerated(String fileName) throws IOException {
        List<Path> files = generatedFilesNamed(fileName);
        assertThat(files).as("expected generated file " + fileName).isNotEmpty();
        return files.get(0);
    }

    private void assertNotGenerated(String fileName) throws IOException {
        assertThat(generatedFilesNamed(fileName)).isEmpty();
    }

    private String contentOf(String fileName) throws IOException {
        return Files.readString(assertGenerated(fileName), StandardCharsets.UTF_8);
    }

    /**
     * 简单生成
     */
    @Test
    public void testSimple() throws IOException {
        AutoGenerator generator = new AutoGenerator(DATA_SOURCE_CONFIG);
        generator.strategy(strategyConfig().build());
        generator.global(tempGlobalConfig().build());
        generator.execute();

        // 未配置 addTablePrefix，实体名保留表名原名
        assertThat(contentOf("TSimple.java")).contains("@TableName(\"t_simple\")");
        assertGenerated("TSimpleMapper.java");
        assertGenerated("TSimpleMapper.xml");
        assertGenerated("ITSimpleService.java");
        assertGenerated("TSimpleServiceImpl.java");
        assertGenerated("TSimpleController.java");
    }

    static Stream<Arguments> testByTemplateEngine() {
        return Stream.of(
                Arguments.arguments("velocity", new VelocityTemplateEngine()),
                Arguments.arguments("freemarker", new FreemarkerTemplateEngine()),
                Arguments.arguments("beetl", new BeetlTemplateEngine()),
                Arguments.arguments("enjoy", new EnjoyTemplateEngine()));
    }

    @MethodSource
    @ParameterizedTest
    public void testByTemplateEngine(String engine, AbstractTemplateEngine templateEngine) throws IOException {
        Path javaOutput = tempDir.resolve("java");
        LOGGER.info("当前执行引擎:{}, 输出目录:{}", engine, javaOutput);
        AutoGenerator generator = new AutoGenerator(DATA_SOURCE_CONFIG);
        generator.strategy(
                strategyConfig().addTablePrefix("t_").entityBuilder().enableFileOverride().mapperBuilder()
                        .enableFileOverride().controllerBuilder().enableFileOverride().serviceBuilder()
                        .enableFileOverride().build());
        generator.packageInfo(new PackageConfig.Builder().moduleName("demo" + "." + engine).build());
        generator.global(tempGlobalConfig().commentDate(() -> "2025-03-27").outputDir(javaOutput.toString()).build());
        generator.execute(templateEngine);

        // -- 生成kotlin代码
        LOGGER.info("当前执行引擎:{}, 执行生成Kotlin代码.", engine);
        Path kotlinOutput = tempDir.resolve("kotlin");
        generator = new AutoGenerator(DATA_SOURCE_CONFIG);

        generator.strategy(
                strategyConfig().addTablePrefix("t_").entityBuilder().enableFileOverride()
                        .importPackageFunction(ArrayList::new).mapperBuilder().enableFileOverride()
                        .enableBaseResultMap().importPackageFunction(ArrayList::new).controllerBuilder()
                        .enableFileOverride().serviceBuilder().enableFileOverride().build());
        generator.packageInfo(new PackageConfig.Builder().moduleName(engine).build());
        generator.global(
                tempGlobalConfig().enableKotlin().commentDate(() -> "2025-03-27").outputDir(kotlinOutput.toString())
                        .build());
        generator.execute(templateEngine);

        assertSameTree(COMMITTED_JAVA_OUTPUT.resolve(engine), javaOutput.resolve("com/baomidou/demo").resolve(engine));
        assertSameTree(COMMITTED_KOTLIN_OUTPUT.resolve(engine), kotlinOutput.resolve("com/baomidou").resolve(engine));
    }

    /**
     * 逐个文件比对生成结果与仓库里已提交的产物：文件清单要一致，内容按 canonical 归一后一致。
     */
    private static void assertSameTree(Path committedRoot, Path generatedRoot) throws IOException {
        assertThat(committedRoot).as("committed output of " + committedRoot).isDirectory();
        assertThat(generatedRoot).as("generated output of " + generatedRoot).isDirectory();

        List<String> committedFiles = relativeFileNames(committedRoot);
        assertThat(relativeFileNames(generatedRoot)).isEqualTo(committedFiles);
        for (String relative : committedFiles) {
            String committed = canonical(committedRoot.resolve(relative));
            String generated = canonical(generatedRoot.resolve(relative));
            assertThat(generated).as(relative).isEqualTo(committed);
        }
    }

    /**
     * 已提交的产物同时是 src/test/java 下的测试源文件，会被仓库的 Eclipse 格式化器重排（import 顺序、表达式换行）， kotlin 产物则不受影响。归一方式：import
     * 单独成块并排序，其余内容压掉换行差异，只留下真正的文本差异。
     */
    private static String canonical(Path file) throws IOException {
        List<String> imports = new ArrayList<>();
        List<String> lines = new ArrayList<>();
        for (String line : Files.readString(file, StandardCharsets.UTF_8).split("\\R")) {
            String text = WHITESPACE.matcher(line).replaceAll(" ").trim();
            if (text.isEmpty()) {
                continue;
            }
            if (text.startsWith("import ")) {
                imports.add(text);
            } else {
                lines.add(text);
            }
        }
        imports.sort(String::compareTo);
        StringBuilder canonical = new StringBuilder();
        imports.forEach(importLine -> canonical.append(importLine).append('\n'));
        return canonical.append(String.join(" ", lines)).toString();
    }

    private static List<String> relativeFileNames(Path root) throws IOException {
        try (Stream<Path> paths = Files.walk(root)) {
            return paths.filter(Files::isRegularFile).map(path -> root.relativize(path).toString()).sorted()
                    .collect(Collectors.toList());
        }
    }

    /**
     * 过滤表前缀（后缀同理，支持多个） <br>
     * result: t_simple -> simple
     */
    @Test
    public void testTablePrefix() throws IOException {
        AutoGenerator generator = new AutoGenerator(DATA_SOURCE_CONFIG);
        generator.strategy(strategyConfig().addTablePrefix("t_", "c_").build());
        generator.global(tempGlobalConfig().build());
        generator.execute();

        assertGenerated("Simple.java");
        assertNotGenerated("TSimple.java");
    }

    /**
     * 过滤字段后缀（前缀同理，支持多个） <br>
     * result: deleted_flag -> deleted
     */
    @Test
    public void testFieldSuffix() throws IOException {
        AutoGenerator generator = new AutoGenerator(DATA_SOURCE_CONFIG);
        generator.strategy(strategyConfig().addFieldSuffix("_flag").build());
        generator.global(tempGlobalConfig().build());
        generator.execute();

        assertThat(contentOf("TSimple.java")).contains("private Byte delete;").doesNotContain("deleteFlag");
    }

    /**
     * 乐观锁字段设置 <br>
     * result: 新增@Version注解 <br>
     * 填充字段设置 <br>
     * result: 新增@TableField(value = "xxx", fill = FieldFill.xxx)注解
     */
    @Test
    public void testVersionAndFill() throws IOException {
        AutoGenerator generator = new AutoGenerator(DATA_SOURCE_CONFIG);
        generator.strategy(
                strategyConfig().entityBuilder().versionColumnName("version") // 基于数据库字段
                        .versionPropertyName("version")// 基于模型属性
                        .addTableFills(new Column("create_time", FieldFill.INSERT)) // 基于数据库字段填充
                        .addTableFills(new Property("updateTime", FieldFill.INSERT_UPDATE)) // 基于模型属性填充
                        .build());
        generator.global(tempGlobalConfig().build());
        generator.execute();

        assertThat(contentOf("TSimple.java")).contains("@Version").contains("@TableField(fill = FieldFill.INSERT)")
                .contains("@TableField(fill = FieldFill.INSERT_UPDATE)");
    }

    /**
     * 逻辑删除字段设置 <br>
     * result: 新增@TableLogic注解 <br>
     * 忽略字段设置 <br>
     * result: 不生成
     */
    @Test
    public void testLogicDeleteAndIgnoreColumn() throws IOException {
        AutoGenerator generator = new AutoGenerator(DATA_SOURCE_CONFIG);
        generator.strategy(
                strategyConfig().entityBuilder().logicDeleteColumnName("deleted") // 基于数据库字段
                        .logicDeletePropertyName("deleteFlag")// 基于模型属性
                        .addIgnoreColumns("age") // 基于数据库字段
                        .build());
        generator.global(tempGlobalConfig().build());
        generator.execute();

        assertThat(contentOf("TSimple.java")).contains("@TableLogic").doesNotContain("private Integer age;");
    }

    /**
     * 重命名模板生成的文件名称 <br>
     * result: TSimple -> TSimpleEntity
     */
    @Test
    public void testCustomTemplateName() throws IOException {
        AutoGenerator generator = new AutoGenerator(DATA_SOURCE_CONFIG);
        generator.strategy(
                strategyConfig().entityBuilder().formatFileName("%sEntity").mapperBuilder()
                        .formatMapperFileName("%sDao").formatXmlFileName("%sXml").controllerBuilder()
                        .formatFileName("%sAction").serviceBuilder().formatServiceFileName("%sService")
                        .formatServiceImplFileName("%sServiceImp").build());
        generator.global(tempGlobalConfig().build());
        generator.execute();

        assertGenerated("TSimpleEntity.java");
        assertGenerated("TSimpleDao.java");
        assertGenerated("TSimpleXml.xml");
        assertGenerated("TSimpleAction.java");
        assertGenerated("TSimpleService.java");
        assertGenerated("TSimpleServiceImp.java");
    }

    /**
     * 更改模板生成的文件路径
     *
     * @see OutputFile
     */
    @Test
    public void testCustomTemplatePath() throws IOException {
        Path xmlDir = tempDir.resolve("customXml");
        Path entityDir = tempDir.resolve("customEntity");
        // 设置自定义路径
        Map<OutputFile, String> pathInfo = new HashMap<>();
        pathInfo.put(OutputFile.xml, xmlDir.toString());
        pathInfo.put(OutputFile.entity, entityDir.toString());
        AutoGenerator generator = new AutoGenerator(DATA_SOURCE_CONFIG);
        generator.strategy(strategyConfig().build());
        generator.packageInfo(packageConfig().pathInfo(pathInfo).build());
        generator.global(tempGlobalConfig().build());
        generator.execute();

        assertThat(xmlDir.resolve("TSimpleMapper.xml")).exists();
        assertThat(entityDir.resolve("TSimple.java")).exists();
    }

    /**
     * 替换模板
     */
    @Test
    public void testCustomTemplate() throws IOException {
        AutoGenerator generator = new AutoGenerator(DATA_SOURCE_CONFIG);
        generator.strategy(strategyConfig().entityBuilder().javaTemplate("/templates/entity1.java").build());
        generator.global(tempGlobalConfig().build());
        generator.execute();

        assertThat(contentOf("TSimple.java")).contains("自定义模板entity");
    }

    /**
     * 测试文件覆盖
     */
    @Test
    public void testFileOverride() throws IOException {
        AutoGenerator generator = new AutoGenerator(DATA_SOURCE_CONFIG);
        generator.strategy(
                strategyConfig()
                        // 实体文件覆盖
                        .entityBuilder().enableFileOverride()
                        // Mapper文件覆盖
                        .mapperBuilder().enableFileOverride()
                        // Service文件覆盖
                        .serviceBuilder().enableFileOverride()
                        // Controller文件覆盖
                        .controllerBuilder().enableFileOverride().build());
        generator.global(tempGlobalConfig().build());
        generator.execute();
        // 同名文件已存在时靠 enableFileOverride 覆盖，第二次执行同样要产出全部文件
        generator.execute();

        assertThat(contentOf("TSimple.java")).contains("@TableName(\"t_simple\")");
        assertGenerated("TSimpleController.java");
    }

    /**
     * 测试日期类型
     */
    @Test
    public void testDateType() throws IOException {
        AutoGenerator generator = new AutoGenerator(DATA_SOURCE_CONFIG);
        generator.strategy(strategyConfig().build());
        // 关闭其它模块生成只生成实体
        generator.strategy(
                strategyConfig().controllerBuilder().disable().serviceBuilder().disable().mapperBuilder().disable()
                        .build());
        // 日期类型强制设置为 Date 类型
        generator.global(tempGlobalConfig().dateType(DateType.ONLY_DATE).build());
        generator.execute();

        assertThat(contentOf("TSimple.java")).contains("private Date createTime;");
        assertNotGenerated("TSimpleMapper.java");
        assertNotGenerated("TSimpleController.java");
    }

    /**
     * 注入自定义属性
     */
    @Test
    public void testCustomMap() throws IOException {
        // 设置自定义属性
        Map<String, Object> map = new HashMap<>();
        map.put("abc", 123);
        AutoGenerator generator = new AutoGenerator(DATA_SOURCE_CONFIG);
        generator.strategy(strategyConfig().entityBuilder().javaTemplate("/templates/entity1.java").build());
        generator.injection(injectionConfig().customMap(map).build());
        generator.global(tempGlobalConfig().build());
        generator.execute();

        assertThat(contentOf("TSimple.java")).contains("自定义属性注入abc=123");
    }

    /**
     * 自定义模板（Map） <br>
     * key为文件名称，value为文件路径
     */
    @Test
    public void testCustomFileByMap() throws IOException {
        // 设置自定义输出文件
        Map<String, String> customFile = new HashMap<>();
        customFile.put("DTO.java", "/templates/dto.java.vm");
        AutoGenerator generator = new AutoGenerator(DATA_SOURCE_CONFIG);
        generator.strategy(strategyConfig().build());
        generator.injection(injectionConfig().customFile(customFile).build());
        generator.global(tempGlobalConfig().build());
        generator.execute();

        assertThat(tempDir.resolve("com/baomidou/TSimpleDTO.java")).exists();
        assertThat(Files.readString(tempDir.resolve("com/baomidou/TSimpleDTO.java"), StandardCharsets.UTF_8))
                .contains("测试自定义DTO模板");
    }

    /**
     * 自定义模板（单个文件）
     */
    @Test
    public void testCustomFileBySingle() throws IOException {
        // 设置自定义输出文件
        CustomFile customFile = new CustomFile.Builder().fileName("DTO.java").templatePath("templates/dto.java.vm")
                .packageName("dto").build();
        AutoGenerator generator = new AutoGenerator(DATA_SOURCE_CONFIG);
        generator.strategy(strategyConfig().build());
        generator.injection(injectionConfig().customFile(customFile).build());
        generator.global(tempGlobalConfig().build());
        generator.execute();

        assertThat(tempDir.resolve("com/baomidou/dto/TSimpleDTO.java")).exists();
    }

    /**
     * 自定义模板（列表）
     */
    @Test
    public void testCustomFileByList() throws IOException {
        // 设置自定义输出文件
        AutoGenerator generator = new AutoGenerator(DATA_SOURCE_CONFIG);
        generator.strategy(
                strategyConfig()
                        // 禁用Controller默认模板
                        .controllerBuilder().disable().build());
        generator.injection(injectionConfig().customFile(new ArrayList<>() {

            {
                add(
                        new CustomFile.Builder().fileName("DTO.java").templatePath("/templates/dto.java.vm")
                                .packageName("dto").build());
                add(
                        new CustomFile.Builder().fileName("VO.java").templatePath("/templates/vo.java.vm")
                                .packageName("vo").build());
                // 通过格式化函数添加文件最后缀
                add(
                        new CustomFile.Builder()
                                .formatNameFunction(tableInfo -> "Prefix" + tableInfo.getEntityName() + "Suffix")
                                .fileName("Controller.java").templatePath("/templates/controller.java.vm")
                                .packageName("controller").build());
            }
        }).build());
        generator.global(tempGlobalConfig().build());
        generator.execute();

        assertThat(contentOf("PrefixTSimpleSuffixController.java")).contains("@Controller")
                .contains("@RequestMapping(\"/tSimple\")");
        assertGenerated("TSimpleDTO.java");
        assertGenerated("TSimpleVO.java");
        assertNotGenerated("TSimpleController.java");
    }

    /**
     * 测试内置模板路径自定义输出
     */
    @Test
    public void testOutputFile() throws IOException {
        AutoGenerator generator = new AutoGenerator(DATA_SOURCE_CONFIG);
        GlobalConfig globalConfig = tempGlobalConfig().build();
        generator.global(globalConfig);
        generator.strategy(strategyConfig().outputFile(((filePath, outputFile) -> {
            File file = new File(filePath);
            if (outputFile == OutputFile.controller) {
                String outputDir = globalConfig.getOutputDir();
                return new File(outputDir + File.separator + file.getName());
            }
            return file;
        })).build());
        generator.execute();

        assertThat(tempDir.resolve("TSimpleController.java")).exists();
        assertThat(tempDir.resolve("com/baomidou/controller/TSimpleController.java")).doesNotExist();
        assertGenerated("TSimple.java");
    }

    /**
     * 测试开启生成实体时生成字段注解
     */
    @Test
    public void testEnableTableFieldAnnotation() throws IOException {
        AutoGenerator generator = new AutoGenerator(DATA_SOURCE_CONFIG);
        generator.strategy(strategyConfig().entityBuilder().enableTableFieldAnnotation().build());
        generator.global(tempGlobalConfig().build());
        generator.execute();

        assertThat(contentOf("TSimple.java")).contains("@TableField(\"name\")");
    }

    /**
     * 测试开正则匹配包含的表名
     */
    @Test
    public void testAddIncludeTables() throws IOException {
        AutoGenerator generator = new AutoGenerator(DATA_SOURCE_CONFIG);
        generator.strategy(new StrategyConfig.Builder().addInclude("^t_.*").build());
        generator.global(tempGlobalConfig().build());
        generator.execute();

        // init.sql 里 t_ 开头的表只有这两张
        assertGenerated("TSimple.java");
        assertGenerated("TTest.java");
    }

    /**
     * 测试开正则匹配排除的表名
     */
    @Test
    public void testAddExcludeTables() throws IOException {
        AutoGenerator generator = new AutoGenerator(DATA_SOURCE_CONFIG);
        generator.strategy(
                new StrategyConfig.Builder().addExclude(
                        // 排除 st 结尾的表
                        ".*st$",
                        // 排除非 t_ 开头的表
                        "^(?!t_).*").build());
        generator.global(tempGlobalConfig().build());
        generator.execute();

        assertGenerated("TSimple.java");
        assertNotGenerated("TTest.java");
    }

    /**
     * 测试开启Boolean类型字段移除is前缀
     */
    @Test
    public void testEnableRemoveIsPrefix() throws IOException {
        AutoGenerator generator = new AutoGenerator(new DataSourceConfig.Builder(H2URL, "sa", "")
                .typeConvertHandler((globalConfig, typeRegistry, metaInfo) -> {
                    IColumnType dbColumnType = typeRegistry.getColumnType(metaInfo);
                    if (dbColumnType == DbColumnType.BYTE) {
                        // 这里按照自己的要求转换为指定类型
                        return DbColumnType.BOOLEAN;
                    }
                    return dbColumnType;
                }).build());
        generator.strategy(strategyConfig().entityBuilder().enableRemoveIsPrefix().build());
        generator.global(tempGlobalConfig().build());
        generator.execute();

        assertThat(contentOf("TSimple.java")).contains("private Boolean ok;").doesNotContain("isOk");
    }

    /**
     * 自定义模板（列表）
     */
    @Test
    public void testCustomFileByLambda() throws IOException {
        Path dtoDir = tempDir.resolve("dtoFile");
        Path voDir = tempDir.resolve("voFile");
        // 设置自定义属性
        Map<String, Object> map = new HashMap<>();
        map.put("abc", 118);
        // 设置自定义输出文件
        AutoGenerator generator = new AutoGenerator(DATA_SOURCE_CONFIG);
        generator.strategy(strategyConfig().build());
        generator.injection(
                injectionConfig().customMap(map)
                        .customFile(
                                file -> file.fileName("DTO.java").templatePath("/templates/dto.java.vm")
                                        .filePath(dtoDir.toString()))
                        .customFile(
                                file -> file.fileName("VO.java").templatePath("/templates/vo.java.vm")
                                        .enableFileOverride().filePath(voDir.toString()))
                        .build());
        generator.global(tempGlobalConfig().build());
        generator.execute();

        assertThat(Files.readString(dtoDir.resolve("TSimpleDTO.java"), StandardCharsets.UTF_8)).contains("测试注入 118");
        assertThat(voDir.resolve("TSimpleVO.java")).exists();
    }
}
