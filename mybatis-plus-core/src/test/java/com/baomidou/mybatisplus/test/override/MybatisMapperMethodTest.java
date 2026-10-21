package com.baomidou.mybatisplus.test.override;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.exceptions.MybatisPlusException;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.core.override.MybatisMapperMethod;
import org.apache.ibatis.annotations.Flush;
import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.binding.BindingException;
import org.apache.ibatis.cursor.Cursor;
import org.apache.ibatis.executor.BatchResult;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ResultMap;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.mapping.SqlSource;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.apache.ibatis.session.SqlSession;
import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * {@link MybatisMapperMethod} 是 MP 从上游 {@code MapperMethod} 抄过来再改的：SELECT 多了 IPage 分支， 参数转换前会把 {@code @Param} 声明的别名写回
 * Wrapper。用例用 {@link Proxy} 造的 SqlSession 替身驱动真类， 盯的就是这张分派表：语句 id 取哪个、走 SqlSession 的哪个方法、行数怎么换算成声明返回类型。 真库执行由 h2
 * 那套集成测试负责。
 */
class MybatisMapperMethodTest {

    /**
     * 这些方法注册的语句结果类型故意写成 {@code void}：{@code insert*} 的返回值由 rowCount 换算，用不到结果映射； {@code selectHandlerWithoutResultType}
     * 则是专门留给 {@code executeWithResultHandler} 那条前置校验的靶子。
     */
    private static final Set<String> VOID_RESULT_TYPES = Set.of(
            "insertInt", "insertInteger", "insertLong", "insertBoolean", "insertVoid", "insertUnsupported",
            "selectHandlerWithoutResultType");

    private static final List<String> ROWS = List.of("a", "b");

    private static final Cursor<String> CURSOR = new Cursor<String>() {

        @Override
        public void close() {
            // 替身不持有资源
        }

        @Override
        public boolean isOpen() {
            return true;
        }

        @Override
        public boolean isConsumed() {
            return false;
        }

        @Override
        public int getCurrentIndex() {
            return -1;
        }

        @Override
        public Iterator<String> iterator() {
            return ROWS.iterator();
        }
    };

    @Test
    void selectManyKeepsTheListAndConvertsWhenTheDeclaredTypeDiffers() {
        RecordingSession session = new RecordingSession(configuration());
        session.canned.put("selectList", new ArrayList<>(ROWS));
        assertThat(execute("selectNames", session)).isEqualTo(ROWS);
        assertThat(session.last("selectList").args()).containsExactly(id("selectNames"), null);

        Object declared = execute("selectNameSet", session);
        assertThat(declared).isInstanceOf(Set.class);
        assertThat((Collection<?>) declared).extracting(Object::toString).containsExactlyElementsOf(ROWS);
        assertThat((String[]) execute("selectNameArray", session)).containsExactlyElementsOf(ROWS);

        session.canned.put("selectList", List.of(1, 2));
        assertThat((int[]) execute("selectCountArray", session)).containsExactly(1, 2);
    }

    @Test
    void selectOneReturnsTheRowAndOptionalWrapsBothNullAndValue() {
        RecordingSession session = new RecordingSession(configuration());
        session.canned.put("selectOne", "a");
        assertThat(execute("selectOneName", session)).isEqualTo("a");
        assertThat(session.last("selectOne").args()).containsExactly(id("selectOneName"), null);

        assertThat(execute("selectOptional", session)).isEqualTo(Optional.of("a"));

        session.canned.remove("selectOne");
        assertThat(execute("selectOptional", session)).isEqualTo(Optional.empty());
    }

    @Test
    void mapKeyedSelectUsesSelectMapWithItsDeclaredKey() {
        RecordingSession session = new RecordingSession(configuration());
        Map<String, String> rows = Map.of("1", "a");
        session.canned.put("selectMap", rows);
        assertThat(execute("selectNameByKey", session)).isEqualTo(rows);
        assertThat(session.last("selectMap").args()).containsExactly(id("selectNameByKey"), null, "id");
    }

    @Test
    void cursorSelectUsesSelectCursor() {
        RecordingSession session = new RecordingSession(configuration());
        session.canned.put("selectCursor", CURSOR);
        assertThat(execute("selectCursorRows", session)).isSameAs(CURSOR);
        assertThat(session.last("selectCursor").args()).containsExactly(id("selectCursorRows"), null);
    }

    @Test
    void rowBoundsComesOutOfTheArgumentsNotTheParameterMap() {
        RecordingSession session = new RecordingSession(configuration());
        session.canned.put("selectList", new ArrayList<>(ROWS));
        RowBounds rowBounds = new RowBounds(1, 2);
        assertThat(execute("selectPaged", session, rowBounds)).isEqualTo(ROWS);
        assertThat(session.last("selectList").args()).containsExactly(id("selectPaged"), null, rowBounds);
    }

    @Test
    void voidWithResultHandlerWithoutAResultTypeFails() {
        RecordingSession session = new RecordingSession(configuration());
        ResultHandler<String> handler = resultContext -> {
        };
        assertThatThrownBy(() -> execute("selectHandlerWithoutResultType", session, handler))
                .isInstanceOf(BindingException.class).hasMessageContaining("needs either a @ResultMap annotation");
    }

    @Test
    void voidWithResultHandlerAndRowBoundsUsesTheFourArgumentSelect() {
        RecordingSession session = new RecordingSession(configuration());
        ResultHandler<String> handler = resultContext -> {
        };
        RowBounds rowBounds = new RowBounds(0, 10);
        assertThat(execute("selectWithHandlerPaged", session, handler, rowBounds)).isNull();
        assertThat(session.last("select").args())
                .containsExactly(id("selectWithHandlerPaged"), null, rowBounds, handler);
    }

    @Test
    void voidWithResultHandlerUsesTheThreeArgumentSelect() {
        RecordingSession session = new RecordingSession(configuration());
        ResultHandler<String> handler = resultContext -> {
        };
        assertThat(execute("selectWithHandler", session, handler)).isNull();
        assertThat(session.last("select").args()).containsExactly(id("selectWithHandler"), null, handler);
    }

    @Test
    void rowCountIsConvertedToTheDeclaredInsertReturnType() {
        RecordingSession session = new RecordingSession(configuration());
        session.canned.put("insert", 2);
        assertThat(execute("insertInt", session)).isEqualTo(2);
        assertThat(execute("insertInteger", session)).isEqualTo(2);
        assertThat(execute("insertLong", session)).isEqualTo(2L);
        assertThat(execute("insertBoolean", session)).isEqualTo(true);
        assertThat(execute("insertVoid", session)).isNull();
        assertThat(session.last("insert").args()).containsExactly(id("insertVoid"), null);

        session.canned.put("insert", 0);
        assertThat(execute("insertBoolean", session)).isEqualTo(false);
    }

    @Test
    void unsupportedRowCountReturnTypeFails() {
        RecordingSession session = new RecordingSession(configuration());
        session.canned.put("insert", 1);
        assertThatThrownBy(() -> execute("insertUnsupported", session)).isInstanceOf(BindingException.class)
                .hasMessageContaining("has an unsupported return type: class java.lang.String");
    }

    @Test
    void pageReturnNeedsAPageArgument() {
        RecordingSession session = new RecordingSession(configuration());
        assertThatThrownBy(() -> execute("selectPageWithoutPageArgument", session))
                .isInstanceOf(MybatisPlusException.class).hasMessageContaining("can't found IPage for args!");
    }

    @Test
    void pageRecordsAreFilledFromTheSelectListResult() {
        RecordingSession session = new RecordingSession(configuration());
        session.canned.put("selectList", new ArrayList<>(ROWS));
        SimplePage page = new SimplePage();
        assertThat(execute("selectPage", session, page)).isSameAs(page);
        assertThat(page.getRecords()).containsExactlyElementsOf(ROWS);
        assertThat(session.last("selectList").args()).containsExactly(id("selectPage"), page);
    }

    @Test
    void wrapperParameterAliasComesFromTheParamAnnotation() {
        RecordingSession session = new RecordingSession(configuration());
        session.canned.put("selectList", new ArrayList<>(ROWS));
        QueryWrapper<String> wrapper = new QueryWrapper<>();
        wrapper.setParamAlias("staleAlias");
        execute("selectByWrapper", session, wrapper);
        assertThat(wrapper.getParamAlias()).as("@Param 上写的别名要盖掉 Wrapper 自己带来的别名").isEqualTo("ew");
        assertThat(session.last("selectList").args())
                .containsExactly(id("selectByWrapper"), Map.of("ew", wrapper, "param1", wrapper));
    }

    @Test
    void primitiveReturnTypeCannotBeNull() {
        RecordingSession session = new RecordingSession(configuration());
        assertThatThrownBy(() -> execute("selectCount", session)).isInstanceOf(BindingException.class)
                .hasMessageContaining("attempted to return null from a method with a primitive return type (int)");
    }

    @Test
    void flushUsesFlushStatements() {
        RecordingSession session = new RecordingSession(configuration());
        List<BatchResult> flushed = new ArrayList<>();
        session.canned.put("flushStatements", flushed);
        assertThat(execute("flush", session)).isSameAs(flushed);
        assertThat(session.calls).extracting(Call::method).containsExactly("flushStatements");
    }

    private static Object execute(String methodName, RecordingSession session, Object... args) {
        return mapperMethod(session.configuration, methodName).execute(session.proxy(), args);
    }

    private static MybatisMapperMethod mapperMethod(Configuration configuration, String methodName) {
        return new MybatisMapperMethod(Mapper.class, method(methodName), configuration);
    }

    private static String id(String methodName) {
        return Mapper.class.getName() + '.' + methodName;
    }

    private static Method method(String methodName) {
        return Arrays.stream(Mapper.class.getDeclaredMethods()).filter(m -> m.getName().equals(methodName)).findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Mapper 里没有 " + methodName));
    }

    /**
     * 每条语句都按 {@code 接口全名.方法名} 注册，命令类型只由方法名前缀决定：{@code insert*} 是 INSERT， 其余是 SELECT。带 {@code @Flush}
     * 的方法故意不注册，上游就是靠"没有语句 + 有注解"判成 FLUSH 的。
     */
    private static Configuration configuration() {
        Configuration configuration = new Configuration();
        for (Method method : Mapper.class.getDeclaredMethods()) {
            if (method.isAnnotationPresent(Flush.class)) {
                continue;
            }
            SqlCommandType type = method.getName().startsWith("insert") ? SqlCommandType.INSERT : SqlCommandType.SELECT;
            Class<?> resultType = VOID_RESULT_TYPES.contains(method.getName()) ? void.class : String.class;
            ResultMap resultMap = new ResultMap.Builder(configuration, method.getName() + "Result", resultType,
                    Collections.emptyList()).build();
            SqlSource sqlSource = parameterObject -> new BoundSql(configuration, "select 1", Collections.emptyList(),
                    parameterObject);
            configuration.addMappedStatement(
                    new MappedStatement.Builder(configuration, id(method.getName()), sqlSource, type)
                            .resultMaps(List.of(resultMap)).build());
        }
        return configuration;
    }

    private record Call(String method, List<Object> args) {
    }

    /**
     * 只记调用、按方法名返回预置值的 SqlSession 替身，零依赖：分派表要盯的是"调了哪个方法、带了什么参数"， 不是结果集怎么映射出来的。
     */
    private static final class RecordingSession implements InvocationHandler {

        private final Configuration configuration;
        private final List<Call> calls = new ArrayList<>();
        private final Map<String, Object> canned = new HashMap<>();

        private RecordingSession(Configuration configuration) {
            this.configuration = configuration;
        }

        private SqlSession proxy() {
            return (SqlSession) Proxy
                    .newProxyInstance(SqlSession.class.getClassLoader(), new Class<?>[]{SqlSession.class}, this);
        }

        private Call last(String methodName) {
            List<Call> matched = calls.stream().filter(call -> call.method().equals(methodName)).toList();
            assertThat(matched).as("期望调用过 SqlSession#" + methodName).isNotEmpty();
            return matched.get(matched.size() - 1);
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) {
            calls.add(new Call(method.getName(), args == null ? List.of() : Arrays.asList(args)));
            if ("getConfiguration".equals(method.getName())) {
                return configuration;
            }
            Object value = canned.get(method.getName());
            if (value != null) {
                return value;
            }
            Class<?> returnType = method.getReturnType();
            if (returnType == int.class) {
                return 0;
            }
            if (returnType == boolean.class) {
                return false;
            }
            if (returnType == long.class) {
                return 0L;
            }
            if (List.class.isAssignableFrom(returnType)) {
                return new ArrayList<>();
            }
            if (Map.class.isAssignableFrom(returnType)) {
                return new HashMap<>();
            }
            return null;
        }
    }

    /**
     * core 模块里没有 Page 实现（分页实体在 extension），IPage 分支只需要一个能装 records 的壳。
     */
    private static final class SimplePage implements IPage<String> {

        private List<String> records = new ArrayList<>();
        private long total;
        private long size = 10L;
        private long current = 1L;

        @Override
        public List<OrderItem> orders() {
            return Collections.emptyList();
        }

        @Override
        public List<String> getRecords() {
            return records;
        }

        @Override
        public IPage<String> setRecords(List<String> records) {
            this.records = records;
            return this;
        }

        @Override
        public long getTotal() {
            return total;
        }

        @Override
        public IPage<String> setTotal(long total) {
            this.total = total;
            return this;
        }

        @Override
        public long getSize() {
            return size;
        }

        @Override
        public IPage<String> setSize(long size) {
            this.size = size;
            return this;
        }

        @Override
        public long getCurrent() {
            return current;
        }

        @Override
        public IPage<String> setCurrent(long current) {
            this.current = current;
            return this;
        }
    }

    interface Mapper {

        List<String> selectNames();

        Set<String> selectNameSet();

        String[] selectNameArray();

        int[] selectCountArray();

        String selectOneName();

        Optional<String> selectOptional();

        Cursor<String> selectCursorRows();

        @MapKey("id")
        Map<String, String> selectNameByKey();

        List<String> selectPaged(RowBounds rowBounds);

        void selectWithHandler(ResultHandler<String> handler);

        void selectWithHandlerPaged(ResultHandler<String> handler, RowBounds rowBounds);

        void selectHandlerWithoutResultType(ResultHandler<String> handler);

        int selectCount();

        IPage<String> selectPage(IPage<String> page);

        IPage<String> selectPageWithoutPageArgument();

        List<String> selectByWrapper(@Param("ew") QueryWrapper<String> wrapper);

        int insertInt();

        Integer insertInteger();

        long insertLong();

        boolean insertBoolean();

        void insertVoid();

        String insertUnsupported();

        @Flush
        List<BatchResult> flush();
    }
}
