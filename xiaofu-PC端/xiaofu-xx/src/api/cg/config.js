import request from "@/utils/request";

// 查询家长建议征集活动配置列表
export function listConfig(query) {
  return request({
    url: "/school/jzbuy/opinions/config/list",
    method: "get",
    params: query,
  });
}

// 查询家长建议征集活动配置详细
export function getConfig(id) {
  return request({
    url: "/school/jzbuy/opinions/config/" + id,
    method: "get",
  });
}

// 新增家长建议征集活动配置
export function addConfig(data) {
  return request({
    url: "/school/jzbuy/opinions/config",
    method: "post",
    data: data,
  });
}

// 修改家长建议征集活动配置
export function updateConfig(data) {
  return request({
    url: "/school/jzbuy/opinions/config",
    method: "put",
    data: data,
  });
}

// 删除家长建议征集活动配置
export function delConfig(id) {
  return request({
    url: "/school/jzbuy/opinions/config/" + id,
    method: "delete",
  });
}

//获取班级列表
export function getClassesList() {
  return request({
    url: "/school/gradeClass/list",
    method: "get",
  });
}
