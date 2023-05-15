DROP TABLE IF EXISTS t_chat;
CREATE TABLE t_chat
(
  c_id CHAR(36) NOT NULL COMMENT '主键',
  c_group CHAR(36) NOT NULL COMMENT '群组',
  c_sender CHAR(36) DEFAULT NULL COMMENT '发送人',
  c_genre VARCHAR(255) DEFAULT NULL COMMENT '类型',
  c_body VARCHAR(10240) DEFAULT NULL COMMENT '内容',
  c_time BIGINT DEFAULT 0 COMMENT '时间',

  PRIMARY KEY pk(c_id) USING HASH,
  KEY k_group_time(c_group,c_time) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

DROP TABLE IF EXISTS m_chat;
CREATE TABLE m_chat
(
  c_id CHAR(36) NOT NULL COMMENT '主键',
  c_group CHAR(36) NOT NULL COMMENT '群组',
  c_sender CHAR(36) DEFAULT NULL COMMENT '发送人',
  c_genre VARCHAR(255) DEFAULT NULL COMMENT '类型',
  c_body VARCHAR(10240) DEFAULT NULL COMMENT '内容',
  c_time BIGINT DEFAULT 0 COMMENT '时间',

  PRIMARY KEY pk(c_id) USING HASH,
  KEY k_group_time(c_group,c_time) USING BTREE
) ENGINE=Memory DEFAULT CHARSET=utf8mb4;