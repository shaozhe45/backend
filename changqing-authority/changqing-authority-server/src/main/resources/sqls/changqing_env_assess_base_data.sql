DELETE FROM `c_auth_menu` where label='开发者管理';
DELETE FROM `c_auth_menu` where label='网关管理';
DELETE FROM `c_auth_menu` where label='文件中心';
DELETE FROM `c_auth_menu` where parent_id=104;
DELETE FROM `c_auth_menu` where parent_id=1291625710699413504;
DELETE FROM `c_auth_menu` where parent_id=107;

delete from t_clause_score;
DELETE FROM `f_attachment`;
delete from t_self_check_result;

update `c_common_parameter` set `name`='/analysis/api/v3/share/data/' where key_='DATATOTAL01' ;
update `c_common_parameter` set `name`='/analysis/api/v3/share/data/' where key_='DATATOTAL02' ;
update `c_common_parameter` set `name`='/analysis/api/v3/share/data/' where key_='DATAYINHUAN' ;
update `c_common_parameter` set `name`='/analysis/api/v3/share/data/' where key_='DATAERJIYAOSU' ;
update `c_common_parameter` set `name`='/analysis/api/v3/share/data/' where key_='DATAERJIQIAN3' ;
update `c_common_parameter` set `name`='/analysis/api/v3/share/data/' where key_='DATAERJIHOU3' ;

DELETE FROM `t_check_form`;
DELETE FROM `t_hidden_trouble_rectify`;
DELETE FROM `t_manage_record`;
UPDATE c_auth_user SET `name`=(SELECT CONCAT(`name`,'超管') FROM changqing_env_assess_defaults.`d_tenant` where `code`='{tenant}') WHERE account='admin';

DELETE FROM t_course;
DELETE FROM t_course_chapter;
DELETE FROM t_train_record;
DELETE FROM t_station_train;
DELETE FROM t_station_course;
DELETE FROM t_train_station;
DELETE FROM t_user_train_chapter;
DELETE FROM t_rectify_confirm;

DELETE FROM `c_auth_menu` where id>=150 and id<=157;
DELETE FROM `c_auth_menu` where id=50;