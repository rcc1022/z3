DROP TABLE IF EXISTS t_wps_file;
CREATE TABLE t_wps_file
(
  c_id CHAR(36) NOT NULL COMMENT '主键',
  c_wps CHAR(36) NOT NULL COMMENT 'WPS',
  c_uri VARCHAR(255) NOT NULL COMMENT 'URI',
  c_name VARCHAR(255) DEFAULT NULL COMMENT '文件名',
  c_permission INT DEFAULT 0 COMMENT '权限：0-只读；1-读写',
  c_version INT DEFAULT 0 COMMENT '版本号',
  c_size BIGINT DEFAULT 0 COMMENT '文件大小',
  c_creator CHAR(36) DEFAULT NULL COMMENT '创建者',
  c_create_time BIGINT DEFAULT 0 COMMENT '创建时间，单位：秒',
  c_modifier CHAR(36) DEFAULT NULL COMMENT '修改者',
  c_modify_time BIGINT DEFAULT 0 COMMENT '修改时间，单位：秒',

  PRIMARY KEY pk(c_id) USING HASH,
  KEY k_wps(c_wps) USING HASH,
  KEY k_uri(c_uri) USING HASH
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
