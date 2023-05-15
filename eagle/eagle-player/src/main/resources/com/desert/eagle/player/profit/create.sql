DROP TABLE IF EXISTS t_player_profit;
CREATE TABLE t_player_profit
(
  c_id CHAR(36) NOT NULL COMMENT '主键',
  c_player CHAR(36) NOT NULL COMMENT '玩家',
  c_game CHAR(36) NOT NULL COMMENT '游戏',
  c_date DATE NOT NULL COMMENT '日期',
  c_deposit INT DEFAULT 0 COMMENT '充值',
  c_withdraw INT DEFAULT 0 COMMENT '提现',
  c_count INT DEFAULT 0 COMMENT '下注笔数',
  c_amount INT DEFAULT 0 COMMENT '下注总额',
  c_water INT DEFAULT 0 COMMENT '返水总额',
  c_water0 INT DEFAULT 0 COMMENT '待返水',
  c_water2 INT DEFAULT 0 COMMENT '拒绝返水',
  c_water_lose INT DEFAULT 0 COMMENT '亏损返水',
  c_profit INT DEFAULT 0 COMMENT '输赢',
  c_commission INT DEFAULT 0 COMMENT '佣金',
  c_status INT DEFAULT 0 COMMENT '状态：0-待结算；1-已结算',

  PRIMARY KEY pk(c_id) USING HASH
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;