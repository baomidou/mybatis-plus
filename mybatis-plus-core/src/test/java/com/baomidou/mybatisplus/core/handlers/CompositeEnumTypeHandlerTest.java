package com.baomidou.mybatisplus.core.handlers;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.baomidou.mybatisplus.annotation.IEnum;
import org.apache.ibatis.type.EnumTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeException;
import org.apache.ibatis.type.TypeHandler;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * {@link CompositeEnumTypeHandler} 只做一件事：替每个枚举挑一个合适的 {@link TypeHandler}—— 实现 {@link IEnum} 或带 {@link EnumValue} 字段的走
 * {@code MybatisEnumTypeHandler}（按持久值读写）， 其余走用户配置的默认处理器（没配就是 MyBatis 的 {@link EnumTypeHandler}，按 {@code name()} 读写）。
 * 这个分派表此前没有任何用例提过，而配错默认处理器会让全库的枚举映射静默变形。 注意它有两个静态状态：按枚举类缓存的判定结果，和可被 setter 改写的默认处理器， 所以每个用例跑完都要把默认处理器还原。
 */
@Tag("unit")
class CompositeEnumTypeHandlerTest {

    @AfterEach
    void restoreDefaultHandler() throws Exception {
        setDefaultEnumTypeHandler(EnumTypeHandler.class);
    }

    @Test
    void enumImplementingIEnumGetsTheMpHandler() {
        assertThat(delegateOf(new CompositeEnumTypeHandler<>(StatusEnum.class)))
                .isInstanceOf(MybatisEnumTypeHandler.class);
    }

    @Test
    void enumWithEnumValueFieldGetsTheMpHandler() {
        assertThat(delegateOf(new CompositeEnumTypeHandler<>(LevelEnum.class)))
                .isInstanceOf(MybatisEnumTypeHandler.class);
    }

    @Test
    void plainEnumKeepsTheNameBasedHandler() {
        assertThat(delegateOf(new CompositeEnumTypeHandler<>(ColorEnum.class))).isInstanceOf(EnumTypeHandler.class);
    }

    @Test
    void nullEnumClassIsRejected() {
        assertThatThrownBy(() -> new CompositeEnumTypeHandler<ColorEnum>(null))
                .isInstanceOf(IllegalArgumentException.class).hasMessage("Type argument cannot be null");
    }

    @Test
    void customDefaultHandlerIsUsedForPlainEnums() {
        CompositeEnumTypeHandler.setDefaultEnumTypeHandler(ClassConstructorHandler.class);
        assertThat(delegateOf(new CompositeEnumTypeHandler<>(ColorEnum.class)))
                .isInstanceOf(ClassConstructorHandler.class);
    }

    @Test
    void defaultHandlerWithOnlyANoArgConstructorIsStillUsable() {
        CompositeEnumTypeHandler.setDefaultEnumTypeHandler(NoArgConstructorHandler.class);
        assertThat(delegateOf(new CompositeEnumTypeHandler<>(ColorEnum.class)))
                .isInstanceOf(NoArgConstructorHandler.class);
    }

    @Test
    void defaultHandlerWithoutAnyUsableConstructorReportsTypeException() {
        CompositeEnumTypeHandler.setDefaultEnumTypeHandler(NoUsableConstructorHandler.class);
        assertThatThrownBy(() -> new CompositeEnumTypeHandler<>(ColorEnum.class)).isInstanceOf(TypeException.class)
                .hasMessageContaining("Unable to find a usable constructor for");
    }

    @Test
    void mpEnumsIgnoreTheConfiguredDefaultHandler() {
        CompositeEnumTypeHandler.setDefaultEnumTypeHandler(ClassConstructorHandler.class);
        assertThat(delegateOf(new CompositeEnumTypeHandler<>(StatusEnum.class)))
                .isInstanceOf(MybatisEnumTypeHandler.class);
    }

    @Test
    void settingTheDefaultToNullOrToTheMpHandlerIsIgnored() {
        CompositeEnumTypeHandler.setDefaultEnumTypeHandler(null);
        assertThat(delegateOf(new CompositeEnumTypeHandler<>(ColorEnum.class))).isInstanceOf(EnumTypeHandler.class);
        CompositeEnumTypeHandler.setDefaultEnumTypeHandler(MybatisEnumTypeHandler.class);
        assertThat(delegateOf(new CompositeEnumTypeHandler<>(ColorEnum.class))).isInstanceOf(EnumTypeHandler.class);
    }

    @Test
    void mpEnumIsWrittenByItsPersistentValue() throws Exception {
        PreparedStatement statement = mock(PreparedStatement.class);
        new CompositeEnumTypeHandler<>(LevelEnum.class).setParameter(statement, 1, LevelEnum.HIGH, null);
        verify(statement).setObject(1, 3);
    }

    @Test
    void mpEnumIsReadBackByItsPersistentValue() throws Exception {
        ResultSet resultSet = mock(ResultSet.class);
        when(resultSet.getObject("level", Integer.class)).thenReturn(3);
        assertThat(new CompositeEnumTypeHandler<>(LevelEnum.class).getResult(resultSet, "level"))
                .isEqualTo(LevelEnum.HIGH);
    }

    @Test
    void plainEnumIsWrittenAndReadByName() throws Exception {
        PreparedStatement statement = mock(PreparedStatement.class);
        CompositeEnumTypeHandler<ColorEnum> handler = new CompositeEnumTypeHandler<>(ColorEnum.class);
        handler.setParameter(statement, 1, ColorEnum.RED, JdbcType.VARCHAR);
        verify(statement).setObject(1, "RED", JdbcType.VARCHAR.TYPE_CODE);
        ResultSet resultSet = mock(ResultSet.class);
        when(resultSet.getString("color")).thenReturn("RED");
        assertThat(handler.getResult(resultSet, "color")).isEqualTo(ColorEnum.RED);
    }

    private static Object delegateOf(CompositeEnumTypeHandler<?> handler) {
        try {
            Field field = CompositeEnumTypeHandler.class.getDeclaredField("delegate");
            field.setAccessible(true);
            return field.get(handler);
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException("CompositeEnumTypeHandler 的 delegate 字段形状变了", e);
        }
    }

    private static void setDefaultEnumTypeHandler(Class<? extends TypeHandler> typeHandlerClass) throws Exception {
        Field field = CompositeEnumTypeHandler.class.getDeclaredField("defaultEnumTypeHandler");
        field.setAccessible(true);
        field.set(null, typeHandlerClass);
    }

    enum StatusEnum implements IEnum<Integer> {

        ENABLE(1),
        DISABLE(0);

        private final Integer value;

        StatusEnum(Integer value) {
            this.value = value;
        }

        @Override
        public Integer getValue() {
            return value;
        }
    }

    enum LevelEnum {

        LOW(1),
        MID(2),
        HIGH(3);

        @EnumValue
        private final int code;

        LevelEnum(int code) {
            this.code = code;
        }

        public int getCode() {
            return code;
        }
    }

    enum ColorEnum {
        RED,
        GREEN
    }

    public static class ClassConstructorHandler implements TypeHandler<Object> {

        public ClassConstructorHandler(Class<?> enumClass) {
        }

        @Override
        public void setParameter(PreparedStatement ps, int i, Object parameter, JdbcType jdbcType) {
        }

        @Override
        public Object getResult(ResultSet rs, String columnName) {
            return null;
        }

        @Override
        public Object getResult(ResultSet rs, int columnIndex) {
            return null;
        }

        @Override
        public Object getResult(CallableStatement cs, int columnIndex) {
            return null;
        }
    }

    public static class NoArgConstructorHandler implements TypeHandler<Object> {

        public NoArgConstructorHandler() {
        }

        @Override
        public void setParameter(PreparedStatement ps, int i, Object parameter, JdbcType jdbcType) {
        }

        @Override
        public Object getResult(ResultSet rs, String columnName) {
            return null;
        }

        @Override
        public Object getResult(ResultSet rs, int columnIndex) {
            return null;
        }

        @Override
        public Object getResult(CallableStatement cs, int columnIndex) {
            return null;
        }
    }

    public static class NoUsableConstructorHandler implements TypeHandler<Object> {

        public NoUsableConstructorHandler(Class<?> enumClass, String extra) {
        }

        @Override
        public void setParameter(PreparedStatement ps, int i, Object parameter, JdbcType jdbcType) {
        }

        @Override
        public Object getResult(ResultSet rs, String columnName) {
            return null;
        }

        @Override
        public Object getResult(ResultSet rs, int columnIndex) {
            return null;
        }

        @Override
        public Object getResult(CallableStatement cs, int columnIndex) {
            return null;
        }
    }
}
