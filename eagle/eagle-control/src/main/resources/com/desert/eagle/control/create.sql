DROP TABLE IF EXISTS t_control;
CREATE TABLE t_control
(
  c_id CHAR(36) NOT NULL COMMENT '主键',
  c_mode INT DEFAULT 0 COMMENT '模式：0-单局；1-智能',
  c_type INT DEFAULT 0 COMMENT '类型：0-极速PC；1-极速赛车',
  c_win INT DEFAULT 0 COMMENT '赢取彩金',
  c_win_rate INT DEFAULT 0 COMMENT '赢取控率',
  c_to_win INT DEFAULT 0 COMMENT '待赢取',
  c_lose INT DEFAULT 0 COMMENT '发放彩金',
  c_lose_rate INT DEFAULT 0 COMMENT '发放控率',
  c_to_lose INT DEFAULT 0 COMMENT '待发放',

  PRIMARY KEY pk(c_id) USING HASH
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;