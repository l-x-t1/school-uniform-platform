import request from "@/utils/request";

// 查询家长反馈列表
export function listParentsFeedback(query) {
  return request({
    url: "/school/parentsFeedback/list",
    method: "get",
    params: query,
  });
}

// 查询家长反馈详细
export function getParentsFeedback(id) {
  return request({
    url: "/school/parentsFeedback/" + id,
    method: "get",
  });
}

// 新增家长反馈
export function addParentsFeedback(data) {
  return request({
    url: "/school/parentsFeedback",
    method: "post",
    data: data,
  });
}

// 修改家长反馈
export function updateParentsFeedback(data) {
  return request({
    url: "/school/parentsFeedback",
    method: "put",
    data: data,
  });
}

// 删除家长反馈
export function delParentsFeedback(id) {
  return request({
    url: "/school/parentsFeedback/" + id,
    method: "delete",
  });
}
