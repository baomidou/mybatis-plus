/*
 Navicat Premium Data Transfer

 Source Server         : mysql-admin
 Source Server Type    : MySQL
 Source Server Version : 50621
 Source Host           : localhost
 Source Database       : mybatis-plus

 Target Server Type    : MySQL
 Target Server Version : 50621
 File Encoding         : utf-8

 Date: 09/09/2016 16:38:46 PM
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
--  Table structure for `user`
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
  `test_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `name` varchar(30) COLLATE utf8_bin DEFAULT NULL COMMENT '名称',
  `age` int(11) DEFAULT NULL COMMENT '年龄',
  `test_type` int(11) DEFAULT '0' COMMENT '测试下划线字段命名类型',
  `role` bigint(20) DEFAULT NULL,
  `phone` varchar(64) COLLATE utf8_bin DEFAULT NULL,
  PRIMARY KEY (`test_id`)
) ENGINE=InnoDB AUTO_INCREMENT=774164658700161025 DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='用户表';

SET FOREIGN_KEY_CHECKS = 1;