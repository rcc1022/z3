DROP TABLE IF EXISTS t_weixin_qrcode;
CREATE TABLE t_weixin_qrcode
(
  c_id CHAR(36) NOT NULL COMMENT '主键',
  c_key VARCHAR(255) NOT NULL COMMENT '微信key',
  c_app_id VARCHAR(255) NOT NULL COMMENT 'App ID',
  c_user CHAR(36) NOT NULL COMMENT '用户ID',
  c_name VARCHAR(255) DEFAULT NULL COMMENT '名称',
  c_scene VARCHAR(64) DEFAULT NULL COMMENT '场景',
  c_ticket VARCHAR(255) DEFAULT NULL COMMENT 'Ticket',
  c_url VARCHAR(255) DEFAULT NULL COMMENT '微信URL',
  c_time DATETIME NOT NULL COMMENT '时间',

  PRIMARY KEY pk(c_id) USING HASH,
  KEY k_key(c_key) USING HASH,
  KEY k_app_id(c_app_id) USING HASH,
  KEY k_user(c_user) USING HASH,
  KEY k_time(c_time) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
