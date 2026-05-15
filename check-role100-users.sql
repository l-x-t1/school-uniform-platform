SELECT ur.user_id, u.user_name, u.nick_name, u.status, u.del_flag
FROM sys_user_role ur
LEFT JOIN sys_user u ON ur.user_id = u.user_id
WHERE ur.role_id = 100;
