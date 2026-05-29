import request from "@/utils/request";

// 查询待审核的入驻申请列表
export function auditList(query) {
  return request({
    url: "/school/supplierSettleApply/audit/list",
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