DROP TABLE IF EXISTS t_group_friend;
CREATE TABLE t_group_friend
(
  c_id CHAR(36) NOT NULL COMMENT '主键',
  c_user CHAR(36) NOT NULL COMMENT '用户',
  c_proposer CHAR(36) NOT NULL COMMENT '申请人',
  c_memo VARCHAR(255) DEFAULT NULL COMMENT '备注',
  c_state INT DEFAULT 0 COMMENT '状态：0-待处理；1-已通过；2-已拒绝；3-已过期',
  c_time DATETIME NOT NULL COMMENT '时间',

  PRIMARY KEY pk(c_id) USING HASH,
  UNIQUE KEY uk_user_proposer(c_user,c_proposer) USING BTREE,
  KEY k_user_time(c_user,c_time) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;