CREATE TABLE user (
  id INTEGER AUTO_INCREMENT,
  name varchar(25) NOT NULL,
  PRIMARY KEY(id)
);

insert into user (name) values ('read_1'), ('read_2'), ('read_3');
