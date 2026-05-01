import request from "@/utils/request";

// 查询选用组织选购结果公示报备与公开列表
export function listResults(query) {
  return request({
    url: "/school/results/list",
    method: "get",
    params: query,
  });
}

// 查询选用组织选购结果公示报备与公开详细
export function getResults(id) {
  return request({
    url: "/school/results/" + id,
    method: "get",
  });
}

// 新增选用组织选购结果公示报备与公开
export function addResults(data) {
  return request({
    url: "/school/results",
    method: "post",
    data: data,
  });
}

// 修改选用组织选购结果公示报备与公开
export function updateResults(data) {
  return request({
    url: "/school/results",
    method: "put",
    data: data,
  });
}

// 删除选用组织选购结果公示报备与公开
export function delResults(id) {
  return request({
    url: "/school/results/" + id,
    method: "delete",
  });
}

// 查询选用组织选购结果公示报备与公开列表
export function procurementNoticeList(query) {
  return request({
    url: "/school/results/procurementNoticeList",
    method: "get",
    params: query,
  });
}
// 添加评标结果
export function addBidEvaluationResults(data) {
  return request({
    url: "/school/mode",
    method: "post",
    data: data,
  });
}
