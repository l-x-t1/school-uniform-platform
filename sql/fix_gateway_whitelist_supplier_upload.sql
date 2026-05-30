-- ===============================================
-- 修复：供应商入驻文件上传网关白名单
-- 在 xfglpt-gateway-dev.yml 中添加 /file/settle/**
-- 执行后需重启 xfglpt-gateway 服务
-- ===============================================
SET SQL_SAFE_UPDATES = 0;

UPDATE config_info
SET content = REPLACE(content,
  '- /csrf\n      - /questionnaireResult/selIpAddress',
  '- /csrf\n      - /questionnaireResult/selIpAddress\n      - /file/settle/**')
WHERE id = 2;

SET SQL_SAFE_UPDATES = 1;

-- 验证修改结果
SELECT data_id, content
FROM config_info
WHERE id = 2;