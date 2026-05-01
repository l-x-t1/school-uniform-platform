import request from "@/utils/request";

// 获取订购统概览
export function getOrderStatisticsOverview(params) {
  return request({
    url: "/school/OrderManagement/statistics",
    method: "post",
    data: params,
  });
}

// 根据所选地区获取订购统计数据
export function getOrderStatisticsByArea(params) {
  return request({
    url: "/school/OrderManagement/statisticsInfo",
    method: "post",
    data: params,
  });
}

// 学校分类统计订购管理
export function getOrderStatisticsTypeInfo(params) {
  return request({
    url: "/school/OrderManagement/statisticsTypeInfo",
    method: "post",
    data: params,
  });
}

//查询地区列表
export function getAreaList() {
  return request({
    url: "/school/OrderManagement/selectDeptList",
    method: "get",
  });
}
