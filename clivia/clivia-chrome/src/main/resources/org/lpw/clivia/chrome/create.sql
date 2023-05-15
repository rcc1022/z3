DROP TABLE IF EXISTS t_chrome;
CREATE TABLE t_chrome
(
  c_id CHAR(36) NOT NULL COMMENT '主键',
  c_key VARCHAR(255) NOT NULL COMMENT '引用key',
  c_name VARCHAR(255) DEFAULT NULL COMMENT '名称',
  c_x INT DEFAULT 0 COMMENT '图片X位置',
  c_y INT DEFAULT 0 COMMENT '图片Y位置',
  c_width INT DEFAULT 0 COMMENT '宽度',
  c_height INT DEFAULT 0 COMMENT '高度',
  c_pages VARCHAR(255) DEFAULT NULL COMMENT '页面集',
  c_wait INT DEFAULT 0 COMMENT '等待时长，单位：秒',
  c_filename VARCHAR(255) DEFAULT NULL COMMENT '文件名',

  PRIMARY KEY pk(c_id) USING HASH,
  UNIQUE KEY uk_key(c_key) USING HASH
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
