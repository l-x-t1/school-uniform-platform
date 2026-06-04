import request from "@/utils/request";

// 查询入驻入口上下文（公开）
export function getPublicEntryContext(noticeId) {
  return request({
    url: "/school/supplierSettleApply/public/entryContext",
    method: "get",
    params: { noticeId },
  });
}

// 校验账号并查询绑定厂商信息
export function checkBinding(data) {
  return request({
    url: "/school/supplierSettleApply/checkBinding",
    method: "post",
    headers: {
      isToken: false,
    },
    data,
  });
}

// 公开提交供应商注册入驻申请
export function submitSupplierSettleApply(data) {
  return request({
    url: "/school/supplierSettleApply/public/register",
    method: "post",
    headers: {
      isToken: false,
    },
    data,
  });
}

// 已有账号提交绑定供应商入驻申请
export function submitLoginBoundSupplierSettleApply(data) {
  return request({
    url: "/school/supplierSettleApply/loginBound",
    method: "post",
    headers: {
      isToken: false,
    },
    data,
  });
}
