CREATE TABLE IF NOT EXISTS `fsn_dictionaries_envs` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `code` varchar(36) COLLATE utf8mb4_bin NOT NULL COMMENT '环境编码',
  `description` varchar(128) COLLATE utf8mb4_bin NOT NULL COMMENT '环境描述',
  `enabled` tinyint(1) NOT NULL DEFAULT '0' COMMENT '启用状态',
  `deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '已删除标记',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后一次更新时间',
  `delete_time` datetime DEFAULT NULL COMMENT '删除时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `idx_1886f7b2435` (`code`) USING BTREE COMMENT '唯一性主索引'
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='系统字典信息-系统字典环境编码';

CREATE TABLE IF NOT EXISTS `fsn_dictionaries_classify` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `scope` varchar(36) COLLATE utf8mb4_bin NOT NULL DEFAULT 'DEFAULT' COMMENT '作用域',
  `classify` varchar(36) COLLATE utf8mb4_bin NOT NULL COMMENT '字典分类',
  `description` varchar(128) COLLATE utf8mb4_bin NOT NULL COMMENT '字典描述',
  `default_key` varchar(32) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '字典默认键',
  `deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '已删除标记',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后一次更新时间',
  `delete_time` datetime DEFAULT NULL COMMENT '删除时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `idx_1886f7aed00` (`classify`) USING BTREE COMMENT '唯一性主索引'
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='系统字典信息-系统字典分类编码';

CREATE TABLE IF NOT EXISTS `fsn_dictionaries_info` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `env` varchar(36) COLLATE utf8mb4_bin NOT NULL COMMENT '字典环境编码',
  `classify` varchar(36) COLLATE utf8mb4_bin NOT NULL COMMENT '字典分类编码',
  `dict_key` varchar(128) COLLATE utf8mb4_bin NOT NULL COMMENT '字典健',
  `dict_value` varchar(512) COLLATE utf8mb4_bin NOT NULL COMMENT '字典值',
  `enabled` tinyint(1) NOT NULL DEFAULT '1' COMMENT '启用状态',
  `ordered` int(11) NOT NULL DEFAULT '0' COMMENT '排序',
  `description` varchar(128) COLLATE utf8mb4_bin NOT NULL COMMENT '字典描述',
  `deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '已删除标记',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后一次更新时间',
  `delete_time` datetime DEFAULT NULL COMMENT '删除时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `idx_1886f8fb164` (`env`,`classify`,`dict_key`,`dict_value`) USING BTREE COMMENT '唯一性索引',
  KEY `idx_18069ff9a81` (`classify`) USING BTREE COMMENT '字典分类编码索引',
  KEY `idx_1886f900269` (`env`) USING BTREE COMMENT '字典环境编码索引',
  CONSTRAINT `fk_1886fad65c3` FOREIGN KEY (`env`) REFERENCES `fsn_dictionaries_envs` (`code`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_1886faf38d4` FOREIGN KEY (`classify`) REFERENCES `fsn_dictionaries_classify` (`classify`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='系统字典信息-系统字典';

INSERT INTO `fsn_dictionaries_envs` ( `code`, `description`, `enabled`, `deleted`, `create_time`, `update_time`, `delete_time` )
SELECT 'DEFAULT', '默认环境', 0, 0, NOW(), NOW(), NULL FROM DUAL
WHERE NOT EXISTS ( SELECT `id` FROM `fsn_dictionaries_envs` WHERE `code` = 'DEFAULT' AND `deleted` = false AND `delete_time` IS NULL );
