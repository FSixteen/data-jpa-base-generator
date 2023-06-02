CREATE TABLE IF NOT EXISTS `fsn_configurations_key` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `scope` varchar(36) COLLATE utf8mb4_bin NOT NULL DEFAULT 'DEFAULT' COMMENT '作用域',
  `code` varchar(128) COLLATE utf8mb4_bin NOT NULL COMMENT '键编码',
  `description` varchar(128) COLLATE utf8mb4_bin NOT NULL COMMENT '键描述',
  `default_value` varchar(512) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '键默认值',
  `deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '已删除标记',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后一次更新时间',
  `delete_time` datetime DEFAULT NULL COMMENT '删除时间',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_1887489b010` (`code`) USING BTREE COMMENT '唯一性主索引'
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='系统配置信息-系统参数编码';

CREATE TABLE IF NOT EXISTS `fsn_configurations_info` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `env` varchar(36) COLLATE utf8mb4_bin NOT NULL COMMENT '环境编码',
  `key` varchar(128) COLLATE utf8mb4_bin NOT NULL COMMENT '键编码',
  `val` text COLLATE utf8mb4_bin NOT NULL COMMENT '键值',
  `deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '已删除标记',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后一次更新时间',
  `delete_time` datetime DEFAULT NULL COMMENT '删除时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `idx_188748c26d6` (`env`,`key`,`deleted`,`delete_time`) USING BTREE COMMENT '唯一性索引',
  KEY `idx_18069ff9a81` (`env`) USING BTREE COMMENT '环境编码索引',
  KEY `idx_1886f900269` (`key`) USING BTREE COMMENT '系统参数编码索引',
  CONSTRAINT `fk_188749050f4` FOREIGN KEY (`env`) REFERENCES `fsn_environments_info` (`code`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_18874906cf6` FOREIGN KEY (`key`) REFERENCES `fsn_configurations_key` (`code`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='系统配置信息-系统参数';
