DROP TABLE IF EXISTS t_player_brokerage;
CREATE TABLE t_player_brokerage
(
  c_id CHAR(36) NOT NULL COMMENT '主键',
  c_player CHAR(36) NOT NULL COMMENT '玩家',
  c_date DATE NOT NULL COMMENT '日期',
  c_amount INT DEFAULT 0 COMMENT '金额',

  PRIMARY KEY pk(c_id) USING HASH,
  UNIQUE KEY uk_date_player(c_date,c_player) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;