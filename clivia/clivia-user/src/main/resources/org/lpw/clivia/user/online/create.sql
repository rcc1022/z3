DROP TABLE IF EXISTS t_user_online;
CREATE TABLE t_user_online
(
  c_id CHAR(36) NOT NULL COMMENT '主键',
  c_user CHAR(36) NOT NULL COMMENT '用户',
  c_grade INT DEFAULT 0 COMMENT '等级',
  c_ip VARCHAR(255) NOT NULL COMMENT 'IP地址',
  c_sid VARCHAR(255) NOT NULL COMMENT 'Session ID',
  c_sign_in DATETIME DEFAULT NULL COMMENT '登入时间',
  c_last_visit DATETIME NOT NULL COMMENT '最后访问时间',

  PRIMARY KEY pk(c_id) USING HASH,
  UNIQUE KEY uk_sid(c_sid) USING HASH
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

DROP TABLE IF EXISTS m_user_online;
CREATE TABLE m_user_online
(
  c_id CHAR(36) NOT NULL COMMENT '主键',
  c_user CHAR(36) NOT NULL COMMENT '用户',
  c_grade INT DEFAULT 0 COMMENT '等级',
  c_ip VARCHAR(255) NOT NULL COMMENT 'IP地址',
  c_sid VARCHAR(255) NOT NULL COMMENT 'Session ID',
  c_sign_in DATETIME DEFAULT NULL COMMENT '登入时间',
  c_last_visit DATETIME NOT NULL COMMENT '最后访问时间',

  PRIMARY KEY pk(c_id) USING HASH,
  UNIQUE KEY uk_sid(c_sid) USING HASH
) ENGINE=Memory DEFAULT CHARSET=utf8mb4;
