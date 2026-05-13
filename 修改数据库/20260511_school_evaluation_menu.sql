-- 学校端评价管理菜单及权限
-- 说明：挂到“新增功能”目录下，优先按菜单名称动态查找父菜单ID。

SET @new_feature_menu_id := (
  SELECT `MENU_ID`
  FROM `sys_menu`
  WHERE `MENU_NAME` = '新增功能'
    AND `MENU_TYPE` = 'M'
  ORDER BY `MENU_ID` DESC
  LIMIT 1
);
SET @new_feature_menu_id := IFNULL(@new_feature_menu_id, 2333);

SET @evaluation_menu_id := (
  SELECT `MENU_ID`
  FROM `sys_menu`
  WHERE `PERMS` = 'school:evaluation:list'
  LIMIT 1
);
SET @evaluation_menu_id := IFNULL(@evaluation_menu_id, (SELECT IFNULL(MAX(`MENU_ID`), 0) + 1 FROM `sys_menu`));

INSERT INTO `sys_menu` (
  `MENU_ID`, `MENU_NAME`, `PARENT_ID`, `ORDER_NUM`, `PATH`, `COMPONENT`, `QUERY`, `ROUTE_NAME`,
  `IS_FRAME`, `IS_CACHE`, `MENU_TYPE`, `VISIBLE`, `STATUS`, `PERMS`, `ICON`,
  `CREATE_BY`, `CREATE_TIME`, `UPDATE_BY`, `UPDATE_TIME`, `REMARK`, `TYPE`
)
SELECT @evaluation_menu_id, '评价管理', @new_feature_menu_id, 8, 'evaluation', 'school/evaluation/index', NULL, '',
       1, 0, 'C', '0', '0', 'school:evaluation:list', 'star',
       'admin', NOW(), '', NULL, '学校端评价管理菜单', NULL
WHERE NOT EXISTS (
  SELECT 1
  FROM `sys_menu`
  WHERE `PERMS` = 'school:evaluation:list'
);

UPDATE `sys_menu`
SET `PARENT_ID` = @new_feature_menu_id,
    `ORDER_NUM` = 8,
    `PATH` = 'evaluation',
    `COMPONENT` = 'school/evaluation/index',
    `MENU_TYPE` = 'C',
    `UPDATE_BY` = 'admin',
    `UPDATE_TIME` = NOW(),
    `REMARK` = '学校端评价管理菜单'
WHERE `PERMS` = 'school:evaluation:list';

UPDATE `sys_menu`
SET `PARENT_ID` = @evaluation_menu_id,
    `UPDATE_BY` = 'admin',
    `UPDATE_TIME` = NOW()
WHERE `PERMS` IN ('school:evaluation:query', 'school:evaluation:reply', 'school:evaluation:hide');

SET @evaluation_query_id := (
  SELECT `MENU_ID`
  FROM `sys_menu`
  WHERE `PERMS` = 'school:evaluation:query'
  LIMIT 1
);
SET @evaluation_query_id := IFNULL(@evaluation_query_id, (SELECT IFNULL(MAX(`MENU_ID`), 0) + 1 FROM `sys_menu`));

INSERT INTO `sys_menu` (
  `MENU_ID`, `MENU_NAME`, `PARENT_ID`, `ORDER_NUM`, `PATH`, `COMPONENT`, `QUERY`, `ROUTE_NAME`,
  `IS_FRAME`, `IS_CACHE`, `MENU_TYPE`, `VISIBLE`, `STATUS`, `PERMS`, `ICON`,
  `CREATE_BY`, `CREATE_TIME`, `UPDATE_BY`, `UPDATE_TIME`, `REMARK`, `TYPE`
)
SELECT @evaluation_query_id, '评价管理查询', @evaluation_menu_id, 1, '#', '', NULL, '',
       1, 0, 'F', '0', '0', 'school:evaluation:query', '#',
       'admin', NOW(), '', NULL, '', NULL
WHERE NOT EXISTS (
  SELECT 1
  FROM `sys_menu`
  WHERE `PERMS` = 'school:evaluation:query'
);

SET @evaluation_reply_id := (
  SELECT `MENU_ID`
  FROM `sys_menu`
  WHERE `PERMS` = 'school:evaluation:reply'
  LIMIT 1
);
SET @evaluation_reply_id := IFNULL(@evaluation_reply_id, (SELECT IFNULL(MAX(`MENU_ID`), 0) + 1 FROM `sys_menu`));

INSERT INTO `sys_menu` (
  `MENU_ID`, `MENU_NAME`, `PARENT_ID`, `ORDER_NUM`, `PATH`, `COMPONENT`, `QUERY`, `ROUTE_NAME`,
  `IS_FRAME`, `IS_CACHE`, `MENU_TYPE`, `VISIBLE`, `STATUS`, `PERMS`, `ICON`,
  `CREATE_BY`, `CREATE_TIME`, `UPDATE_BY`, `UPDATE_TIME`, `REMARK`, `TYPE`
)
SELECT @evaluation_reply_id, '评价管理回复', @evaluation_menu_id, 2, '#', '', NULL, '',
       1, 0, 'F', '0', '0', 'school:evaluation:reply', '#',
       'admin', NOW(), '', NULL, '', NULL
WHERE NOT EXISTS (
  SELECT 1
  FROM `sys_menu`
  WHERE `PERMS` = 'school:evaluation:reply'
);

SET @evaluation_hide_id := (
  SELECT `MENU_ID`
  FROM `sys_menu`
  WHERE `PERMS` = 'school:evaluation:hide'
  LIMIT 1
);
SET @evaluation_hide_id := IFNULL(@evaluation_hide_id, (SELECT IFNULL(MAX(`MENU_ID`), 0) + 1 FROM `sys_menu`));

INSERT INTO `sys_menu` (
  `MENU_ID`, `MENU_NAME`, `PARENT_ID`, `ORDER_NUM`, `PATH`, `COMPONENT`, `QUERY`, `ROUTE_NAME`,
  `IS_FRAME`, `IS_CACHE`, `MENU_TYPE`, `VISIBLE`, `STATUS`, `PERMS`, `ICON`,
  `CREATE_BY`, `CREATE_TIME`, `UPDATE_BY`, `UPDATE_TIME`, `REMARK`, `TYPE`
)
SELECT @evaluation_hide_id, '评价管理隐藏恢复', @evaluation_menu_id, 3, '#', '', NULL, '',
       1, 0, 'F', '0', '0', 'school:evaluation:hide', '#',
       'admin', NOW(), '', NULL, '', NULL
WHERE NOT EXISTS (
  SELECT 1
  FROM `sys_menu`
  WHERE `PERMS` = 'school:evaluation:hide'
);

INSERT INTO `sys_role_menu` (`ROLE_ID`, `MENU_ID`)
SELECT r.`ROLE_ID`, m.`MENU_ID`
FROM `sys_role` r
JOIN `sys_menu` m
  ON m.`PERMS` IN ('school:evaluation:list', 'school:evaluation:query', 'school:evaluation:reply', 'school:evaluation:hide')
WHERE r.`ROLE_KEY` IN ('admin', 'xuexiao', 'gly', 'testXiaofu')
  AND r.`DEL_FLAG` = '0'
  AND NOT EXISTS (
    SELECT 1
    FROM `sys_role_menu` rm
    WHERE rm.`ROLE_ID` = r.`ROLE_ID`
      AND rm.`MENU_ID` = m.`MENU_ID`
  );

INSERT INTO `sys_role_menu` (`ROLE_ID`, `MENU_ID`)
SELECT DISTINCT rm_parent.`ROLE_ID`, m.`MENU_ID`
FROM `sys_role_menu` rm_parent
JOIN `sys_menu` m
  ON m.`PERMS` IN ('school:evaluation:list', 'school:evaluation:query', 'school:evaluation:reply', 'school:evaluation:hide')
WHERE rm_parent.`MENU_ID` = @new_feature_menu_id
  AND NOT EXISTS (
    SELECT 1
    FROM `sys_role_menu` rm
    WHERE rm.`ROLE_ID` = rm_parent.`ROLE_ID`
      AND rm.`MENU_ID` = m.`MENU_ID`
  );

INSERT INTO `sys_role_menu` (`ROLE_ID`, `MENU_ID`)
SELECT DISTINCT ur.`ROLE_ID`, m.`MENU_ID`
FROM `sys_user` u
JOIN `sys_user_role` ur
  ON ur.`USER_ID` = u.`USER_ID`
JOIN `sys_menu` m
  ON m.`PERMS` IN ('school:evaluation:list', 'school:evaluation:query', 'school:evaluation:reply', 'school:evaluation:hide')
WHERE u.`USER_NAME` = 'testUser'
  AND NOT EXISTS (
    SELECT 1
    FROM `sys_role_menu` rm
    WHERE rm.`ROLE_ID` = ur.`ROLE_ID`
      AND rm.`MENU_ID` = m.`MENU_ID`
  );

INSERT INTO `sys_role_menu` (`ROLE_ID`, `MENU_ID`)
SELECT r.`ROLE_ID`, m.`MENU_ID`
FROM `sys_role` r
JOIN `sys_menu` m
  ON m.`MENU_ID` = @new_feature_menu_id
WHERE r.`ROLE_KEY` IN ('admin', 'xuexiao', 'gly', 'testXiaofu')
  AND r.`DEL_FLAG` = '0'
  AND NOT EXISTS (
    SELECT 1
    FROM `sys_role_menu` rm
    WHERE rm.`ROLE_ID` = r.`ROLE_ID`
      AND rm.`MENU_ID` = m.`MENU_ID`
  );

INSERT INTO `sys_role_menu` (`ROLE_ID`, `MENU_ID`)
SELECT DISTINCT ur.`ROLE_ID`, m.`MENU_ID`
FROM `sys_user` u
JOIN `sys_user_role` ur
  ON ur.`USER_ID` = u.`USER_ID`
JOIN `sys_menu` m
  ON m.`MENU_ID` = @new_feature_menu_id
WHERE u.`USER_NAME` = 'testUser'
  AND NOT EXISTS (
    SELECT 1
    FROM `sys_role_menu` rm
    WHERE rm.`ROLE_ID` = ur.`ROLE_ID`
      AND rm.`MENU_ID` = m.`MENU_ID`
  );
