SELECT u.user_id, u.user_name, u.nick_name, u.status, u.del_flag, r.role_name, r.role_key
FROM sys_user u
JOIN sys_user_role ur ON u.user_id = ur.user_id
JOIN sys_role r ON ur.role_id = r.role_id
WHERE r.role_key LIKE '%changshang%' OR r.role_name LIKE '%??%' OR r.role_name LIKE '%??%' OR r.role_name LIKE '%??%'
ORDER BY u.user_id;
