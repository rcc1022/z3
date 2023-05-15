DROP TABLE IF EXISTS t_user_upload;
CREATE TABLE t_user_upload
(
  c_id CHAR(36) NOT NULL COMMENT '主键',
  c_user CHAR(36) NOT NULL COMMENT '用户',
  c_name VARCHAR(255) DEFAULT NULL COMMENT '上传名',
  c_content_type VARCHAR(255) DEFAULT NULL COMMENT '文件类型',
  c_filename VARCHAR(255) DEFAULT NULL COMMENT '文件名',
  c_uri VARCHAR(255) DEFAULT NULL COMMENT '存储URI',
  c_length INT DEFAULT 0 COMMENT '文件大小',
  c_time DATETIME NOT NULL COMMENT '时间',

  PRIMARY KEY pk(c_id) USING HASH,
  KEY k_user(c_user) USING HASH,
  KEY k_user_time(c_user,c_time) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;