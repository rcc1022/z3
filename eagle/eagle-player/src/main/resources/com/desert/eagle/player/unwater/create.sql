DROP TABLE IF EXISTS t_player_unwater;
CREATE TABLE t_player_unwater
(
  c_id CHAR(36) NOT NULL COMMENT '主键',
  c_player CHAR(36) DEFAULT NULL COMMENT '玩家',
  c_amount INT DEFAULT 0 COMMENT '金额',
  c_memo VARCHAR(255) DEFAULT NULL COMMENT '备注',
  c_time DATETIME NOT NULL COMMENT '时间',

  PRIMARY KEY pk(c_id) USING HASH,
  KEY k_time(c_time) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;