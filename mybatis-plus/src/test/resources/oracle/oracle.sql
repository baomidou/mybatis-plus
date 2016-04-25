create table TEST_USER
(
  test_id   VARCHAR2(32) not null,
  name      VARCHAR2(30),
  age       NUMBER,
  test_type NUMBER
)

alter table TEST_USER
  add constraint PK_TEST_USER primary key (TEST_ID)
  using index 
  tablespace MP_DATA
  pctfree 10
  initrans 2
  maxtrans 255
  storage
  (
    initial 64K
    next 1M
    minextents 1
    maxextents unlimited
  );
