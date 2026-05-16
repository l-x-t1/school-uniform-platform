import request from "@/utils/request";

// 查询供应商信息列表
export function listSupplierMessage(query) {
  return request({
    url: "/school/supplierMessage/list",
    method: "get",
    params: query,
  });
}

// 查询供应商信息详细
export function getSupplierMessage(id) {
  return request({
    url: "/school/supplierMessage/" + id,
    method: "get",
  });
}

// 修改供应商信息
export function updateSupplierMessage(data) {
  return request({
    url: "/school/supplierMessage",
    method: "put",
    data,
  });
}
