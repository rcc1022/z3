DROP TABLE IF EXISTS t_player_withdraw;
CREATE TABLE t_player_withdraw
(
  c_id CHAR(36) NOT NULL COMMENT '主键',
  c_player CHAR(36) NOT NULL COMMENT '玩家',
  c_type INT DEFAULT 0 COMMENT '类型：0-微信支付；1-支付宝',
  c_amount INT DEFAULT 0 COMMENT '金额',
  c_status INT DEFAULT 0 COMMENT '状态：0-待审核；1-已通过；2-已拒绝',
  c_check INT DEFAULT 0 COMMENT '审核：0-否；1-是',
  c_submit DATETIME DEFAULT NULL COMMENT '提交时间',
  c_audit DATETIME DEFAULT NULL COMMENT '审核时间',
  c_auditor CHAR(36) NOT NULL COMMENT '审核/操作人',

  PRIMARY KEY pk(c_id) USING HASH,
  KEY k_player(c_player) USING HASH,
  KEY k_submit(c_submit) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;