DROP TABLE IF EXISTS t_game;
CREATE TABLE t_game
(
  c_id CHAR(36) NOT NULL COMMENT '主键',
  c_sort INT DEFAULT 0 COMMENT '顺序',
  c_name VARCHAR(255) DEFAULT NULL COMMENT '名称',
  c_cover VARCHAR(255) DEFAULT NULL COMMENT '封面图',
  c_type INT DEFAULT 0 COMMENT '类型：0-加拿大PC2.0；1-加拿大PC2.8；2-加拿大PC3.2；3-极速PC·私2.0；4-极速PC·私2.8；5-极速PC·私3.2；6-幸运飞艇；7-极速赛车·私；8-澳洲10；9-极速赛车；10-澳洲幸运5；11-皇冠足球',
  c_water INT DEFAULT 0 COMMENT '返水比例',
  c_lose INT DEFAULT 0 COMMENT '亏损返水',
  c_commission INT DEFAULT 0 COMMENT '上级佣金',
  c_min INT DEFAULT 0 COMMENT '最小投注额',
  c_max INT DEFAULT 0 COMMENT '最大投注额',
  c_total INT DEFAULT 0 COMMENT '当期最大投注总额',
  c_close INT DEFAULT 0 COMMENT '封盘时间：秒',
  c_control INT DEFAULT 0 COMMENT '控奖：0-否；1-是',
  c_rate INT DEFAULT 0 COMMENT '控率',
  c_robot INT DEFAULT 0 COMMENT '机器人数',
  c_on INT DEFAULT 0 COMMENT '开启：0-关；1-开',
  c_rule TEXT DEFAULT NULL COMMENT '玩法规则',

  PRIMARY KEY pk(c_id) USING HASH
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;