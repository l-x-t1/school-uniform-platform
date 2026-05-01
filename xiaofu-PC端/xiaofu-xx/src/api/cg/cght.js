import request from "@/utils/request";

// 查询合作厂商列表
export function supplierMessage(query) {
  return request({
    url: "/school/supplierMessage/list",
    method: "get",
    params: query,
  });
}
// 添加合作厂商
export function addSupplieGYS(data) {
  return request({
    url: "/school/suppliceContract",
    method: "post",
    data,
  });
}
// 学校与厂商绑定列表
export function schoolSupplierBindList(query) {
  return request({
    url: "/school/suppliceContract/list",
    method: "get",
    params: query,
  });
}
// }查看厂商信息
export function getSupplierInfo(id) {
  return request({
    url: "/school/supplierMessage/" + id,
    method: "get",
  });
}
// 取消合作
export function cancelSupplierMessage(id) {
  return request({
    url: "/school/suppliceContract/" + id,
    method: "delete",
  });
}
// 合同列表
export function suppliceContractSon(data) {
  return request({
    url: "/school/suppliceContractSon/list",
    method: "get",
    params: data,
  });
}
// 添加合同
export function addSuppliceContractSon(data) {
  return request({
    url: "/school/suppliceContractSon",
    method: "post",
    data,
  });
}
// 删除合同
export function deleteSuppliceContractSon(id) {
  return request({
    url: "/school/suppliceContractSon/" + id,
    method: "delete",
  });
}
// 编辑合同
export function editSuppliceContractSon(data) {
  return request({
    url: "/school/suppliceContractSon",
    method: "put",
    data,
  });
}
// 下载zip
export function downloadZip(data) {
  return request({
    url: "/school/suppliceContractSon/downloadZip",
    method: "post",
    data,
  });
}
