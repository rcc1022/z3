DROP TABLE IF EXISTS t_pcnum;
CREATE TABLE t_pcnum
(
  c_id CHAR(36) NOT NULL COMMENT '主键',
  c_type INT DEFAULT 0 COMMENT '类型：0-加拿大；1-极速·私',
  c_issue BIGINT DEFAULT 0 COMMENT '期号',
  c_num1 INT DEFAULT 0 COMMENT '号码1',
  c_num2 INT DEFAULT 0 COMMENT '号码2',
  c_num3 INT DEFAULT 0 COMMENT '号码3',
  c_sum INT DEFAULT 0 COMMENT '和',
  c_status INT DEFAULT 0 COMMENT '状态：0-预设；1-已开奖',
  c_time DATETIME DEFAULT NULL COMMENT '时间',

  PRIMARY KEY pk(c_id) USING HASH,
  UNIQUE KEY uk_type_issue(c_type,c_issue) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;