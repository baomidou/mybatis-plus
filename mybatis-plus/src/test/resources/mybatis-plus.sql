/*
 Navicat MySQL Data Transfer

 Source Server         : local
 Source Server Version : 50616
 Source Host           : localhost
 Source Database       : mybatis-plus

 Target Server Version : 50616
 File Encoding         : utf-8

 Date: 02/28/2016 13:50:48 PM
*/

SET NAMES utf8;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
--  Table structure for `user`
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
  `test_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `name` varchar(30) COLLATE utf8_bin NOT NULL COMMENT '名称',
  `age` int(11) NOT NULL COMMENT '年龄',
  `test_type` int(11) NOT NULL DEFAULT '0' COMMENT '测试下划线字段命名类型',
  PRIMARY KEY (`test_id`)
) ENGINE=MyISAM AUTO_INCREMENT=393377871298560003 DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='用户表';

-- ----------------------------
--  Records of `user`
-- ----------------------------
BEGIN;
INSERT INTO `user` VALUES ('1', 'test', '18', '0'), ('2', '6', '6', '0'), ('3', '1', '1', '0'), ('393377871298560001', '2', '2', '0'), ('393377871298560002', '3', '3', '0');
COMMIT;

SET FOREIGN_KEY_CHECKS = 1;
