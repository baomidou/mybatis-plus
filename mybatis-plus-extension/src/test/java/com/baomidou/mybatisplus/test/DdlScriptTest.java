package com.baomidou.mybatisplus.test;

import com.baomidou.mybatisplus.extension.ddl.DdlScript;
import org.h2.Driver;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DdlScriptTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(DdlScriptTest.class);

    @Test
    void test() throws Exception {
        var ddlScript = new DdlScript(Driver.class.getName(),
            "jdbc:h2:mem:test;MODE=mysql;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE",
            "sa", "", true).scriptRunner(scriptRunner -> {
                scriptRunner.setLogWriter(null);
        });
        LOGGER.info("--------------execute----------------");
        ddlScript.run( "select 1 from dual;", msg ->{});
        LOGGER.info("--------------execute----------------");
        ddlScript.run( "select 2 from dual;", msg ->{});
        LOGGER.info("--------------run----------------");
        ddlScript.run("select 1 from dual;");
        LOGGER.info("--------------run----------------");
        ddlScript.run("select 3 from dual#", "#");
    }

}
