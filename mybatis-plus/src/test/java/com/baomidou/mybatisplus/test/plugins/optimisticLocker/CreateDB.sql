DROP TABLE
IF EXISTS version_user;

CREATE TABLE version_user (
	id bigint (11) NOT NULL ,
	NAME VARCHAR (20),
	version INT (11),
	PRIMARY KEY (`id`)
) ENGINE = INNODB AUTO_INCREMENT = 1 DEFAULT CHARSET = utf8;

DROP TABLE
IF EXISTS time_version_user;

CREATE TABLE time_version_user (
	id bigint (11) NOT NULL ,
	NAME VARCHAR (20),
	version datetime,
	PRIMARY KEY (`id`)
) ENGINE = INNODB AUTO_INCREMENT = 1 DEFAULT CHARSET = utf8;

insert into version_user (id,name,version) values(1,"zhangsan",15);
insert into version_user (id,name,version) values(2,"lisi",null);
insert into version_user (id,name,version) values(3,"wangwu",109);

