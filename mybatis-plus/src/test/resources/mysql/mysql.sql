/*
 Navicat MySQL Data Transfer

 Source Server         : localhost
 Source Server Version : 50714
 Source Host           : localhost
 Source Database       : mybatis-plus

 Target Server Version : 50714
 File Encoding         : utf-8

 Date: 10/24/2016 11:17:44 AM
*/

SET NAMES utf8;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
--  Table structure for `role`
-- ----------------------------
DROP TABLE IF EXISTS `role`;
CREATE TABLE `role` (
  `id` bigint(20) NOT NULL,
  `name` varchar(64) DEFAULT NULL,
  `sort` int(11) DEFAULT NULL,
  `description` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
--  Table structure for `test`
-- ----------------------------
DROP TABLE IF EXISTS `test`;
CREATE TABLE `test` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `type` varchar(255) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Table structure for not_pk
-- ----------------------------
DROP TABLE IF EXISTS `not_pk`;
CREATE TABLE `not_pk` (
  `uuid` varchar(64) NOT NULL,
  `type` varchar(64) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ----------------------------
--  Records of `test`
-- ----------------------------
BEGIN;
INSERT INTO `test` VALUES ('1', '11'), ('2', '22');
COMMIT;

-- ----------------------------
--  Table structure for `user`
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
  `test_id` bigint(20) NOT NULL COMMENT '主键ID',
  `name` varchar(30) COLLATE utf8_bin DEFAULT NULL COMMENT '名称',
  `age` int(11) DEFAULT NULL COMMENT '年龄',
  `test_type` int(11) DEFAULT '0' COMMENT '测试下划线字段命名类型',
  `role` bigint(20) DEFAULT NULL,
  `phone` varchar(64) COLLATE utf8_bin DEFAULT NULL,
  `desc` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `price` DECIMAL(10,2) NULL DEFAULT NULL,
  `version` INT(5) NULL DEFAULT NULL,
  PRIMARY KEY (`test_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='用户表';

SET FOREIGN_KEY_CHECKS = 1;
