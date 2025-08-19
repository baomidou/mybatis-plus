package com.baomidou.mybatisplus.generator.samples;

import com.baomidou.mybatisplus.generator.AutoGenerator;
import com.baomidou.mybatisplus.generator.config.DataSourceConfig;
import com.baomidou.mybatisplus.generator.config.StrategyConfig;
import com.baomidou.mybatisplus.generator.config.rules.DbColumnType;
import com.baomidou.mybatisplus.generator.query.DefaultQuery;
import com.baomidou.mybatisplus.generator.query.SQLQuery;
import org.apache.ibatis.jdbc.SqlRunner;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;

/**
 * @author nieqiurong
 * @since 3.5.13
 */
public class GaussDBGeneratorTest {

    private static final String CRATE_TABLE_DDL = """

        DROP TABLE IF EXISTS "t_user";

        CREATE TABLE "t_user" (
          "id" bigserial,
          "name" varchar(50),
          "age" int4,
          "sex" int2,
          "enable" bool DEFAULT true,
          "ext_json" json,
          "birthday" date,
          "status" char,
          "amount" decimal(10,2),
          "score" numeric,
          "creat_time" timestamp,
          "describe" text,
          "uid" uuid,
          PRIMARY KEY ("id")
        );

        COMMENT ON COLUMN "t_user"."name" IS '姓名';

        COMMENT ON COLUMN "t_user"."age" IS '年龄';

        COMMENT ON COLUMN "t_user"."sex" IS '性别(0:未知 1:男 2:女)';

        COMMENT ON COLUMN "t_user"."enable" IS '是否启用';

        COMMENT ON COLUMN "t_user"."ext_json" IS '扩展信息JSON';

        COMMENT ON COLUMN "t_user"."birthday" IS '生日';

        COMMENT ON COLUMN "t_user"."status" IS '状态（A:正常, D:禁用）';

        COMMENT ON COLUMN "t_user"."amount" IS '金额';

        COMMENT ON COLUMN "t_user"."score" IS '分数';

        COMMENT ON COLUMN "t_user"."creat_time" IS '创建时间';

        COMMENT ON COLUMN "t_user"."describe" IS '描述';

        COMMENT ON COLUMN "t_user"."uid" IS 'uid';
        """;


    private static final String DB_URL = "jdbc:gaussdb://127.0.0.1:8000/baomidou";

    private static final String DB_USER = "root";

    private static final String DB_PASSWORD = "123456";

    public static void main(String[] args) throws SQLException {
        //建议使用默认的 defaultQuery() 方式查询
//        defaultQuery();
        sqlQuery();
    }

    private static void defaultQuery() throws SQLException {
        DataSourceConfig dataSourceConfig = new DataSourceConfig
            .Builder(DB_URL, DB_USER, DB_PASSWORD)
            .databaseQueryClass(DefaultQuery.class)
            .typeConvertHandler((globalConfig, typeRegistry, metaInfo) -> {
                //int2 这种默认是转为Short,需要转换为int单独注册自己的转换器处理适配。
                if (metaInfo.getJdbcType().TYPE_CODE == Types.SMALLINT) {
                    return DbColumnType.INTEGER;
                }
                return typeRegistry.getColumnType(metaInfo);
            })
            .build();
        createTable(dataSourceConfig.getConn());
        AutoGenerator generator = new AutoGenerator(dataSourceConfig);
        generator.strategy(getStrategyConfig());
        generator.execute();
    }

    private static void createTable(Connection connection) throws SQLException {
        new SqlRunner(connection).run(CRATE_TABLE_DDL);
    }

    private static StrategyConfig getStrategyConfig() {
        return new StrategyConfig.Builder().addInclude("t_user").build()
            .mapperBuilder().disable().serviceBuilder().disable().controllerBuilder().disable()
            .entityBuilder().enableTableFieldAnnotation()
            .enableFileOverride().build();
    }

    private static void sqlQuery() throws SQLException {
        DataSourceConfig dataSourceConfig = new DataSourceConfig
            .Builder(DB_URL, DB_USER, DB_PASSWORD)
            .databaseQueryClass(SQLQuery.class)
            .build();
        createTable(dataSourceConfig.getConn());
        AutoGenerator generator = new AutoGenerator(dataSourceConfig);
        generator.strategy(getStrategyConfig());
        generator.execute();
    }


}
