drop table if exists common_data;
drop table if exists common_logic_data;
drop table if exists pg_data;
CREATE TABLE common_data (
    id        BIGINT primary key,
    test_int  integer,
    test_str  varchar(50),
    c_time    timestamp,
    u_time    timestamp,
    version   integer default 0,
    tenant_id bigint
);

CREATE TABLE common_logic_data (
    id       BIGINT primary key,
    test_int integer,
    test_str varchar(50),
    c_time   timestamp,
    u_time   timestamp,
    deleted  smallint default 0,
    version  integer  default 0
);

CREATE TABLE pg_data (
    id      BIGINT primary key,
    "pgInt" integer,
    pg_int2 integer,
    "group" integer
);
