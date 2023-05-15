DROP TABLE IF EXISTS t_category;
CREATE TABLE t_category
(
  c_id CHAR(36) NOT NULL COMMENT '主键',
  c_key VARCHAR(255) NOT NULL COMMENT '键',
  c_parent CHAR(36) NOT NULL COMMENT '上级',
  c_sort INT DEFAULT 0 COMMENT '顺序',
  c_name VARCHAR(255) DEFAULT NULL COMMENT '名称',
  c_value TEXT DEFAULT NULL COMMENT '值',
  c_image TEXT DEFAULT NULL COMMENT '图片',
  c_point_to CHAR(36) DEFAULT NULL COMMENT '指向',

  PRIMARY KEY pk(c_id) USING HASH,
  KEY k_key_parent(c_key,c_parent) USING HASH
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
