DROP TABLE IF EXISTS t_olcs_member;
CREATE TABLE t_olcs_member
(
  c_id CHAR(36) NOT NULL COMMENT '主键',
  c_content TEXT DEFAULT NULL COMMENT '内容',
  c_time DATETIME NOT NULL COMMENT '消息时间',
  c_user_unread INT DEFAULT 0 COMMENT '用户未读数',
  c_replier_unread INT DEFAULT 0 COMMENT '客服未读数',
  c_user_read DATETIME DEFAULT NULL COMMENT '用户阅读时间',
  c_replier_read DATETIME DEFAULT NULL COMMENT '客服阅读时间',

  PRIMARY KEY pk(c_id) USING HASH,
  KEY k_time(c_time) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;