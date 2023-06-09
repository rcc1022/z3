DROP TABLE IF EXISTS t_push;
CREATE TABLE t_push
(
  c_id CHAR(36) NOT NULL COMMENT '主键',
  c_scene VARCHAR(255) DEFAULT NULL COMMENT '场景',
  c_sender VARCHAR(255) DEFAULT NULL COMMENT '推送器',
  c_name VARCHAR(255) DEFAULT NULL COMMENT '名称',
  c_config TEXT DEFAULT NULL COMMENT '配置',
  c_cert TEXT DEFAULT NULL COMMENT '证书',
  c_state INT DEFAULT 0 COMMENT '状态：0-停用；1-可用',
  c_time DATETIME DEFAULT NULL COMMENT '时间',

  PRIMARY KEY pk(c_id) USING HASH
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
