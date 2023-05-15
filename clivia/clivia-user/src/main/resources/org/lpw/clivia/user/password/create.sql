DROP TABLE IF EXISTS t_user_password;
CREATE TABLE t_user_password
(
  c_id CHAR(36) NOT NULL COMMENT '主键',
  c_user CHAR(36) NOT NULL COMMENT '用户',
  c_type VARCHAR(255) NOT NULL COMMENT '类型',
  c_hash VARCHAR(255) NOT NULL COMMENT '密码哈希值',
  c_time DATETIME NOT NULL COMMENT '时间',

  PRIMARY KEY pk(c_id) USING HASH,
  UNIQUE KEY uk_user_type(c_user,c_type) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;