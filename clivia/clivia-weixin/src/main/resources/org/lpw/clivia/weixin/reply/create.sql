DROP TABLE IF EXISTS t_weixin_reply;
CREATE TABLE t_weixin_reply
(
  c_id CHAR(36) NOT NULL COMMENT '主键',
  c_key VARCHAR(255) NOT NULL COMMENT '引用KEY',
  c_sort INT DEFAULT 0 COMMENT '顺序',
  c_receive_type VARCHAR(255) DEFAULT NULL COMMENT '接收类型',
  c_receive_message VARCHAR(255) DEFAULT NULL COMMENT '接收消息',
  c_send_type VARCHAR(255) DEFAULT NULL COMMENT '发送类型',
  c_send_app_id VARCHAR(255) DEFAULT NULL COMMENT '小程序APP ID',
  c_send_message TEXT DEFAULT NULL COMMENT '发送消息',
  c_send_title VARCHAR(255) DEFAULT NULL COMMENT '发送标题',
  c_send_description VARCHAR(255) DEFAULT NULL COMMENT '发送描述',
  c_send_url TEXT DEFAULT NULL COMMENT '发送链接',
  c_send_picurl VARCHAR(255) DEFAULT NULL COMMENT '发送图片链接',
  c_state INT DEFAULT 0 COMMENT '状态：0-待使用；1-使用中',

  PRIMARY KEY pk(c_id) USING HASH,
  KEY k_key(c_key) USING HASH
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
