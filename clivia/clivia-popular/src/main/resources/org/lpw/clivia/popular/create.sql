DROP TABLE IF EXISTS t_popular;
CREATE TABLE t_popular
(
  c_id CHAR(36) NOT NULL COMMENT '主键',
  c_key VARCHAR(255) NOT NULL COMMENT '引用key',
  c_value VARCHAR(255) NOT NULL COMMENT '值',
  c_count INT DEFAULT 0 COMMENT '次数',
  c_state INT DEFAULT 0 COMMENT '状态：0-正常；1-禁用',

  PRIMARY KEY pk(c_id) USING HASH,
  UNIQUE KEY uk_key_value(c_key,c_value) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
