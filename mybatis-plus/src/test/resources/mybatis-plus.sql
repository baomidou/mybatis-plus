/*
Navicat MySQL Data Transfer

Source Server         : localhost
Source Server Version : 50615
Source Host           : localhost:3306
Source Database       : mybatis-plus

Target Server Type    : MYSQL
Target Server Version : 50615
File Encoding         : 65001

Date: 2016-02-29 18:03:37
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for `user`
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
  `test_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `name` varchar(30) COLLATE utf8_bin NOT NULL COMMENT '名称',
  `age` int(11) NOT NULL COMMENT '年龄',
  `test_type` int(11) NOT NULL DEFAULT '0' COMMENT '测试下划线字段命名类型',
  PRIMARY KEY (`test_id`)
) ENGINE=MyISAM AUTO_INCREMENT=398402012225470465 DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='用户表';

-- ----------------------------
-- Records of user
-- ----------------------------
