/*
 Navicat Premium Data Transfer

 Source Server         : 127.0.0.1
 Source Server Type    : MySQL
 Source Server Version : 50722
 Source Host           : 127.0.0.1:3306
 Source Schema         : changqing_base_0000

 Target Server Type    : MySQL
 Target Server Version : 50722
 File Encoding         : 65001

 Date: 12/07/2020 23:49:10
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;
-- ----------------------------
-- Table structure for g_block_list
-- ----------------------------
DROP TABLE IF EXISTS `g_block_list`;
CREATE TABLE `g_block_list` (
  `id` bigint(20) NOT NULL COMMENT 'ID',
  `ip` varchar(20) DEFAULT '' COMMENT '黑名单ip',
  `request_uri` varchar(255) DEFAULT '' COMMENT '请求URI',
  `request_method` varchar(10) DEFAULT 'ALL' COMMENT '请求方法\n如果为ALL则表示对所有方法生效',
  `limit_start` varchar(8) DEFAULT '' COMMENT '限制时间起',
  `limit_end` varchar(8) DEFAULT '' COMMENT '限制时间止',
  `status` bit(1) DEFAULT b'1' COMMENT '状态',
  `create_time` datetime DEFAULT NULL,
  `create_user` bigint(20) DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  `update_user` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  FULLTEXT KEY `FU_PATH` (`limit_start`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='黑名单\n';

-- ----------------------------
-- Table structure for g_rate_limiter
-- ----------------------------
DROP TABLE IF EXISTS `g_rate_limiter`;
CREATE TABLE `g_rate_limiter` (
  `id` bigint(20) NOT NULL COMMENT 'ID',
  `count` int(10) DEFAULT '0' COMMENT '次数',
  `request_uri` varchar(255) DEFAULT '' COMMENT '请求URI',
  `request_method` varchar(10) DEFAULT 'ALL' COMMENT '请求方法\n如果为ALL则表示对所有方法生效',
  `limit_start` varchar(8) DEFAULT '' COMMENT '限制时间起',
  `limit_end` varchar(8) DEFAULT '' COMMENT '限制时间止',
  `status` bit(1) DEFAULT b'1' COMMENT '状态',
  `interval_sec` bigint(20) DEFAULT '0' COMMENT '时间窗口',
  `create_time` datetime DEFAULT NULL,
  `create_user` bigint(20) DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  `update_user` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  FULLTEXT KEY `FU_PATH` (`limit_start`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='限流';

-- ----------------------------
-- Table structure for undo_log
-- ----------------------------
DROP TABLE IF EXISTS `undo_log`;
CREATE TABLE `undo_log` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'increment id',
  `branch_id` bigint(20) NOT NULL COMMENT 'branch transaction id',
  `xid` varchar(100) NOT NULL COMMENT 'global transaction id',
  `context` varchar(128) NOT NULL COMMENT 'undo_log context,such as serialization',
  `rollback_info` longblob NOT NULL COMMENT 'rollback info',
  `log_status` int(11) NOT NULL COMMENT '0:normal status,1:defense status',
  `log_created` datetime NOT NULL COMMENT 'create datetime',
  `log_modified` datetime NOT NULL COMMENT 'modify datetime',
  PRIMARY KEY (`id`),
  UNIQUE KEY `ux_undo_log` (`xid`,`branch_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='AT transaction mode undo table';

SET FOREIGN_KEY_CHECKS = 1;
