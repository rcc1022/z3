-- 2023-03-13;
ALTER TABLE t_player_profit ADD COLUMN c_water2 INT DEFAULT 0 COMMENT '拒绝返水' AFTER c_water0;

-- 2303-03-26;
UPDATE t_game SET c_type=11 WHERE c_type=10;

-- 2023-03-29;
UPDATE t_daily SET c_profit=c_profit+c_bet;
UPDATE t_game SET c_type=12 WHERE c_type=11;

-- 2023-03-31;
ALTER TABLE t_player_deposit ADD COLUMN c_check INT DEFAULT 0 COMMENT '审核：0-否；1-是' AFTER c_status;
UPDATE t_player_deposit SET c_check=1 WHERE c_status>0;
ALTER TABLE t_player_withdraw ADD COLUMN c_check INT DEFAULT 0 COMMENT '审核：0-否；1-是' AFTER c_status;
UPDATE t_player_withdraw SET c_check=1 WHERE c_status>0;

-- 2023-04-01;
INSERT INTO t_user_auth VALUES(UUID(),'a8804f2a-4814-45f2-b25f-210724ad5d57','kefu','',NULL,NULL,NULL,NULL,NOW());
INSERT INTO t_user(c_id,c_password,c_nick,c_code,c_register,c_grade,c_state) VALUES('a8804f2a-4814-45f2-b25f-210724ad5d57','4598278f1f939315f739de84f03f6491','kefu','89cqnnct',NOW(),89,1);
