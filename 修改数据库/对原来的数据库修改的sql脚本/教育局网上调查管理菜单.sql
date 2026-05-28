-- Give school and education bureau roles access to online survey management.
-- Role 101 = xuexiao
-- Role 102 = jyj
-- Role 103 = sjyj
-- Menu 2094 = wjdc/index
-- Menu 2124 = school:survey:add

INSERT INTO sys_role_menu (role_id, menu_id)
SELECT 101, 2094
WHERE NOT EXISTS (
  SELECT 1 FROM sys_role_menu WHERE role_id = 101 AND menu_id = 2094
);

INSERT INTO sys_role_menu (role_id, menu_id)
SELECT 101, 2124
WHERE NOT EXISTS (
  SELECT 1 FROM sys_role_menu WHERE role_id = 101 AND menu_id = 2124
);

INSERT INTO sys_role_menu (role_id, menu_id)
SELECT 102, 2094
WHERE NOT EXISTS (
  SELECT 1 FROM sys_role_menu WHERE role_id = 102 AND menu_id = 2094
);

INSERT INTO sys_role_menu (role_id, menu_id)
SELECT 102, 2124
WHERE NOT EXISTS (
  SELECT 1 FROM sys_role_menu WHERE role_id = 102 AND menu_id = 2124
);

INSERT INTO sys_role_menu (role_id, menu_id)
SELECT 103, 2094
WHERE NOT EXISTS (
  SELECT 1 FROM sys_role_menu WHERE role_id = 103 AND menu_id = 2094
);

INSERT INTO sys_role_menu (role_id, menu_id)
SELECT 103, 2124
WHERE NOT EXISTS (
  SELECT 1 FROM sys_role_menu WHERE role_id = 103 AND menu_id = 2124
);
