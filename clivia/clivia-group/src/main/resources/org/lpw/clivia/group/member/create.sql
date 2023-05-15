DROP TABLE IF EXISTS t_group_member;
CREATE TABLE t_group_member
(
  c_id CHAR(36) NOT NULL COMMENT '主键',
  c_group CHAR(36) NOT NULL COMMENT '群组',
  c_user CHAR(36) NOT NULL COMMENT '用户',
  c_type INT DEFAULT 0 COMMENT '类型：0-好友；1-群组',
  c_grade INT DEFAULT 0 COMMENT '等级：0-普通；1-管理员；2-群主',
  c_memo VARCHAR(255) DEFAULT NULL COMMENT '备注',
  c_state INT DEFAULT 0 COMMENT '状态：0-正常；1-禁言；2-黑名单',
  c_time DATETIME DEFAULT NULL COMMENT '时间',

  PRIMARY KEY pk(c_id) USING HASH,
  KEY k_group(c_group) USING HASH,
  UNIQUE KEY uk_user_group(c_user,c_group) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;