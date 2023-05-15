DROP TABLE IF EXISTS t_message;
CREATE TABLE t_message
(
  c_id CHAR(36) NOT NULL COMMENT '主键',
  c_game CHAR(36) NOT NULL COMMENT '游戏',
  c_player CHAR(36) DEFAULT NULL COMMENT '玩家',
  c_type INT DEFAULT 0 COMMENT '类型：0-开盘；1-封盘；2-下注；3-下注列表',
  c_content TEXT DEFAULT NULL COMMENT '内容',
  c_time BIGINT DEFAULT 0 COMMENT '时间',

  PRIMARY KEY pk(c_id) USING HASH,
  KEY k_game_time(c_game,c_time) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;