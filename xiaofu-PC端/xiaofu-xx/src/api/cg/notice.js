import request from "@/utils/request";

// 查询采购公告列表
export function listNotice(query) {
  return request({
    url: "/cg/notice/list",
    method: "get",
    params: query,
  });
}

// 查询采购公告详细
export function getNotice(id) {
  return request({
    url: "/cg/notice/" + id,
    method: "get",
  });
}

// 新增采购公告
export function addNotice(data) {
  return request({
    url: "/cg/notice",
    method: "post",
    data: data,
  });
}

// 修改采购公告
export function updateNotice(data) {
  return request({
    url: "/cg/notice",
    method: "put",
    data: data,
  });
}

// 删除采购公告
export function delNotice(id) {
  return request({
    url: "/cg/notice/" + id,
    method: "delete",
  });
}
