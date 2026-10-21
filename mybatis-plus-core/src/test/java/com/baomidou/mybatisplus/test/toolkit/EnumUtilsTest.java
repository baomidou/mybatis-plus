package com.baomidou.mybatisplus.test.toolkit;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.baomidou.mybatisplus.annotation.IEnum;
import com.baomidou.mybatisplus.core.toolkit.EnumMetadata;
import com.baomidou.mybatisplus.core.toolkit.EnumUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Tests for {@link EnumUtils}.
 *
 * @author baomidou
 * @since 3.5.15
 */
class EnumUtilsTest {

    @Test
    void shouldExposeMetadata() {
        EnumMetadata metadata = EnumUtils.metadata(GradeEnum.class);

        assertThat(metadata.getPropertyType()).isEqualTo(Integer.class);
        assertThat(metadata.getGetterName()).isEqualTo("code");
        assertThat(metadata.getEnumConstants()).isEqualTo(GradeEnum.values());
        assertThat(metadata.<GradeEnum>valueOf(1)).isEqualTo(GradeEnum.PRIMARY);
        assertThat(metadata.<GradeEnum>valueOf(1L)).isEqualTo(GradeEnum.PRIMARY);
        assertThat(metadata.getValue(GradeEnum.PRIMARY)).isEqualTo(1);
    }

    @Test
    void shouldProtectEnumConstantsSnapshot() {
        EnumMetadata metadata = EnumUtils.metadata(GradeEnum.class);

        Enum<?>[] enumConstants = metadata.getEnumConstants();
        enumConstants[0] = null;

        assertThat(metadata.getEnumConstants()[0]).isEqualTo(GradeEnum.PRIMARY);
    }

    @Test
    void shouldResolveEnumValue() {
        assertThat(EnumUtils.valueOf(SexEnum.class, 1)).isEqualTo(SexEnum.MAN);
        assertThat(EnumUtils.valueOf(SexEnum.class, "2")).isEqualTo(SexEnum.WO_MAN);
        assertThat(EnumUtils.valueOf(GradeEnum.class, BigInteger.valueOf(3L))).isEqualTo(GradeEnum.HIGH);
        assertThat(EnumUtils.valueOf(BigDecimalEnum.class, BigInteger.TEN)).isEqualTo(BigDecimalEnum.TEN);
        assertThat(EnumUtils.valueOf(GradeEnum.class, 9)).isNull();
    }

    @Test
    void shouldResolvePersistentValue() {
        assertThat(EnumUtils.getValue(SexEnum.MAN)).isEqualTo(1);
        assertThat(EnumUtils.getValue(GradeEnum.HIGH)).isEqualTo(3);
        assertThat(EnumUtils.getValue(null)).isNull();
    }

    @Test
    void shouldDetectMpEnums() {
        Optional<String> fieldName = EnumUtils.findEnumValueFieldName(GradeEnum.class);

        assertThat(fieldName).isPresent();
        assertThat(fieldName.get()).isEqualTo("code");
        assertThat(EnumUtils.findEnumValueFieldName(SexEnum.class)).isEmpty();
        assertThat(EnumUtils.findEnumValueFieldName(String.class)).isEmpty();
        assertThat(EnumUtils.isMpEnums(SexEnum.class)).isTrue();
        assertThat(EnumUtils.isMpEnums(GradeEnum.class)).isTrue();
        assertThat(EnumUtils.isMpEnums(PlainEnum.class)).isFalse();
    }

    @Test
    void shouldNormalizeValue() {
        assertThat(EnumUtils.normalizeValue(1)).isEqualTo(1L);
        assertThat(EnumUtils.normalizeValue(BigInteger.ONE)).isEqualTo(1L);
        assertThat(EnumUtils.normalizeValue(BigDecimal.TEN)).isEqualTo(10L);
        assertThat(EnumUtils.normalizeValue(new BigDecimal("1.10"))).isEqualTo(new BigDecimal("1.1"));
    }

    @Test
    void shouldFailFastForIllegalType() {
        assertThatThrownBy(() -> EnumUtils.metadata(null)).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> EnumUtils.metadata(String.class)).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> EnumUtils.metadata(PlainEnum.class)).isInstanceOf(IllegalArgumentException.class);
    }

    @Getter
    @AllArgsConstructor
    enum SexEnum implements IEnum<Integer> {

        MAN(1),
        WO_MAN(2);

        private final Integer value;
    }

    @Getter
    @AllArgsConstructor
    enum GradeEnum {

        PRIMARY(1),
        SECONDARY(2),
        HIGH(3);

        @EnumValue
        private final int code;
    }

    @Getter
    @AllArgsConstructor
    enum BigDecimalEnum {

        TEN(BigDecimal.TEN);

        @EnumValue
        private final BigDecimal code;
    }

    enum PlainEnum {

        A
    }
}
