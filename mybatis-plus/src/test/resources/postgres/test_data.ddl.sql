CREATE TABLE IF NOT EXISTS tb_test_data_logic (
    id              BIGINT primary key,
    test_int        integer,
    test_str        varchar(50),
    test_double     double precision,
    test_boolean    smallint,
    test_date       date,
    test_time       time,
    test_date_time  timestamp,
    test_timestamp  timestamp,
    create_datetime timestamp,
    update_datetime timestamp,
    deleted         smallint default 0,
    version         integer  default 0
);

CREATE TABLE IF NOT EXISTS tb_test_data (
    id              BIGINT primary key,
    test_int        integer,
    test_str        varchar(50),
    test_double     double precision,
    test_boolean    smallint,
    test_date       date,
    test_time       time,
    test_date_time  timestamp,
    test_timestamp  timestamp,
    create_datetime timestamp,
    update_datetime timestamp,
    version         integer default 0
);
