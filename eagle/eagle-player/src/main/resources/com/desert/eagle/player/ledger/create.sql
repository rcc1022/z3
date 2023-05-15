DROP TABLE IF EXISTS t_player_ledger;
CREATE TABLE t_player_ledger
(
  c_id CHAR(36) NOT NULL COMMENT '主键',
  c_player CHAR(36) NOT NULL COMMENT '玩家',
  c_type VARCHAR(255) DEFAULT NULL COMMENT '类型',
  c_balance0 INT DEFAULT 0 COMMENT '变动前余额',
  c_amount INT DEFAULT 0 COMMENT '金额',
  c_balance INT DEFAULT 0 COMMENT '变动后余额',
  c_memo TEXT DEFAULT NULL COMMENT '备注',
  c_time DATETIME DEFAULT NULL COMMENT '时间',
  c_timestamp BIGINT DEFAULT 0 COMMENT '时间戳',

  PRIMARY KEY pk(c_id) USING HASH,
  KEY k_player_time(c_player,c_time) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;