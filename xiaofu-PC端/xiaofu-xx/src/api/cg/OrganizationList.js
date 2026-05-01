import request from "@/utils/request";

// 查询选用组织名单报备与公开列表
export function listDisclose(query) {
  return request({
    url: "/school/org/report/disclose/list",
    method: "get",
    params: query,
  });
}

// 查询选用组织名单报备与公开详细
export function getDisclose(tId) {
  return request({
    url: "/school/org/report/disclose/" + tId,
    method: "get",
  });
}

// 新增选用组织名单报备与公开
export function addDisclose(data) {
  return request({
    url: "/school/org/report/disclose/add",
    method: "post",
    data: data,
  });
}

// 修改选用组织名单报备与公开
export function updateDisclose(data) {
  return request({
    url: "/school/org/report/disclose/edit",
    method: "put",
    data: data,
  });
}

// 删除选用组织名单报备与公开
export function delDisclose(tId) {
  return request({
    url: "/school/org/report/disclose/" + tId,
    method: "delete",
  });
}

//删除子表数据
export function delSonData(ids) {
  return request({
    url: "/school/org/report/disclose/deleteSonData/" + ids,
    method: "delete",
  });
}
