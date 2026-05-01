import request from "@/utils/request";

// 查询选用组织确定选购模式会议列表
export function listRepmode(query) {
  return request({
    url: "/school/repmode/list",
    method: "get",
    params: query,
  });
}

// 查询选用组织确定选购模式会议详细
export function getRepmode(id) {
  return request({
    url: "/school/repmode/" + id,
    method: "get",
  });
}

// 新增选用组织确定选购模式会议
export function addRepmode(data) {
  return request({
    url: "/school/repmode",
    method: "post",
    data: data,
  });
}

// 修改选用组织确定选购模式会议
export function updateRepmode(data) {
  return request({
    url: "/school/repmode",
    method: "put",
    data: data,
  });
}

// 删除选用组织确定选购模式会议
export function delRepmode(id) {
  return request({
    url: "/school/repmode/" + id,
    method: "delete",
  });
}
