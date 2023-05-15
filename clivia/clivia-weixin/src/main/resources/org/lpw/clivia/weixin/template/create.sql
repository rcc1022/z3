DROP TABLE IF EXISTS t_weixin_template;
CREATE TABLE t_weixin_template
(
  c_id CHAR(36) NOT NULL COMMENT '主键',
  c_key VARCHAR(255) NOT NULL COMMENT '引用key',
  c_weixin_key VARCHAR(255) NOT NULL COMMENT '微信key',
  c_type INT DEFAULT 0 COMMENT '类型：0-公众号；1-小程序',
  c_template_id VARCHAR(255) DEFAULT NULL COMMENT '模板ID',
  c_url TEXT DEFAULT NULL COMMENT '跳转URL',
  c_page VARCHAR(255) DEFAULT NULL COMMENT '小程序页面',
  c_mini_app_id VARCHAR(255) DEFAULT NULL COMMENT '小程序APP ID',
  c_color VARCHAR(255) DEFAULT NULL COMMENT '字体颜色',
  c_keyword VARCHAR(255) DEFAULT NULL COMMENT '放大关键词',
  c_state INT DEFAULT 0 COMMENT '状态：0-待审核；1-已上线',

  PRIMARY KEY pk(c_id) USING HASH,
  UNIQUE KEY uk_key(c_key) USING HASH
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
