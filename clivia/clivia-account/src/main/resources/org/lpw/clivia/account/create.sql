DROP TABLE IF EXISTS t_account;
CREATE TABLE t_account
(
  c_id CHAR(36) NOT NULL COMMENT '主键',
  c_user CHAR(36) NOT NULL COMMENT '用户',
  c_owner VARCHAR(255) DEFAULT NULL COMMENT '所有者',
  c_type INT DEFAULT 0 COMMENT '类型',
  c_balance BIGINT DEFAULT 0 COMMENT '余额',
  c_deposit BIGINT DEFAULT 0 COMMENT '存入总额',
  c_withdraw BIGINT DEFAULT 0 COMMENT '取出总额',
  c_reward BIGINT DEFAULT 0 COMMENT '奖励总额',
  c_profit BIGINT DEFAULT 0 COMMENT '盈利总额',
  c_consume BIGINT DEFAULT 0 COMMENT '消费总额',
  c_remit_in BIGINT DEFAULT 0 COMMENT '汇入总额',
  c_remit_out BIGINT DEFAULT 0 COMMENT '汇出总额',
  c_refund BIGINT DEFAULT 0 COMMENT '退款总额',
  c_pending BIGINT DEFAULT 0 COMMENT '待结算总额',
  c_checksum CHAR(32) DEFAULT NULL COMMENT '校验值',

  PRIMARY KEY pk(c_id) USING HASH,
  KEY k_user(c_user) USING HASH
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
