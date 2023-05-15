DROP TABLE IF EXISTS t_async;
CREATE TABLE t_async
(
  c_id CHAR(36) NOT NULL COMMENT '主键',
  c_key VARCHAR(255) NOT NULL COMMENT '引用KEY',
  c_parameter TEXT DEFAULT NULL COMMENT '参数',
  c_result TEXT DEFAULT NULL COMMENT '结果',
  c_state INT DEFAULT 0 COMMENT '状态：0-进行中；1-已完成；2-异常；3-超时',
  c_begin DATETIME DEFAULT NULL COMMENT '开始时间',
  c_finish DATETIME DEFAULT NULL COMMENT '结束时间',
  c_timeout DATETIME DEFAULT NULL COMMENT '超时时间',

  PRIMARY KEY pk(c_id) USING HASH,
  KEY k_key(c_key) USING HASH,
  KEY k_state(c_state) USING HASH
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
