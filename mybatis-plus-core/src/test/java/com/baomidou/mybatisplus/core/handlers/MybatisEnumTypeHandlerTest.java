package com.baomidou.mybatisplus.core.handlers;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.baomidou.mybatisplus.annotation.IEnum;
import com.baomidou.mybatisplus.core.toolkit.EnumUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.ibatis.type.JdbcType;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Tests for {@link MybatisEnumTypeHandler}.
 *
 * @author baomidou
 * @since 3.5.15
 */
@ExtendWith(MockitoExtension.class)
@Tag("unit")
class MybatisEnumTypeHandlerTest {

    @Mock
    private ResultSet resultSet;

    @Mock
    private PreparedStatement preparedStatement;

    @Mock
    private CallableStatement callableStatement;

    @Test
    void shouldSupportIEnum() throws Exception {
        MybatisEnumTypeHandler<SexEnum> handler = new MybatisEnumTypeHandler<>(SexEnum.class);

        when(resultSet.getObject("column", Integer.class)).thenReturn(1);
        assertThat(handler.getResult(resultSet, "column")).isEqualTo(SexEnum.MAN);

        when(resultSet.getObject(1, Integer.class)).thenReturn(2);
        assertThat(handler.getResult(resultSet, 1)).isEqualTo(SexEnum.WO_MAN);

        when(callableStatement.getObject(1, Integer.class)).thenReturn(null);
        assertThat(handler.getResult(callableStatement, 1)).isNull();

        handler.setParameter(preparedStatement, 1, SexEnum.MAN, null);
        verify(preparedStatement).setObject(1, 1);

        handler.setParameter(preparedStatement, 2, SexEnum.WO_MAN, JdbcType.INTEGER);
        verify(preparedStatement).setObject(2, 2, JdbcType.INTEGER.TYPE_CODE);
    }

    @Test
    void shouldSupportEnumValue() throws Exception {
        MybatisEnumTypeHandler<GradeEnum> handler = new MybatisEnumTypeHandler<>(GradeEnum.class);

        when(resultSet.getObject("column", Integer.class)).thenReturn(1);
        assertThat(handler.getResult(resultSet, "column")).isEqualTo(GradeEnum.PRIMARY);

        when(resultSet.getObject("column", Integer.class)).thenReturn(3);
        assertThat(handler.getResult(resultSet, "column")).isEqualTo(GradeEnum.HIGH);

        handler.setParameter(preparedStatement, 1, GradeEnum.SECONDARY, null);
        verify(preparedStatement).setObject(1, 2);
    }

    @Test
    void shouldReturnNullForJdbcNullAndMissingValue() throws Exception {
        MybatisEnumTypeHandler<SexEnum> handler = new MybatisEnumTypeHandler<>(SexEnum.class);

        when(resultSet.getObject("column", Integer.class)).thenReturn(null);
        assertThat(handler.getResult(resultSet, "column")).isNull();

        when(resultSet.getObject("column", Integer.class)).thenReturn(9);
        assertThat(handler.getResult(resultSet, "column")).isNull();

        when(callableStatement.getObject(1, Integer.class)).thenReturn(0);
        when(callableStatement.wasNull()).thenReturn(true);
        assertThat(handler.getResult(callableStatement, 1)).isNull();
    }

    @Test
    void shouldUseNormalizedMapKey() throws Exception {
        MybatisEnumTypeHandler<LongEnum> longHandler = new MybatisEnumTypeHandler<>(LongEnum.class);
        when(resultSet.getObject(eq("longColumn"), eq(Long.class))).thenAnswer(invocation -> Integer.valueOf(1));
        assertThat(longHandler.getResult(resultSet, "longColumn")).isEqualTo(LongEnum.ONE);

        MybatisEnumTypeHandler<BigIntegerEnum> bigIntegerHandler = new MybatisEnumTypeHandler<>(BigIntegerEnum.class);
        when(resultSet.getObject(eq("bigIntegerColumn"), eq(BigInteger.class)))
                .thenAnswer(invocation -> Integer.valueOf(2));
        assertThat(bigIntegerHandler.getResult(resultSet, "bigIntegerColumn")).isEqualTo(BigIntegerEnum.TWO);

        MybatisEnumTypeHandler<StringEnum> stringHandler = new MybatisEnumTypeHandler<>(StringEnum.class);
        when(resultSet.getObject(eq("stringColumn"), eq(String.class))).thenAnswer(invocation -> Integer.valueOf(3));
        assertThat(stringHandler.getResult(resultSet, "stringColumn")).isEqualTo(StringEnum.THREE);

        MybatisEnumTypeHandler<CharacterEnum> characterHandler = new MybatisEnumTypeHandler<>(CharacterEnum.class);
        when(resultSet.getObject(eq("characterColumn"), eq(Character.class)))
                .thenAnswer(invocation -> Integer.valueOf(4));
        assertThat(characterHandler.getResult(resultSet, "characterColumn")).isEqualTo(CharacterEnum.FOUR);
    }

    @Test
    void shouldSupportBigDecimalValue() throws Exception {
        MybatisEnumTypeHandler<DecimalEnum> handler = new MybatisEnumTypeHandler<>(DecimalEnum.class);

        when(resultSet.getObject(eq("column"), eq(BigDecimal.class))).thenAnswer(invocation -> Long.valueOf(10L));
        assertThat(handler.getResult(resultSet, "column")).isEqualTo(DecimalEnum.TEN);

        when(resultSet.getObject(eq("column"), eq(BigDecimal.class))).thenAnswer(invocation -> BigDecimal.valueOf(20L));
        assertThat(handler.getResult(resultSet, "column")).isEqualTo(DecimalEnum.TWENTY);
    }

    @Test
    void shouldFailFastForIllegalEnum() {
        assertThatThrownBy(() -> new MybatisEnumTypeHandler<>(PlainEnum.class))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void shouldDetectMpEnums() {
        assertThat(MybatisEnumTypeHandler.findEnumValueFieldName(String.class)).isEmpty();
        assertThat(MybatisEnumTypeHandler.findEnumValueFieldName(GradeEnum.class)).isPresent();
        assertThat(MybatisEnumTypeHandler.findEnumValueFieldName(SexEnum.class)).isEmpty();
        assertThat(EnumUtils.isMpEnums(SexEnum.class)).isTrue();
        assertThat(EnumUtils.isMpEnums(GradeEnum.class)).isTrue();
        assertThat(EnumUtils.isMpEnums(PlainEnum.class)).isFalse();
    }

    @Test
    void metadataCacheShouldBeThreadSafe() throws Exception {
        int threadCount = 16;
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch doneLatch = new CountDownLatch(threadCount);
        List<Throwable> errors = new ArrayList<>();
        for (int i = 0; i < threadCount; i++) {
            executorService.execute(() -> {
                try {
                    startLatch.await();
                    new MybatisEnumTypeHandler<>(LongEnum.class);
                } catch (Throwable e) {
                    synchronized (errors) {
                        errors.add(e);
                    }
                } finally {
                    doneLatch.countDown();
                }
            });
        }
        startLatch.countDown();
        assertThat(doneLatch.await(10, TimeUnit.SECONDS)).isTrue();
        executorService.shutdownNow();
        assertThat(errors).isEmpty();
    }

    @Getter
    @AllArgsConstructor
    enum SexEnum implements IEnum<Integer> {

        MAN(1, "1"),
        WO_MAN(2, "2");

        final Integer code;
        final String desc;

        @Override
        public Integer getValue() {
            return this.code;
        }
    }

    @Getter
    @AllArgsConstructor
    enum GradeEnum {

        PRIMARY(1, "小学"),
        SECONDARY(2, "中学"),
        HIGH(3, "高中");

        @EnumValue
        private final int code;
        private final String desc;
    }

    @Getter
    @AllArgsConstructor
    enum LongEnum implements IEnum<Long> {

        ONE(1L);

        private final Long value;
    }

    @Getter
    @AllArgsConstructor
    enum BigIntegerEnum {

        TWO(BigInteger.valueOf(2L));

        @EnumValue
        private final BigInteger code;
    }

    @Getter
    @AllArgsConstructor
    enum StringEnum {

        THREE("3");

        @EnumValue
        private final String code;
    }

    @Getter
    @AllArgsConstructor
    enum DecimalEnum {

        TEN(BigDecimal.valueOf(10L)),
        TWENTY(BigDecimal.valueOf(20L));

        @EnumValue
        private final BigDecimal code;
    }

    @Getter
    @AllArgsConstructor
    enum CharacterEnum {

        FOUR('4');

        @EnumValue
        private final char code;
    }

    enum PlainEnum {

        A
    }
}
