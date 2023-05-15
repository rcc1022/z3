DROP TABLE IF EXISTS t_player_commission;
CREATE TABLE t_player_commission
(
  c_id CHAR(36) NOT NULL COMMENT '主键',
  c_player CHAR(36) DEFAULT NULL COMMENT '玩家',
  c_amount INT DEFAULT 0 COMMENT '金额',
  c_status INT DEFAULT 0 COMMENT '状态：0-待审核；1-已通过；2-已拒绝',
  c_submit DATETIME NOT NULL COMMENT '提交时间',
  c_audit DATETIME DEFAULT NULL COMMENT '审核时间',

  PRIMARY KEY pk(c_id) USING HASH,
  KEY k_submit(c_submit) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;