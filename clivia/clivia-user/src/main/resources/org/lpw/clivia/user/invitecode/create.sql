DROP TABLE IF EXISTS t_user_invitecode;
CREATE TABLE t_user_invitecode
(
  c_id CHAR(36) NOT NULL COMMENT '主键',
  c_user CHAR(36) DEFAULT NULL COMMENT '用户',
  c_batch VARCHAR(255) NOT NULL COMMENT '批次号',
  c_code VARCHAR(255) NOT NULL COMMENT '码',
  c_time DATETIME DEFAULT NULL COMMENT '注册时间',

  PRIMARY KEY pk(c_id) USING HASH,
  KEY k_batch(c_batch) USING BTREE,
  UNIQUE KEY uk_code(c_code) USING HASH
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;