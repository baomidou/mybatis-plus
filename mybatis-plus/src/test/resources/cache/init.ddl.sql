CREATE TABLE IF NOT EXISTS  cache (
	id BIGINT(20) NOT NULL,
	name VARCHAR(30) NULL DEFAULT NULL ,
	PRIMARY KEY (id)
);
insert into cache values (1,'a');
insert into cache values (2,'b');
insert into cache values (3,'c');
insert into cache values (4,'d');
insert into cache values (5,'e');
