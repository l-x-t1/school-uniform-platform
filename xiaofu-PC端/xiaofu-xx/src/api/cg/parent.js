import request from "@/utils/request";

// 查询选用组织组织家长投票确定企业模式(二)列表
export function listParent(query) {
  return request({
    url: "/school/parent/list",
    method: "get",
    params: query,
  });
}

// 查询选用组织组织家长投票确定企业模式(二)详细
export function getParent(id) {
  return request({
    url: "/school/parent/" + id,
    method: "get",
  });
}

// 新增选用组织组织家长投票确定企业模式(二)
export function addParent(data) {
  return request({
    url: "/school/parent",
    method: "post",
    data: data,
  });
}

// 修改选用组织组织家长投票确定企业模式(二)
export function updateParent(data) {
  return request({
    url: "/school/parent",
    method: "put",
    data: data,
  });
}

// 删除选用组织组织家长投票确定企业模式(二)
export function delParent(id) {
  return request({
    url: "/school/parent/" + id,
    method: "delete",
  });
}
