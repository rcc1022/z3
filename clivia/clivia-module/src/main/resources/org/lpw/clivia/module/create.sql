DROP TABLE IF EXISTS t_module;
CREATE TABLE t_module
(
  c_id CHAR(36) NOT NULL COMMENT '主键',
  c_main VARCHAR(255) DEFAULT NULL COMMENT '主模块',
  c_code VARCHAR(255) DEFAULT NULL COMMENT '编码前缀',
  c_name VARCHAR(255) DEFAULT NULL COMMENT '名称',
  c_execute VARCHAR(255) DEFAULT NULL COMMENT '操作',
  c_columns TEXT DEFAULT NULL COMMENT '字段集',

  PRIMARY KEY pk(c_id) USING HASH
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;