drop table if exists common_data;
drop table if exists common_logic_data;
drop table if exists mysql_data;
CREATE TABLE common_data (
    id        BIGINT primary key,
    test_int  integer,
    test_str  varchar(50),
    c_time    datetime,
    u_time    datetime,
    version   integer default 0,
    test_enum integer,
    tenant_id bigint
)
    ENGINE = innodb
DEFAULT CHARSET = utf8;

CREATE TABLE common_logic_data (
    id       BIGINT primary key,
    test_int integer,
    test_str varchar(50),
    c_time   datetime,
    u_time   datetime,
    deleted  tinyint default 0,
    version  integer default 0
)
    ENGINE = innodb
DEFAULT CHARSET = utf8;

CREATE TABLE mysql_data (
    id      BIGINT primary key,
    `order` integer,
    `group` integer
)
    ENGINE = innodb
DEFAULT CHARSET = utf8;
