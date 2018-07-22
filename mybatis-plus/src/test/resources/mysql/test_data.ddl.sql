CREATE TABLE IF NOT EXISTS tb_test_data_logic (
    id              BIGINT primary key,
    test_int        integer,
    test_str        varchar(50),
    test_double     double,
    test_boolean    tinyint(1),
    test_date       date,
    test_time       time,
    test_date_time  datetime,
    test_timestamp  timestamp,
    create_datetime datetime,
    update_datetime datetime,
    deleted         tinyint(1) default 0,
    version        integer default 0
)
    ENGINE = innodb
    DEFAULT CHARSET = utf8;

CREATE TABLE IF NOT EXISTS tb_test_data (
    id              BIGINT primary key,
    test_int        integer,
    test_str        varchar(50),
    test_double     double,
    test_boolean    tinyint(1),
    test_date       date,
    test_time       time,
    test_date_time  datetime,
    test_timestamp  timestamp,
    create_datetime datetime,
    update_datetime datetime,
    version        integer default 0
)
    ENGINE = innodb
    DEFAULT CHARSET = utf8;
