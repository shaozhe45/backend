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
-- Table structure for m_order
-- ----------------------------
DROP TABLE IF EXISTS `m_order`;
CREATE TABLE `m_order` (
  `id` bigint(20) NOT NULL,
  `name` varchar(255) DEFAULT NULL COMMENT '名称',
  `education` varchar(255) DEFAULT NULL COMMENT '学历\n@InjectionField(api = "orderServiceImpl", method = DICTIONARY_ITEM_METHOD, dictType = DictionaryType.EDUCATION) RemoteData<String, String>',
  `nation` varchar(255) DEFAULT NULL COMMENT '民族\n@InjectionField(api = DICTIONARY_ITEM_FEIGN_CLASS, method = DICTIONARY_ITEM_METHOD, dictType = DictionaryType.NATION) RemoteData<String, String>\n',
  `org_id` bigint(20) DEFAULT NULL COMMENT '组织ID\n#c_core_org\n@InjectionField(api = ORG_ID_FEIGN_CLASS, method = ORG_ID_NAME_METHOD) RemoteData<Long, String>',
  `code` varchar(255) DEFAULT NULL COMMENT '编号',
  `create_time` datetime DEFAULT NULL,
  `create_user` bigint(20) DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  `update_user` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='订单(用于测试)';

-- ----------------------------
-- Table structure for m_product
-- ----------------------------
DROP TABLE IF EXISTS `m_product`;
CREATE TABLE `m_product` (
  `id` bigint(20) NOT NULL,
  `name` varchar(255) DEFAULT NULL COMMENT '名称',
  `stock` int(11) DEFAULT NULL COMMENT '库存',
  `create_time` datetime DEFAULT NULL,
  `create_user` bigint(20) DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  `update_user` bigint(20) DEFAULT NULL,
  `type_` text COMMENT '商品类型\n#ProductType{ordinary:普通;gift:赠品}',
  `type2` longtext COMMENT '商品类型2\n#{ordinary:普通;gift:赠品;}',
  `type3` varchar(255) DEFAULT NULL COMMENT '学历\n@InjectionField(api = DICTIONARY_ITEM_FEIGN_CLASS, method = DICTIONARY_ITEM_METHOD, dictType = DictionaryType.EDUCATION) RemoteData<String, String>\n',
  `status` bit(1) DEFAULT NULL COMMENT '状态',
  `test4` tinyint(10) DEFAULT NULL,
  `test5` date DEFAULT NULL COMMENT '时间',
  `test6` datetime DEFAULT NULL COMMENT '日期',
  `parent_id` bigint(20) DEFAULT NULL,
  `label` varchar(255) DEFAULT NULL COMMENT '名称',
  `sort_value` int(11) DEFAULT NULL,
  `test7` char(10) DEFAULT NULL COMMENT 'xxx\n@InjectionField(api = “userApi”, method = USER_ID_NAME_METHOD) RemoteData<Long, String>',
  `user_id` bigint(20) DEFAULT NULL COMMENT '用户\n@InjectionField(api = USER_ID_FEIGN_CLASS, method = USER_ID_NAME_METHOD) RemoteData<Long, String>',
  `org_id` bigint(20) DEFAULT NULL COMMENT '组织\n@InjectionField(api = ORG_ID_FEIGN_CLASS, method = "findOrgNameByIds") RemoteData<Long, String>',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品(用于测试)';

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
