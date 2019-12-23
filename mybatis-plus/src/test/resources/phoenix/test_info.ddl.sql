-- DROP SCHEMA TEST;
-- CREATE SCHEMA TEST;
USE TEST;

DROP TABLE IF EXISTS TEST_INFO;
CREATE TABLE TEST_INFO (
  id INTEGER NOT NULL PRIMARY KEY,
  name VARCHAR,
  phone VARCHAR,
  position VARCHAR,
  department VARCHAR,
  company VARCHAR,
  file_name VARCHAR,
  pos_dep_com VARCHAR,
  gmt_updated DATE,
  gmt_create DATE
);
