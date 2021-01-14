CREATE TABLE IF NOT EXISTS sharding_order_01
(
    order_id     BIGINT(20)  NOT NULL AUTO_INCREMENT,
    subject   VARCHAR(30) NULL DEFAULT NULL,
    create_time  DATETIME      NULL,
    PRIMARY KEY (order_id)
);

CREATE TABLE IF NOT EXISTS sharding_order_02
(
    order_id     BIGINT(20)  NOT NULL AUTO_INCREMENT,
    subject   VARCHAR(30) NULL DEFAULT NULL,
    create_time  DATETIME      NULL,
    PRIMARY KEY (order_id)
);

CREATE TABLE IF NOT EXISTS sharding_order_03
(
    order_id     BIGINT(20)  NOT NULL AUTO_INCREMENT,
    subject   VARCHAR(30) NULL DEFAULT NULL,
    create_time  DATETIME      NULL,
    PRIMARY KEY (order_id)
);
