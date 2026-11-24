package com.baomidou.mybatisplus.test.extension.parser;

import com.baomidou.mybatisplus.extension.parser.JsqlParserSupport;
import net.sf.jsqlparser.schema.Table;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@Tag("unit")
class JsqlParserSupportTest extends JsqlParserSupport {

    @Test
    void prefixesTableNameWithNonEmptySchema() {
        assertThat(getSchemaNameDotName(new Table("app", "orders"))).isEqualTo("app.orders");
        assertThat(getSchemaNameDotName(new Table("", "orders"))).isEqualTo("orders");
        assertThat(getSchemaNameDotName(new Table("orders"))).isEqualTo("orders");
    }
}
