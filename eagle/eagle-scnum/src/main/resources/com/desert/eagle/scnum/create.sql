DROP TABLE IF EXISTS t_scnum;
CREATE TABLE t_scnum
(
  c_id CHAR(36) NOT NULL COMMENT '主键',
  c_type INT DEFAULT 0 COMMENT '类型：0-幸运飞艇；1-极速赛车·私；2-澳洲10；3-极速赛车',
  c_issue BIGINT DEFAULT 0 COMMENT '期号',
  c_num1 INT DEFAULT 0 COMMENT '号码1',
  c_num2 INT DEFAULT 0 COMMENT '号码2',
  c_num3 INT DEFAULT 0 COMMENT '号码3',
  c_num4 INT DEFAULT 0 COMMENT '号码4',
  c_num5 INT DEFAULT 0 COMMENT '号码5',
  c_num6 INT DEFAULT 0 COMMENT '号码6',
  c_num7 INT DEFAULT 0 COMMENT '号码7',
  c_num8 INT DEFAULT 0 COMMENT '号码8',
  c_num9 INT DEFAULT 0 COMMENT '号码9',
  c_num10 INT DEFAULT 0 COMMENT '号码10',
  c_sum INT DEFAULT 0 COMMENT '冠亚和',
  c_status INT DEFAULT 0 COMMENT '状态：0-预设；1-已开奖',
  c_time DATETIME DEFAULT NULL COMMENT '时间',

  PRIMARY KEY pk(c_id) USING HASH,
  UNIQUE KEY uk_type_issue(c_type,c_issue) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;