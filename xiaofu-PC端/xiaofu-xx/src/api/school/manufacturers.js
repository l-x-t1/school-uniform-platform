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

// 新增供应商信息
export function addSupplierMessage(data) {
  return request({
    url: "/school/supplierMessage",
    method: "post",
    data: data,
  });
}

// 修改供应商信息
export function updateSupplierMessage(data) {
  return request({
    url: "/school/supplierMessage",
    method: "put",
    data: data,
  });
}

// 删除供应商信息
export function delSupplierMessage(id) {
  return request({
    url: "/school/supplierMessage/" + id,
    method: "delete",
  });
}

// 查询供应商绑定的用户列表
export function getSupplierUsers(supplierId) {
  return request({
    url: "/school/supplierMessage/" + supplierId + "/users",
    method: "get",
  });
}

// 绑定用户到供应商
export function bindSupplierUser(data) {
  return request({
    url: "/school/supplierMessage/bindUser",
    method: "post",
    data: data,
  });
}

// 解除用户与供应商的绑定
export function unbindSupplierUser(data) {
  return request({
    url: "/school/supplierMessage/unbindUser",
    method: "delete",
    data: data,
  });
}