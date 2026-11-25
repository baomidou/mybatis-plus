package com.baomidou.mybatisplus.generator.index;

import com.baomidou.mybatisplus.generator.AutoGenerator;
import com.baomidou.mybatisplus.generator.IGenerateMapperMethodHandler;
import com.baomidou.mybatisplus.generator.config.DataSourceConfig;
import com.baomidou.mybatisplus.generator.config.GlobalConfig;
import com.baomidou.mybatisplus.generator.config.PackageConfig;
import com.baomidou.mybatisplus.generator.config.StrategyConfig;
import com.baomidou.mybatisplus.generator.engine.AbstractTemplateEngine;
import com.baomidou.mybatisplus.generator.engine.BeetlTemplateEngine;
import com.baomidou.mybatisplus.generator.engine.EnjoyTemplateEngine;
import com.baomidou.mybatisplus.generator.engine.FreemarkerTemplateEngine;
import com.baomidou.mybatisplus.generator.engine.VelocityTemplateEngine;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;

import static org.assertj.core.api.Assertions.assertThat;

@Tag("integration")
class DefaultGenerateMapperMethodHandlerTest {

    @TempDir
    Path output;

    @ParameterizedTest(name = "{0}, lambda={1}")
    @MethodSource("templateEngines")
    void compilesCompositeIndexMethodsAfterSkippingLogicDeleteColumn(String engine, boolean lambda,
            AbstractTemplateEngine templateEngine) throws Exception {
        String url = "jdbc:h2:mem:logic_delete_" + engine + "_" + lambda + ";DATABASE_TO_LOWER=TRUE";
        try (Connection connection = DriverManager.getConnection(url, "sa", "");
                Statement statement = connection.createStatement()) {
            statement.execute("CREATE TABLE zz_index_contact(id INT PRIMARY KEY, name VARCHAR(40), deleted BIGINT)");
            statement
                    .execute("CREATE UNIQUE INDEX uq_zz_index_contact_name_deleted ON zz_index_contact(name, deleted)");
            IGenerateMapperMethodHandler handler = lambda
                    ? new DefaultGenerateMapperLambdaMethodHandler(false)
                    : new DefaultGenerateMapperMethodHandler(false);
            StrategyConfig.Builder strategy = new StrategyConfig.Builder().addInclude("zz_index_contact");
            strategy.entityBuilder().logicDeleteColumnName("deleted");
            if (!lambda && "beetl".equals(engine)) {
                strategy.entityBuilder().enableColumnConstant();
            }
            strategy.mapperBuilder().generateMapperMethodHandler(handler);
            strategy.serviceBuilder().disable();
            strategy.controllerBuilder().disable();
            AutoGenerator generator = new AutoGenerator(
                    new DataSourceConfig.Builder(url, "sa", "").schema("public").build());
            generator.global(new GlobalConfig.Builder().disableOpenDir().outputDir(output.toString()).build());
            generator.packageInfo(new PackageConfig.Builder().parent("example").build());
            generator.strategy(strategy.build());
            generator.execute(templateEngine);
        }

        compileGeneratedJava();
        String mapper = Files.readString(output.resolve("example/mapper/ZzIndexContactMapper.java"));
        assertThat(mapper)
                .contains(
                        "List<ZzIndexContact> selectByName(String name)",
                        "int updateByName(ZzIndexContact entity, String name)", "int deleteByName(String name)")
                .doesNotContain("selectByNameAnd", "String name, )", ".);");
    }

    private void compileGeneratedJava() throws IOException {
        List<Path> sources;
        try (Stream<Path> paths = Files.walk(output)) {
            sources = paths.filter(path -> path.toString().endsWith(".java")).collect(Collectors.toList());
        }
        assertThat(sources).hasSize(2);
        Path classes = Files.createDirectories(output.resolve("classes"));
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        assertThat(compiler).as("run the generator regression with a JDK").isNotNull();
        DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<>();
        try (StandardJavaFileManager files = compiler.getStandardFileManager(diagnostics, null, null)) {
            boolean compiled = compiler.getTask(
                    null, files, diagnostics,
                    List.of(
                            "--release", "8", "-proc:none", "-classpath", System.getProperty("java.class.path"), "-d",
                            classes.toString()),
                    null, files.getJavaFileObjectsFromPaths(sources)).call();
            assertThat(compiled).as("generated entity and mapper must compile: %s", diagnostics.getDiagnostics())
                    .isTrue();
        }
    }

    static Stream<Arguments> templateEngines() {
        return Stream.of(false, true).flatMap(
                lambda -> Stream.of(
                        Arguments.of("velocity", lambda, new VelocityTemplateEngine()),
                        Arguments.of("freemarker", lambda, new FreemarkerTemplateEngine()),
                        Arguments.of("beetl", lambda, new BeetlTemplateEngine()),
                        Arguments.of("enjoy", lambda, new EnjoyTemplateEngine())));
    }
}
