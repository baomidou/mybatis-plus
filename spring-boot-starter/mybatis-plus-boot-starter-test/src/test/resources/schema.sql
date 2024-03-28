drop table if exists sample;
create table if not exists sample
(
    id   bigint,
    name varchar
);

drop table if exists sample_user;
create table if not exists sample_user
(
    id   int,
    username varchar,
    password varchar,
    create_time timestamp
);
