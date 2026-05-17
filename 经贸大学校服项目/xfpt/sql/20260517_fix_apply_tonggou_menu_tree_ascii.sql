-- ASCII-only repair and apply script.
-- Chinese text is expressed as utf8mb4 hex literals to avoid Windows pipe encoding issues.

START TRANSACTION;

SET @name_new_feature := CONVERT(0xE696B0E5A29EE58A9FE883BD USING utf8mb4);
SET @name_tonggou := CONVERT(0xE7BB9FE8B4ADE7AEA1E79086 USING utf8mb4);
SET @name_notice := CONVERT(0xE585ACE5918AE7AEA1E79086 USING utf8mb4);
SET @name_progress := CONVERT(0xE7BB9FE8B4ADE8AEA2E8B4ADE8BF9BE5BAA6E7AEA1E79086 USING utf8mb4);
SET @name_quality := CONVERT(0xE8B4A8E6A380E68AA5E5918AE7AEA1E79086 USING utf8mb4);
SET @name_size := CONVERT(0xE5B0BAE7A081E8B083E68DA2E8AEB0E5BD95 USING utf8mb4);
SET @name_delivery := CONVERT(0xE58F91E8B4A7E58D95E7BB9FE8AEA1 USING utf8mb4);
SET @name_list := CONVERT(0xE58897E8A1A8 USING utf8mb4);
SET @name_add_notice := CONVERT(0xE6B7BBE58AA0E585ACE5918A USING utf8mb4);
SET @name_detail := CONVERT(0xE8AFA6E68385 USING utf8mb4);
SET @name_size_list := CONVERT(0xE58897E8A1A8EFBC88E5BE85E58F97E79086E38081E5B7B2E9809AE8BF87E38081E5B7B2E98080E59B9EE38081E5B7B2E69BB4E68DA2EFBC89 USING utf8mb4);

SET @remark_notice := CONVERT(0xE7BB99E4BC81E4B89AE58F91E5B883E7BB9FE8B4ADE585ACE5918AEFBC8CE4BC81E4B89AE58FAFE4BBA5E69FA5E79C8B USING utf8mb4);
SET @remark_progress := CONVERT(0xE69FA5E79C8BE7BB9FE8B4ADE8AEA2E8B4ADE8BF9BE5BAA6 USING utf8mb4);
SET @remark_quality := CONVERT(0xE7AEA1E79086E7BB9FE8B4ADE8B4A8E6A380E68AA5E5918A USING utf8mb4);
SET @remark_size := CONVERT(0xE68C89E78AB6E68081E69FA5E79C8BE5B0BAE7A081E8B083E68DA2E8AEB0E5BD95 USING utf8mb4);
SET @remark_delivery := CONVERT(0xE7BB9FE8AEA1E7BB9FE8B4ADE58F91E8B4A7E58D95 USING utf8mb4);
SET @remark_notice_list := CONVERT(0xE69FA5E79C8BE7BB9FE8B4ADE585ACE5918AE58897E8A1A8 USING utf8mb4);
SET @remark_notice_detail := CONVERT(0xE69FA5E79C8BE7BB9FE8B4ADE585ACE5918AE8AFA6E68385 USING utf8mb4);
SET @remark_progress_list := CONVERT(0xE69FA5E79C8BE7BB9FE8B4ADE8AEA2E8B4ADE8BF9BE5BAA6E58897E8A1A8 USING utf8mb4);
SET @remark_progress_detail := CONVERT(0xE69FA5E79C8BE7BB9FE8B4ADE8AEA2E8B4ADE8BF9BE5BAA6E8AFA6E68385 USING utf8mb4);
SET @remark_quality_list := CONVERT(0xE69FA5E79C8BE8B4A8E6A380E68AA5E5918AE58897E8A1A8 USING utf8mb4);
SET @remark_quality_detail := CONVERT(0xE69FA5E79C8BE8B4A8E6A380E68AA5E5918AE8AFA6E68385 USING utf8mb4);
SET @remark_size_list := CONVERT(0xE68C89E78AB6E68081E69FA5E79C8BE5B0BAE7A081E8B083E68DA2E8AEB0E5BD95E58897E8A1A8 USING utf8mb4);
SET @remark_size_detail := CONVERT(0xE69FA5E79C8BE5B0BAE7A081E8B083E68DA2E8AEB0E5BD95E8AFA6E68385 USING utf8mb4);
SET @remark_delivery_list := CONVERT(0xE69FA5E79C8BE58F91E8B4A7E58D95E7BB9FE8AEA1E58897E8A1A8 USING utf8mb4);

-- Clean rows produced by the failed encoded run.
DELETE FROM sys_role_menu
WHERE menu_id IN (
  SELECT menu_id
  FROM sys_menu
  WHERE menu_id BETWEEN 3036 AND 3045
    AND HEX(menu_name) LIKE '3F%'
);

DELETE FROM sys_menu
WHERE menu_id BETWEEN 3036 AND 3045
  AND HEX(menu_name) LIKE '3F%';

SET @new_feature_id := (
  SELECT menu_id FROM sys_menu
  WHERE menu_name = @name_new_feature AND menu_type = 'M'
  ORDER BY parent_id, order_num, menu_id
  LIMIT 1
);

INSERT INTO sys_menu (
  menu_name, parent_id, order_num, path, component, query, route_name,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_by, create_time, update_by, update_time, remark
)
SELECT @name_new_feature, 0, 99, 'newFeature', NULL, NULL, '',
       1, 0, 'M', '0', '0', '', 'tree',
       'admin', NOW(), '', NULL, CONVERT(0xE696B0E5A29EE58A9FE883BDE79BAEE5BD95 USING utf8mb4)
WHERE @new_feature_id IS NULL;

SET @new_feature_id := (
  SELECT menu_id FROM sys_menu
  WHERE menu_name = @name_new_feature AND menu_type = 'M'
  ORDER BY parent_id, order_num, menu_id
  LIMIT 1
);

SET @tonggou_id := (
  SELECT menu_id FROM sys_menu
  WHERE menu_name = @name_tonggou AND menu_type = 'M'
  ORDER BY parent_id, order_num, menu_id
  LIMIT 1
);

UPDATE sys_menu
SET parent_id = @new_feature_id,
    path = '/tonggou',
    order_num = 2,
    update_by = 'admin',
    update_time = NOW()
WHERE menu_id = @tonggou_id
  AND menu_name = @name_tonggou
  AND menu_type = 'M';

SET @notice_id := (
  SELECT menu_id FROM sys_menu
  WHERE menu_type = 'C' AND perms = 'school:groupNotice:list'
  ORDER BY menu_id LIMIT 1
);

UPDATE sys_menu
SET menu_name = @name_notice,
    parent_id = @tonggou_id,
    order_num = 1,
    update_by = 'admin',
    update_time = NOW()
WHERE menu_id = @notice_id
  AND menu_type = 'C'
  AND perms = 'school:groupNotice:list';

SET @progress_id := (
  SELECT menu_id FROM sys_menu
  WHERE menu_type = 'C' AND perms = 'school:tgjd:list'
  ORDER BY menu_id LIMIT 1
);

INSERT INTO sys_menu (
  menu_name, parent_id, order_num, path, component, query, route_name,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_by, create_time, update_by, update_time, remark
)
SELECT @name_progress, @tonggou_id, 2, 'tgjd', 'tgjd/index', NULL, '',
       1, 0, 'C', '0', '0', 'school:tgjd:list', 'chart',
       'admin', NOW(), '', NULL, @remark_progress
WHERE @progress_id IS NULL;

SET @progress_id := (
  SELECT menu_id FROM sys_menu
  WHERE menu_type = 'C' AND perms = 'school:tgjd:list'
  ORDER BY menu_id LIMIT 1
);

UPDATE sys_menu
SET parent_id = @tonggou_id,
    order_num = 2,
    update_by = 'admin',
    update_time = NOW()
WHERE menu_id = @progress_id
  AND menu_type = 'C'
  AND perms = 'school:tgjd:list';

SET @quality_id := (
  SELECT menu_id FROM sys_menu
  WHERE menu_type = 'C' AND perms = 'school:zjbg:list'
  ORDER BY menu_id LIMIT 1
);

UPDATE sys_menu
SET parent_id = @tonggou_id,
    order_num = 3,
    update_by = 'admin',
    update_time = NOW()
WHERE menu_id = @quality_id
  AND menu_type = 'C'
  AND perms = 'school:zjbg:list';

SET @size_id := (
  SELECT menu_id FROM sys_menu
  WHERE menu_type = 'C' AND perms = 'school:sxjjl:list'
  ORDER BY menu_id LIMIT 1
);

INSERT INTO sys_menu (
  menu_name, parent_id, order_num, path, component, query, route_name,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_by, create_time, update_by, update_time, remark
)
SELECT @name_size, @tonggou_id, 4, 'sxjjl', 'sxjjl/index', NULL, '',
       1, 0, 'C', '0', '0', 'school:sxjjl:list', 'size',
       'admin', NOW(), '', NULL, @remark_size
WHERE @size_id IS NULL;

SET @size_id := (
  SELECT menu_id FROM sys_menu
  WHERE menu_type = 'C' AND perms = 'school:sxjjl:list'
  ORDER BY menu_id LIMIT 1
);

UPDATE sys_menu
SET parent_id = @tonggou_id,
    order_num = 4,
    update_by = 'admin',
    update_time = NOW()
WHERE menu_id = @size_id
  AND menu_type = 'C'
  AND perms = 'school:sxjjl:list';

SET @delivery_id := (
  SELECT menu_id FROM sys_menu
  WHERE menu_type = 'C' AND perms = 'school:tgfh:list'
  ORDER BY menu_id LIMIT 1
);

INSERT INTO sys_menu (
  menu_name, parent_id, order_num, path, component, query, route_name,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_by, create_time, update_by, update_time, remark
)
SELECT @name_delivery, @tonggou_id, 5, 'tgfh', 'tgfh/index', NULL, '',
       1, 0, 'C', '0', '0', 'school:tgfh:list', 'excel',
       'admin', NOW(), '', NULL, @remark_delivery
WHERE @delivery_id IS NULL;

SET @delivery_id := (
  SELECT menu_id FROM sys_menu
  WHERE menu_type = 'C' AND perms = 'school:tgfh:list'
  ORDER BY menu_id LIMIT 1
);

UPDATE sys_menu
SET parent_id = @tonggou_id,
    order_num = 5,
    update_by = 'admin',
    update_time = NOW()
WHERE menu_id = @delivery_id
  AND menu_type = 'C'
  AND perms = 'school:tgfh:list';

-- Function permissions.
INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
SELECT @name_list, @notice_id, 1, '', NULL, NULL, '', 1, 0, 'F', '0', '0', 'school:groupNotice:list', '#', 'admin', NOW(), '', NULL, @remark_notice_list
WHERE NOT EXISTS (SELECT 1 FROM sys_menu WHERE parent_id = @notice_id AND menu_type = 'F' AND perms = 'school:groupNotice:list');

UPDATE sys_menu SET menu_name = @name_add_notice, parent_id = @notice_id, order_num = 2, update_by = 'admin', update_time = NOW()
WHERE menu_type = 'F' AND perms = 'school:groupNotice:add';

UPDATE sys_menu SET menu_name = @name_detail, parent_id = @notice_id, order_num = 3, update_by = 'admin', update_time = NOW()
WHERE menu_type = 'F' AND perms = 'school:groupNotice:query';

INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
SELECT @name_list, @progress_id, 1, '', NULL, NULL, '', 1, 0, 'F', '0', '0', 'school:tgjd:list', '#', 'admin', NOW(), '', NULL, @remark_progress_list
WHERE NOT EXISTS (SELECT 1 FROM sys_menu WHERE parent_id = @progress_id AND menu_type = 'F' AND perms = 'school:tgjd:list');

INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
SELECT @name_detail, @progress_id, 2, '', NULL, NULL, '', 1, 0, 'F', '0', '0', 'school:tgjd:query', '#', 'admin', NOW(), '', NULL, @remark_progress_detail
WHERE NOT EXISTS (SELECT 1 FROM sys_menu WHERE parent_id = @progress_id AND menu_type = 'F' AND perms = 'school:tgjd:query');

UPDATE sys_menu SET menu_name = @name_list, parent_id = @quality_id, order_num = 1, update_by = 'admin', update_time = NOW()
WHERE menu_type = 'F' AND perms = 'school:zjbg:list';

UPDATE sys_menu SET menu_name = @name_detail, parent_id = @quality_id, order_num = 2, update_by = 'admin', update_time = NOW()
WHERE menu_type = 'F' AND perms = 'school:zjbg:query';

INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
SELECT @name_size_list, @size_id, 1, '', NULL, NULL, '', 1, 0, 'F', '0', '0', 'school:sxjjl:list', '#', 'admin', NOW(), '', NULL, @remark_size_list
WHERE NOT EXISTS (SELECT 1 FROM sys_menu WHERE parent_id = @size_id AND menu_type = 'F' AND perms = 'school:sxjjl:list');

INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
SELECT @name_detail, @size_id, 2, '', NULL, NULL, '', 1, 0, 'F', '0', '0', 'school:sxjjl:query', '#', 'admin', NOW(), '', NULL, @remark_size_detail
WHERE NOT EXISTS (SELECT 1 FROM sys_menu WHERE parent_id = @size_id AND menu_type = 'F' AND perms = 'school:sxjjl:query');

INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
SELECT @name_list, @delivery_id, 1, '', NULL, NULL, '', 1, 0, 'F', '0', '0', 'school:tgfh:list', '#', 'admin', NOW(), '', NULL, @remark_delivery_list
WHERE NOT EXISTS (SELECT 1 FROM sys_menu WHERE parent_id = @delivery_id AND menu_type = 'F' AND perms = 'school:tgfh:list');

-- Grant menus to roles that already had related tonggou access.
INSERT INTO sys_role_menu (role_id, menu_id)
SELECT DISTINCT roles.role_id, menus.menu_id
FROM (
  SELECT DISTINCT rm.role_id
  FROM sys_role_menu rm
  JOIN sys_menu m ON m.menu_id = rm.menu_id
  WHERE rm.role_id <> 0
    AND (
      m.menu_id IN (@tonggou_id, @notice_id, @quality_id)
      OR m.parent_id IN (@notice_id, @quality_id)
      OR m.perms IN (
        'school:groupNotice:list', 'school:groupNotice:add', 'school:groupNotice:query',
        'school:tgjd:list', 'school:tgjd:query',
        'school:zjbg:list', 'school:zjbg:query',
        'school:sxjjl:list', 'school:sxjjl:query',
        'school:tgfh:list'
      )
    )
) roles
CROSS JOIN (
  SELECT menu_id FROM sys_menu
  WHERE menu_id IN (@new_feature_id, @tonggou_id, @notice_id, @progress_id, @quality_id, @size_id, @delivery_id)
     OR parent_id IN (@notice_id, @progress_id, @quality_id, @size_id, @delivery_id)
) menus
WHERE NOT EXISTS (
  SELECT 1 FROM sys_role_menu exists_rm
  WHERE exists_rm.role_id = roles.role_id
    AND exists_rm.menu_id = menus.menu_id
);

COMMIT;

SELECT menu_id, menu_name, parent_id, order_num, path, component, menu_type, perms
FROM sys_menu
WHERE menu_id IN (@new_feature_id, @tonggou_id, @notice_id, @progress_id, @quality_id, @size_id, @delivery_id)
   OR parent_id IN (@notice_id, @progress_id, @quality_id, @size_id, @delivery_id)
ORDER BY parent_id, order_num, menu_id;
