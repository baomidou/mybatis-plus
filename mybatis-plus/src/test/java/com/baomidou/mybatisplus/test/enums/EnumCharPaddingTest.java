package com.baomidou.mybatisplus.test.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.baomidou.mybatisplus.annotation.IEnum;
import com.baomidou.mybatisplus.core.handlers.MybatisEnumTypeHandler;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.ibatis.type.JdbcType;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import static org.assertj.core.api.Assertions.assertThat;

@Tag("integration")
class EnumCharPaddingTest {

    @Test
    void enumValueRoundTripsThroughFixedWidthChar() throws Exception {
        MybatisEnumTypeHandler<CharStatus> handler = new MybatisEnumTypeHandler<>(CharStatus.class);
        try (Connection connection = DriverManager.getConnection("jdbc:h2:mem:enum_char_padding");
                PreparedStatement statement = connection.prepareStatement("SELECT CAST(? AS CHAR(8)) AS code")) {
            handler.setParameter(statement, 1, CharStatus.ON, JdbcType.CHAR);
            try (ResultSet result = statement.executeQuery()) {
                assertThat(result.next()).isTrue();
                assertThat(result.getString("code")).isEqualTo("ON      ");
                assertThat(handler.getResult(result, "code")).isEqualTo(CharStatus.ON);
                assertThat(handler.getResult(result, 1)).isEqualTo(CharStatus.ON);
            }
        }
    }

    @Test
    void iEnumRoundTripsThroughFixedWidthChar() throws Exception {
        MybatisEnumTypeHandler<CharFlag> handler = new MybatisEnumTypeHandler<>(CharFlag.class);
        try (Connection connection = DriverManager.getConnection("jdbc:h2:mem:ienum_char_padding");
                PreparedStatement statement = connection.prepareStatement("SELECT CAST(? AS CHAR(4)) AS code")) {
            handler.setParameter(statement, 1, CharFlag.YES, JdbcType.CHAR);
            try (ResultSet result = statement.executeQuery()) {
                assertThat(result.next()).isTrue();
                assertThat(result.getString("code")).isEqualTo("Y   ");
                assertThat(handler.getResult(result, 1)).isEqualTo(CharFlag.YES);
            }
        }
    }

    @Getter
    @AllArgsConstructor
    enum CharStatus {

        ON("ON");
        @EnumValue
        private final String code;
    }

    @Getter
    @AllArgsConstructor
    enum CharFlag implements IEnum<String> {

        YES("Y");
        private final String value;
    }
}
