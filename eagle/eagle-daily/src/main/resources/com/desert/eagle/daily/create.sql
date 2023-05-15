DROP TABLE IF EXISTS t_daily;
CREATE TABLE t_daily
(
  c_id CHAR(36) NOT NULL COMMENT '主键',
  c_date DATE NOT NULL COMMENT '日期',
  c_game CHAR(36) DEFAULT NULL COMMENT '游戏',
  c_player INT DEFAULT 0 COMMENT '玩家数',
  c_count INT DEFAULT 0 COMMENT '下注笔数',
  c_bet INT DEFAULT 0 COMMENT '下注额',
  c_profit INT DEFAULT 0 COMMENT '输赢额',
  c_water INT DEFAULT 0 COMMENT '已返水',
  c_water0 INT DEFAULT 0 COMMENT '待返水',
  c_commission INT DEFAULT 0 COMMENT '佣金',
  c_gain INT DEFAULT 0 COMMENT '站点赢利',
  c_deposit INT DEFAULT 0 COMMENT '充值',
  c_gift INT DEFAULT 0 COMMENT '补单充值',
  c_withdraw INT DEFAULT 0 COMMENT '提现',
  c_register INT DEFAULT 0 COMMENT '注册数',

  PRIMARY KEY pk(c_id) USING HASH,
  KEY k_date(c_date) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;