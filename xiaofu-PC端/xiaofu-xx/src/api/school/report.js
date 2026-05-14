import request from "@/utils/request";

// 查询学校与供应商检验报告关联列表
export function listReport(query) {
  return request({
    url: "/school/report/list",
    method: "get",
    params: query,
  });
}

// 查询学校与供应商检验报告关联详细
export function getReport(id) {
  return request({
    url: "/school/report/" + id,
    method: "get",
  });
}

// 查询学校端质检报告详情
export function getSuppliceInspectionReport(id) {
  return request({
    url: "/school/suppliceInspectionReport/" + id,
    method: "get",
  });
}

// 新增学校与供应商检验报告关联
export function addReport(data) {
  return request({
    url: "/school/report",
    method: "post",
    data: data,
  });
}

// 修改学校与供应商检验报告关联
export function updateReport(data) {
  return request({
    url: "/school/report",
    method: "put",
    data: data,
  });
}

// 删除学校与供应商检验报告关联
export function delReport(id) {
  return request({
    url: "/school/report/" + id,
    method: "delete",
  });
}
// 查看厂商信息
export function getSupplierInfo(id) {
  return request({
    url: "/school/supplierMessage/" + id,
    method: "get",
  });
}
// 添加厂商
export function suppliceInspectionReport(data) {
  return request({
    url: "/school/suppliceInspectionReport",
    method: "post",
    data: data,
  });
}
// 查询厂商列表
export function suppliceInspectionReportList(query) {
  return request({
    url: "/school/suppliceInspectionReport/list",
    method: "get",
    params: query,
  });
}
// 上传质检报告
export function addsuppliceInspectionReportSon(data) {
  return request({
    url: "/school/suppliceInspectionReportSon",
    method: "post",
    data: data,
  });
}
// 删除质检报告
export function delsuppliceInspectionReportSon(id) {
  return request({
    url: "/school/suppliceInspectionReportSon/" + id,
    method: "delete",
  });
}
// 编辑质检报告
export function editsuppliceInspectionReportSon(data) {
  return request({
    url: "/school/suppliceInspectionReportSon",
    method: "put",
    data: data,
  });
}
// 删除厂商
export function delSupplier(id) {
  return request({
    url: "/school/suppliceInspectionReport/" + id,
    method: "delete",
  });
}
// 质检报告列表
export function getReportList(query) {
  return request({
    url: "/school/suppliceInspectionReportSon/list",
    method: "get",
    params: query,
  });
}
