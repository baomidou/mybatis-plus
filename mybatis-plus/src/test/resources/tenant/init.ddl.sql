CREATE TABLE IF NOT EXISTS  student (
	id BIGINT(20) NOT NULL,
	name VARCHAR(30) NULL DEFAULT NULL ,
	tenant_id BIGINT(20) NOT NULL ,
	PRIMARY KEY (id)
);
insert into student values (1,'a',1);
insert into student values (2,'b',2);

