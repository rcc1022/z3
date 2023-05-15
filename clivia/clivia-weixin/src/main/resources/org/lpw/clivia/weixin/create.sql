DROP TABLE IF EXISTS t_weixin;
CREATE TABLE t_weixin
(
  c_id CHAR(36) NOT NULL COMMENT '主键',
  c_key VARCHAR(255) NOT NULL COMMENT '引用key',
  c_name VARCHAR(255) DEFAULT NULL COMMENT '名称',
  c_app_id VARCHAR(255) DEFAULT NULL COMMENT 'APP ID',
  c_secret VARCHAR(255) DEFAULT NULL COMMENT '密钥',
  c_token VARCHAR(255) DEFAULT NULL COMMENT '验证Token',
  c_mch_id VARCHAR(255) DEFAULT NULL COMMENT '商户ID',
  c_mch_partner_id VARCHAR(255) DEFAULT NULL COMMENT '合作方ID',
  c_mch_key VARCHAR(255) DEFAULT NULL COMMENT '商户密钥',
  c_mch_serial_no VARCHAR(255) DEFAULT NULL COMMENT '商户证书序列号',
  c_mch_private_key TEXT DEFAULT NULL COMMENT '商户私钥',
  c_mch_key_v3 VARCHAR(255) DEFAULT NULL COMMENT '商户密钥V3',
  c_access_token VARCHAR(255) DEFAULT NULL COMMENT 'Access Token',
  c_jsapi_ticket VARCHAR(255) DEFAULT NULL COMMENT 'Jsapi Ticket',
  c_menu TEXT DEFAULT NULL COMMENT '菜单配置',
  c_time DATETIME DEFAULT NULL COMMENT '更新时间',

  PRIMARY KEY pk(c_id) USING HASH,
  UNIQUE KEY uk_key(c_key) USING HASH,
  UNIQUE KEY uk_app_id(c_app_id) USING HASH
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
