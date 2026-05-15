SELECT u.user_id, u.user_name, LEFT(u.password, 20) AS password_prefix, LENGTH(u.password) AS password_len
FROM sys_user u
JOIN sys_user_role ur ON u.user_id = ur.user_id
JOIN sys_role r ON ur.role_id = r.role_id
WHERE r.role_key LIKE '%changshang%' OR r.role_name LIKE '%??%' OR r.role_name LIKE '%??%' OR r.role_name LIKE '%??%'
ORDER BY u.user_id;
