CREATE TABLE IF NOT EXISTS  t_id_generator_long (
	id BIGINT(20) NOT NULL,
	name VARCHAR(30) NULL DEFAULT NULL ,
	PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS  t_id_generator_long_string (
	id varchar NOT NULL,
	name VARCHAR(30) NULL DEFAULT NULL ,
	PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS  t_id_generator_int (
	id int NOT NULL,
	name VARCHAR(30) NULL DEFAULT NULL ,
	PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS  t_id_generator_int_string (
	id varchar NOT NULL,
	name VARCHAR(30) NULL DEFAULT NULL ,
	PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS  t_id_generator_string (
	id varchar (50) NOT NULL,
	name VARCHAR(30) NULL DEFAULT NULL ,
	PRIMARY KEY (id)
);
