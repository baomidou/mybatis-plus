drop table if exists public.common_data;
drop table if exists public.common_logic_data;
drop table if exists public.pg_data;
CREATE TABLE public.common_data (
    id        BIGINT primary key,
    test_int  integer,
    test_str  varchar(50),
    c_time    timestamp,
    u_time    timestamp,
    version   integer default 0,
    test_enum integer,
    tenant_id bigint
);

CREATE TABLE public.common_logic_data (
    id       BIGINT primary key,
    test_int integer,
    test_str varchar(50),
    c_time   timestamp,
    u_time   timestamp,
    deleted  smallint default 0,
    version  integer  default 0
);

CREATE TABLE public.pg_data (
    id      BIGINT primary key,
    "pgInt" integer,
    pg_int2 integer,
    "group" integer
);
