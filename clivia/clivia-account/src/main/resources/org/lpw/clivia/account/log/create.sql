DROP TABLE IF EXISTS t_account_log;
CREATE TABLE t_account_log
(
  c_id CHAR(36) NOT NULL COMMENT '主键',
  c_user CHAR(36) NOT NULL COMMENT '用户',
  c_account CHAR(36) NOT NULL COMMENT '账户',
  c_owner CHAR(36) DEFAULT NULL COMMENT '所有者',
  c_type VARCHAR(255) DEFAULT NULL COMMENT '类型',
  c_channel VARCHAR(255) DEFAULT NULL COMMENT '渠道',
  c_amount BIGINT DEFAULT 0 COMMENT '数量',
  c_balance BIGINT DEFAULT 0 COMMENT '余额',
  c_state INT DEFAULT 0 COMMENT '状态：0-待处理；1-审核通过；2-审核不通过',
  c_restate INT DEFAULT 0 COMMENT '重置状态：0-待处理；1-审核通过；2-审核不通过',
  c_start DATETIME DEFAULT NULL COMMENT '开始时间',
  c_end DATETIME DEFAULT NULL COMMENT '结束时间',
  c_json TEXT DEFAULT NULL COMMENT '扩展属性集',
  c_index BIGINT AUTO_INCREMENT NOT NULL COMMENT '序号',

  PRIMARY KEY pk(c_id) USING HASH,
  KEY k_user(c_user,c_start) USING BTREE,
  KEY k_account(c_account,c_start) USING BTREE,
  KEY k_owner(c_owner,c_start) USING BTREE,
  KEY k_restate(c_restate) USING HASH,
  UNIQUE KEY uk_index(c_index) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4;
