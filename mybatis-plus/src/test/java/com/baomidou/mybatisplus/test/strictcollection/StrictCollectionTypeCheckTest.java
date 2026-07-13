package com.baomidou.mybatisplus.test.strictcollection;

import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.core.MybatisSqlSessionFactoryBuilder;
import com.baomidou.mybatisplus.core.MybatisXMLMapperBuilder;
import com.baomidou.mybatisplus.core.toolkit.ExceptionUtils;
import org.apache.ibatis.builder.BuilderException;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.logging.slf4j.Slf4jImpl;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.transaction.managed.ManagedTransactionFactory;
import org.h2.Driver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Unit tests for {@link MybatisConfiguration#setStrictResultMapCollectionTypeCheck(boolean)}.
 *
 * <h2>Background</h2>
 * <p>
 * In MyBatis, when mapping a one-to-many relationship via {@code <collection>} in XML ResultMap,
 * the {@code ofType} attribute specifies the element type of the collection. For example:
 * </p>
 * <pre>{@code
 * // Entity class:
 * public class Group {
 *     private List<User> users;  // expects User elements
 * }
 *
 * <!-- XML ResultMap: -->
 * <collection property="users" ofType="com.example.User">
 *     ...
 * </collection>
 * }</pre>
 * <p>
 * However, due to Java type erasure, MyBatis does <strong>not</strong> validate whether the
 * {@code ofType} type matches the generic element type declared in the entity class. If a
 * developer accidentally writes {@code ofType="WrongType"}, the error is silently accepted
 * at parse time and only surfaces as a {@link ClassCastException} at runtime — often far
 * from the root cause, making it very hard to debug.
 * </p>
 *
 * <h2>What This Feature Solves</h2>
 * <p>
 * The {@code strictResultMapCollectionTypeCheck} option, when enabled, performs type validation
 * at ResultMap registration time (i.e. when {@code addResultMap()} is called during XML parsing).
 * It compares the {@code ofType} declared type against the entity field's generic element type
 * and throws a {@link BuilderException} immediately if they are incompatible — turning a
 * hard-to-debug runtime error into a clear, early fail-fast error message.
 * </p>
 *
 * <h2>Test Scenarios</h2>
 * <p>
 * This class contains three test cases that together verify the feature works correctly
 * and does not break existing behavior:
 * </p>
 * <ol>
 *   <li>{@link #correctOfType_passesWhenStrictCheckEnabled()} — correct mapping works fine</li>
 *   <li>{@link #wrongOfType_throwsBuilderExceptionWhenStrictCheckEnabled()} — wrong mapping is rejected</li>
 *   <li>{@link #wrongOfType_passesWhenStrictCheckDisabled()} — backward compatibility preserved</li>
 * </ol>
 *
 * @author nieqiurong
 * @since 3.5.18
 */
public class StrictCollectionTypeCheckTest {

    private DataSource dataSource;
    private JdbcTemplate jdbcTemplate;

    /**
     * Sets up an H2 in-memory database with test data before each test.
     * <p>
     * Creates two tables:
     * </p>
     * <ul>
     *   <li>{@code t_strict_group} — 2 groups: "Developers" and "QA Team"</li>
     *   <li>{@code t_strict_user} — 3 users: Alice & Bob in group 1, Charlie in group 2</li>
     * </ul>
     * <p>
     * Tables are dropped and recreated before each test to ensure isolation.
     * </p>
     */
    @BeforeEach
    void setUp() {
        dataSource = createDataSource();
        jdbcTemplate = new JdbcTemplate(dataSource);
        initSchema();
    }

    /**
     * Test 1: Correct {@code ofType} with strict checking enabled — should work normally.
     * <p>
     * <b>Problem addressed:</b> Ensures that enabling strict checking does not break
     * existing, correctly-configured ResultMap mappings. Developers should be able to
     * turn on the feature without worrying about false positives.
     * </p>
     * <p>
     * <b>Setup:</b>
     * </p>
     * <ul>
     *   <li>Entity: {@code StrictGroupEntity} with field {@code List<StrictUserEntity> users}</li>
     *   <li>XML: {@code <collection property="users" ofType="StrictUserEntity">}</li>
     *   <li>Strict checking: <strong>ON</strong></li>
     * </ul>
     * <p>
     * <b>Expected:</b> XML parsing succeeds, query returns 2 groups with correct user data.
     * </p>
     */
    @Test
    void correctOfType_passesWhenStrictCheckEnabled() {
        MybatisConfiguration configuration = createConfiguration(true);
        parseMapperXml(configuration, "com/baomidou/mybatisplus/test/strictcollection/StrictCollectionMapper.xml");
        configuration.addMapper(StrictCollectionMapper.class);

        SqlSessionFactory factory = new MybatisSqlSessionFactoryBuilder().build(configuration);
        try (SqlSession session = factory.openSession()) {
            StrictCollectionMapper mapper = session.getMapper(StrictCollectionMapper.class);
            List<StrictGroupEntity> groups = mapper.selectAllWithUsers();

            // Verify: 2 groups returned
            assertThat(groups).hasSize(2);
            StrictGroupEntity firstGroup = groups.get(0);
            // Verify: first group is "Developers"
            assertThat(firstGroup.getGroupName()).isEqualTo("Developers");
            // Verify: first group has 2 users (Alice, Bob)
            assertThat(firstGroup.getUsers()).hasSize(2);
            // Verify: users are instances of StrictUserEntity (the correct type)
            assertThat(firstGroup.getUsers().get(0)).isInstanceOf(StrictUserEntity.class);
            assertThat(firstGroup.getUsers().get(0).getUserName()).isEqualTo("Alice");
        }
    }

    /**
     * Test 2: Wrong {@code ofType} with strict checking enabled — should fail fast.
     * <p>
     * <b>Problem addressed:</b> This is the core scenario the feature is designed to catch.
     * Without strict checking, a developer who accidentally writes
     * {@code ofType="StrictWrongUserEntity"} instead of {@code ofType="StrictUserEntity"}
     * would get no error at startup — the mismatch would only surface later as a
     * confusing {@link ClassCastException} at runtime.
     * </p>
     * <p>
     * <b>Setup:</b>
     * </p>
     * <ul>
     *   <li>Entity: {@code StrictGroupEntity} with field {@code List<StrictUserEntity> users}</li>
     *   <li>XML: {@code <collection property="users" ofType="StrictWrongUserEntity">} (WRONG!)</li>
     *   <li>Strict checking: <strong>ON</strong></li>
     * </ul>
     * <p>
     * <b>Expected:</b> A {@link BuilderException} is thrown at XML parse time (not at query time),
     * with an error message that clearly states:
     * </p>
     * <ul>
     *   <li>Which collection property has the mismatch ({@code users})</li>
     *   <li>What type was expected ({@code StrictUserEntity})</li>
     *   <li>What type was actually declared ({@code StrictWrongUserEntity})</li>
     * </ul>
     */
    @Test
    void wrongOfType_throwsBuilderExceptionWhenStrictCheckEnabled() {
        MybatisConfiguration configuration = createConfiguration(true);

        // Parsing the wrong XML must throw BuilderException immediately
        assertThatThrownBy(() ->
            parseMapperXml(configuration, "com/baomidou/mybatisplus/test/strictcollection/StrictCollectionWrongMapper.xml")
        ).isInstanceOf(BuilderException.class)
            // Error message must mention the collection property name
            .hasMessageContaining("Collection property 'users'")
            // Error message must mention the expected type (from entity field generic)
            .hasMessageContaining("StrictUserEntity")
            // Error message must mention the wrong type declared in XML
            .hasMessageContaining("StrictWrongUserEntity");
    }

    /**
     * Test 3: Wrong {@code ofType} with strict checking disabled — should be silently accepted.
     * <p>
     * <b>Problem addressed:</b> Ensures backward compatibility. When strict checking is off
     * (the default), the framework behaves exactly as before — no new errors are introduced.
     * Existing projects that upgrade to this version will not break even if they have
     * type-mismatched ResultMap mappings.
     * </p>
     * <p>
     * <b>Setup:</b>
     * </p>
     * <ul>
     *   <li>Entity: {@code StrictGroupEntity} with field {@code List<StrictUserEntity> users}</li>
     *   <li>XML: {@code <collection property="users" ofType="StrictWrongUserEntity">} (WRONG!)</li>
     *   <li>Strict checking: <strong>OFF</strong> (default)</li>
     * </ul>
     * <p>
     * <b>Expected:</b> XML parsing succeeds, query runs without error. Due to Java type erasure,
     * {@code StrictWrongUserEntity} objects are silently inserted into the {@code List} field
     * — this is the pre-existing behavior that the strict checking feature is designed to
     * make opt-in detectable.
     * </p>
     */
    @Test
    void wrongOfType_passesWhenStrictCheckDisabled() {
        MybatisConfiguration configuration = createConfiguration(false);
        parseMapperXml(configuration, "com/baomidou/mybatisplus/test/strictcollection/StrictCollectionWrongMapper.xml");
        configuration.addMapper(StrictCollectionWrongMapper.class);

        SqlSessionFactory factory = new MybatisSqlSessionFactoryBuilder().build(configuration);
        try (SqlSession session = factory.openSession()) {
            StrictCollectionWrongMapper mapper = session.getMapper(StrictCollectionWrongMapper.class);
            List<StrictGroupEntity> groups = mapper.selectAllWithWrongUsers();

            // Verify: query still works (backward compatible)
            assertThat(groups).hasSize(2);
            // Due to Java erasure, StrictWrongUserEntity objects are silently inserted
            // into the List<StrictUserEntity> field without any error.
            // This is exactly the type-safety gap that strictResultMapCollectionTypeCheck
            // is designed to catch when enabled.
            assertThat(groups.get(0).getUsers()).hasSize(2);
        }
    }

    // ------------------------------------------------------------------
    // Helper methods
    // ------------------------------------------------------------------

    /**
     * Creates a {@link MybatisConfiguration} with the specified strict checking mode.
     *
     * @param strictCheck {@code true} to enable strict collection type checking,
     *                    {@code false} to disable (default behavior)
     */
    private MybatisConfiguration createConfiguration(boolean strictCheck) {
        Environment environment = new Environment("test", new ManagedTransactionFactory(), dataSource);
        MybatisConfiguration configuration = new MybatisConfiguration(environment);
        configuration.setStrictResultMapCollectionTypeCheck(strictCheck);
        configuration.setLogImpl(Slf4jImpl.class);
        configuration.setMapUnderscoreToCamelCase(true);
        return configuration;
    }

    /**
     * Parses a mapper XML file and registers its ResultMaps in the configuration.
     *
     * @param configuration the MybatisConfiguration to parse into
     * @param resource      the classpath resource path of the XML file
     */
    private void parseMapperXml(MybatisConfiguration configuration, String resource) {
        try {
            InputStream inputStream = Resources.getResourceAsStream(resource);
            MybatisXMLMapperBuilder xmlMapperBuilder = new MybatisXMLMapperBuilder(
                inputStream, configuration, resource, configuration.getSqlFragments());
            xmlMapperBuilder.parse();
        } catch (IOException e) {
            throw ExceptionUtils.mpe(e);
        }
    }

    /**
     * Creates an H2 in-memory data source for testing.
     * <p>
     * Uses {@code DB_CLOSE_DELAY=-1} to keep the database alive across connections
     * within the same test, so that {@code @BeforeEach} setup and the test itself
     * can share the same data.
     * </p>
     */
    private DataSource createDataSource() {
        SimpleDriverDataSource ds = new SimpleDriverDataSource();
        ds.setDriver(new Driver());
        ds.setUrl("jdbc:h2:mem:strict_collection_test;MODE=mysql;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE");
        ds.setUsername("sa");
        ds.setPassword("");
        return ds;
    }

    /**
     * Initializes the database schema and test data.
     * <p>
     * Tables are dropped first to ensure a clean state between tests (the H2 in-memory
     * database is shared via {@code DB_CLOSE_DELAY=-1}).
     * </p>
     * <p>
     * Test data:
     * </p>
     * <pre>
     * t_strict_group:
     *   id=1  group_name=Developers  description=Dev Team
     *   id=2  group_name=QA Team     description=QA Team
     *
     * t_strict_user:
     *   id=1  group_id=1  user_name=Alice    email=alice@example.com    age=28
     *   id=2  group_id=1  user_name=Bob      email=bob@example.com      age=32
     *   id=3  group_id=2  user_name=Charlie  email=charlie@example.com  age=25
     * </pre>
     */
    private void initSchema() {
        jdbcTemplate.execute("DROP TABLE IF EXISTS t_strict_user");
        jdbcTemplate.execute("DROP TABLE IF EXISTS t_strict_group");
        jdbcTemplate.execute("CREATE TABLE t_strict_group (" +
            "id BIGINT AUTO_INCREMENT PRIMARY KEY, " +
            "group_name VARCHAR(100) NOT NULL, " +
            "description VARCHAR(255))");
        jdbcTemplate.execute("CREATE TABLE t_strict_user (" +
            "id BIGINT AUTO_INCREMENT PRIMARY KEY, " +
            "group_id BIGINT NOT NULL, " +
            "user_name VARCHAR(100) NOT NULL, " +
            "email VARCHAR(100), " +
            "age INT)");
        jdbcTemplate.execute("INSERT INTO t_strict_group (group_name, description) VALUES " +
            "('Developers', 'Dev Team'), ('QA Team', 'QA Team')");
        jdbcTemplate.execute("INSERT INTO t_strict_user (group_id, user_name, email, age) VALUES " +
            "(1, 'Alice', 'alice@example.com', 28), " +
            "(1, 'Bob', 'bob@example.com', 32), " +
            "(2, 'Charlie', 'charlie@example.com', 25)");
    }
}
