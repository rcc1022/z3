DROP TABLE IF EXISTS t_weixin_media;
CREATE TABLE t_weixin_media
(
  c_id CHAR(36) NOT NULL COMMENT '主键',
  c_key VARCHAR(255) NOT NULL COMMENT '微信key',
  c_app_id VARCHAR(255) NOT NULL COMMENT 'App ID',
  c_type VARCHAR(255) DEFAULT NULL COMMENT '类型：image-图片；voice-语音；video-视频；thumb-缩略图',
  c_name VARCHAR(255) DEFAULT NULL COMMENT '名称',
  c_media_id VARCHAR(255) DEFAULT NULL COMMENT '媒体ID',
  c_url VARCHAR(255) DEFAULT NULL COMMENT '微信URL',
  c_uri VARCHAR(255) DEFAULT NULL COMMENT '文件URI',
  c_time DATETIME NOT NULL COMMENT '时间',

  PRIMARY KEY pk(c_id) USING HASH,
  KEY k_key(c_key) USING HASH,
  KEY k_app_id(c_app_id) USING HASH,
  KEY k_time(c_time) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
