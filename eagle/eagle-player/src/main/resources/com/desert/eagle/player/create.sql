DROP TABLE IF EXISTS t_player;
CREATE TABLE t_player
(
  c_id CHAR(36) NOT NULL COMMENT '主键',
  c_uid VARCHAR(255) NOT NULL COMMENT 'UID',
  c_balance INT DEFAULT 0 COMMENT '余额',
  c_bet INT DEFAULT 0 COMMENT '下注流水',
  c_profit INT DEFAULT 0 COMMENT '输赢',
  c_invitor CHAR(36) DEFAULT NULL COMMENT '推荐人',
  c_qrcode VARCHAR(255) DEFAULT NULL COMMENT '二维码',
  c_qruri VARCHAR(255) DEFAULT NULL COMMENT '二维码图片URI',
  c_memo VARCHAR(255) DEFAULT NULL COMMENT '备注',
  c_commission INT DEFAULT 0 COMMENT '总佣金',
  c_commission_balance INT DEFAULT 0 COMMENT '佣金余额',
  c_commission_generate INT DEFAULT 0 COMMENT '产生佣金',
  c_login DATETIME DEFAULT NULL COMMENT '最后一次登录时间',
  c_ip VARCHAR(255) DEFAULT NULL COMMENT '登录IP',
  c_ban INT DEFAULT 0 COMMENT '封禁：0-否；1-是',
  c_junior INT DEFAULT 0 COMMENT '下级人数',
  c_time DATETIME DEFAULT NULL COMMENT '时间',

  PRIMARY KEY pk(c_id) USING HASH,
  UNIQUE KEY uk_uid(c_uid) USING HASH
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;