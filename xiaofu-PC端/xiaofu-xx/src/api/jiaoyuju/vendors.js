import request from "@/utils/request";

//获取厂商列表
export function getVendorsList(params) {
  return request({
    url: "/school/supplierMessage/list",
    method: "get",
    params: params,
  });
}

//添加解除黑名单/school/supplierMessage
export function addOrRemoveBlackList(params) {
  return request({
    url: "/school/supplierMessage",
    method: "put",
    data: params,
  });
}

//查询服务学校列表
export function getSchoolList(params) {
  return request({
    url: "/school/supplierMessage/schoolList",
    method: "get",
    params: params,
  });
}

//获取供应商详情
export function getVendorDetail(params) {
  return request({
    url: "/school/supplierMessage/" + params.id,
    method: "get",
  });
}

//导出数据
export function exportData(params) {
  return request({
    url: "/school/supplierMessage/export",
    method: "get",
    params: params,
  });
}
