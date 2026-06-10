import request, { download } from "@/utils/request";

// 查询待审核的入驻申请列表
export function auditList(query) {
  return request({
    url: "/school/supplierSettleApply/audit/list",
    method: "get",
    params: query,
  });
}

// 查询已审核通过的厂商入驻列表
export function approvedList(query) {
  return request({
    url: "/school/supplierSettleApply/audit/approved/list",
    method: "get",
    params: query,
  });
}

// 查询已处理的厂商入驻列表
export function processedList(query) {
  return request({
    url: "/school/supplierSettleApply/audit/processed/list",
    method: "get",
    params: query,
  });
}

// 审核通过
export function approveApply(id) {
  return request({
    url: "/school/supplierSettleApply/audit/approve/" + id,
    method: "put",
  });
}

// 审核驳回
export function rejectApply(id, remark) {
  return request({
    url: "/school/supplierSettleApply/audit/reject/" + id,
    method: "put",
    params: { remark: remark || "" },
  });
}

// 导出已审核通过的厂商入驻列表
export function exportApprovedApply(query) {
  return download(
    "/school/supplierSettleApply/audit/approved/export",
    query,
    `approved_supplier_settle_${new Date().getTime()}.xlsx`
  );
}

// 导出已处理的厂商入驻列表
export function exportProcessedApply(query) {
  return download(
    "/school/supplierSettleApply/audit/processed/export",
    query,
    `processed_supplier_settle_${new Date().getTime()}.xlsx`
  );
}
