import request from "@/utils/request";

// 查询采购历史数据列表
export function listOldData(query) {
  return request({
    url: "/school/oldData/list",
    method: "get",
    params: query,
  });
}

// 查询采购历史数据详细
export function getOldData(id) {
  return request({
    url: "/school/oldData/" + id,
    method: "get",
  });
}

// 新增采购历史数据
export function addOldData(data) {
  return request({
    url: "/school/oldData",
    method: "post",
    data: data,
  });
}

// 修改采购历史数据
export function updateOldData(data) {
  return request({
    url: "/school/oldData",
    method: "put",
    data: data,
  });
}

// 删除采购历史数据
export function delOldData(id) {
  return request({
    url: "/school/oldData/" + id,
    method: "delete",
  });
}
