DROP TABLE IF EXISTS t_group;
CREATE TABLE t_group
(
  c_id CHAR(36) NOT NULL COMMENT '主键',
  c_type INT DEFAULT 0 COMMENT '类型：0-好友；1-群组',
  c_name VARCHAR(255) DEFAULT NULL COMMENT '名称',
  c_avatar VARCHAR(255) DEFAULT NULL COMMENT '头像',
  c_notice TEXT DEFAULT NULL COMMENT '公告',
  c_join INT DEFAULT 0 COMMENT '加入：0-正常；1-需审核；2-禁止',
  c_ban INT DEFAULT 0 COMMENT '禁言：0-不禁言；1-全禁言；2-普通成员',
  c_count INT DEFAULT 0 COMMENT '人数',
  c_time DATETIME DEFAULT NULL COMMENT '创建时间',

  PRIMARY KEY pk(c_id) USING HASH
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;