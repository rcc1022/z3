DROP TABLE IF EXISTS t_bet;
CREATE TABLE t_bet
(
  c_id CHAR(36) NOT NULL COMMENT '主键',
  c_game VARCHAR(255) NOT NULL COMMENT '游戏',
  c_player CHAR(36) NOT NULL COMMENT '玩家',
  c_issue VARCHAR(255) DEFAULT NULL COMMENT '期号',
  c_type VARCHAR(255) DEFAULT NULL COMMENT '类型',
  c_item VARCHAR(255) DEFAULT NULL COMMENT '项目',
  c_subitem VARCHAR(255) DEFAULT NULL COMMENT '子项目',
  c_rate INT DEFAULT 0 COMMENT '赔率',
  c_amount INT DEFAULT 0 COMMENT '金额',
  c_water INT DEFAULT 0 COMMENT '返水',
  c_commission INT DEFAULT 0 COMMENT '上级佣金',
  c_profit INT DEFAULT 0 COMMENT '输赢',
  c_stop INT DEFAULT 0 COMMENT '追号停止',
  c_zhuihao VARCHAR(255) DEFAULT NULL COMMENT '追号编号',
  c_memo VARCHAR(255) DEFAULT NULL COMMENT '备注',
  c_open VARCHAR(255) DEFAULT NULL COMMENT '开奖结果',
  c_status INT DEFAULT 0 COMMENT '状态：0-未结算；1-已结算',
  c_robot INT DEFAULT 0 COMMENT '机器人：0-否；1-是',
  c_time DATETIME DEFAULT NULL COMMENT '时间',
  c_settle DATETIME DEFAULT NULL COMMENT '结算时间',

  PRIMARY KEY pk(c_id) USING HASH,
  KEY k_game(c_game) USING HASH,
  KEY k_player(c_player) USING HASH,
  KEY k_time(c_time) USING BTREE,
  KEY k_issue_status_robot(c_issue,c_status,c_robot) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;