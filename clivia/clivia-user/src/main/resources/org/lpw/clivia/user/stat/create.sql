DROP TABLE IF EXISTS t_user_stat;
CREATE TABLE t_user_stat
(
  c_id CHAR(36) NOT NULL COMMENT '主键',
  c_date DATE NOT NULL COMMENT '日期',
  c_count INT DEFAULT 0 COMMENT '总数',
  c_register INT DEFAULT 0 COMMENT '注册数',
  c_online INT DEFAULT 0 COMMENT '在线数',

  PRIMARY KEY pk(c_id) USING HASH,
  UNIQUE KEY uk_date(c_date) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
