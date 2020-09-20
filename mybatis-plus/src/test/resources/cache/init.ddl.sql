CREATE TABLE IF NOT EXISTS  t_cache (
	id BIGINT(20) NOT NULL,
	name VARCHAR(30) NULL DEFAULT NULL ,
	PRIMARY KEY (id)
);
insert into t_cache values (1,'a');
insert into t_cache values (2,'b');
insert into t_cache values (3,'c');
insert into t_cache values (4,'d');
insert into t_cache values (5,'e');
