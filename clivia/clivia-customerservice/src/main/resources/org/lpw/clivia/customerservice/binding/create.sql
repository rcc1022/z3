DROP TABLE IF EXISTS t_customerservice_binding;
CREATE TABLE t_customerservice_binding
(
  c_id CHAR(36) NOT NULL COMMENT '主键',
  c_user CHAR(36) NOT NULL COMMENT '用户',
  c_type VARCHAR(255) NOT NULL COMMENT '类型',
  c_customerservice CHAR(36) NOT NULL COMMENT '客服',
  c_state INT DEFAULT 0 COMMENT '状态：0-停用；1-可用',
  c_time DATETIME NOT NULL COMMENT '时间',

  PRIMARY KEY pk(c_id) USING HASH,
  KEY k_user_type_state_time(c_user,c_type,c_state,c_time) USING BTREE,
  KEY k_customerservice(c_customerservice) USING HASH
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
