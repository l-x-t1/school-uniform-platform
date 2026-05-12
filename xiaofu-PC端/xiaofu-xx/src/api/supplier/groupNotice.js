import request from "@/utils/request";

// 查询供应商可查看的统购公告列表
export function listSupplierGroupNotice(query) {
  return request({
    url: "/school/groupNotice/list",
    method: "get",
    params: query,
  });
}

// 查询供应商可查看的统购公告详情
export function getSupplierGroupNotice(id) {
  return request({
    url: "/school/groupNotice/" + id,
    method: "get",
  });
}
