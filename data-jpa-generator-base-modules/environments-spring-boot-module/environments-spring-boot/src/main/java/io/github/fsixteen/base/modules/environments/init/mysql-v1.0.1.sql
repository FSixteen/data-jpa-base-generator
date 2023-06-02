CREATE TABLE IF NOT EXISTS `fsn_versions_info` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `code` varchar(64) COLLATE utf8mb4_bin NOT NULL COMMENT '模块编码',
  `description` varchar(128) COLLATE utf8mb4_bin NOT NULL COMMENT '模块描述',
  `pre_version` varchar(64) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '模块上一版本',
  `curr_version` varchar(64) COLLATE utf8mb4_bin NOT NULL DEFAULT 'unknown' COMMENT '模块版本',
  `post_version` varchar(64) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '模块下一版本',
  `version_date` bigint(20) NOT NULL DEFAULT '0' COMMENT '模块发布日期',
  `deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '已删除标记',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后一次更新时间',
  `delete_time` datetime DEFAULT NULL COMMENT '删除时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `idx_188572aa166` (`code`,`curr_version`) USING BTREE COMMENT '唯一性主索引'
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='系统配置信息-模块版本信息';

CREATE TABLE IF NOT EXISTS `fsn_environments_info` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `code` varchar(36) COLLATE utf8mb4_bin NOT NULL COMMENT '环境编码',
  `description` varchar(128) COLLATE utf8mb4_bin NOT NULL COMMENT '环境描述',
  `enabled` tinyint(1) NOT NULL DEFAULT '0' COMMENT '启用状态',
  `deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '已删除标记',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后一次更新时间',
  `delete_time` datetime DEFAULT NULL COMMENT '删除时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `idx_18837e9ca94` (`code`) USING BTREE COMMENT '唯一性主索引'
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='系统配置信息-系统环境编码';

INSERT INTO `fsn_versions_info`(`code`, `description`, `pre_version`, `curr_version`, `post_version`, `version_date`, `deleted`, `create_time`, `update_time`, `delete_time`) 
SELECT 'FSN_VERSIONS_INFO', '系统配置信息-模块版本信息', 'unknown', '1.0.1', 'unknown', '1685376000000', 0, NOW(), NOW(), NULL FROM DUAL
WHERE NOT EXISTS ( SELECT `id` FROM `fsn_versions_info` WHERE `code` = 'FSN_VERSIONS_INFO' AND `curr_version` = '1.0.1' AND `deleted` = false AND `delete_time` IS NULL ); 

INSERT INTO `fsn_versions_info`(`code`, `description`, `pre_version`, `curr_version`, `post_version`, `version_date`, `deleted`, `create_time`, `update_time`, `delete_time`) 
SELECT 'FSN_ENVIRONMENTS_INFO', '系统配置信息-系统环境编码', 'unknown', '1.0.1', 'unknown', '1685376000000', 0, NOW(), NOW(), NULL FROM DUAL
WHERE NOT EXISTS ( SELECT `id` FROM `fsn_versions_info` WHERE `code` = 'FSN_ENVIRONMENTS_INFO' AND `curr_version` = '1.0.1' AND `deleted` = false AND `delete_time` IS NULL ); 

INSERT INTO `fsn_environments_info` ( `code`, `description`, `enabled`, `deleted`, `create_time`, `update_time`, `delete_time` )
SELECT 'DEV', '开发环境', 0, 0, NOW(), NOW(), NULL FROM DUAL
WHERE NOT EXISTS ( SELECT `id` FROM `fsn_environments_info` WHERE `code` = 'DEV' AND `deleted` = false AND `delete_time` IS NULL ); 

INSERT INTO `fsn_environments_info` ( `code`, `description`, `enabled`, `deleted`, `create_time`, `update_time`, `delete_time` )
SELECT 'TEST', '测试环境', 0, 0, NOW(), NOW(), NULL FROM DUAL
WHERE NOT EXISTS ( SELECT `id` FROM `fsn_environments_info` WHERE `code` = 'TEST' AND `deleted` = false AND `delete_time` IS NULL ); 

INSERT INTO `fsn_environments_info` ( `code`, `description`, `enabled`, `deleted`, `create_time`, `update_time`, `delete_time` )
SELECT 'DEMO', '演示环境', 0, 0, NOW(), NOW(), NULL FROM DUAL
WHERE NOT EXISTS ( SELECT `id` FROM `fsn_environments_info` WHERE `code` = 'DEMO' AND `deleted` = false AND `delete_time` IS NULL ); 

INSERT INTO `fsn_environments_info` ( `code`, `description`, `enabled`, `deleted`, `create_time`, `update_time`, `delete_time` )
SELECT 'PROD', '生产环境', 1, 0, NOW(), NOW(), NULL FROM DUAL
WHERE NOT EXISTS ( SELECT `id` FROM `fsn_environments_info` WHERE `code` = 'PROD' AND `deleted` = false AND `delete_time` IS NULL ); 
