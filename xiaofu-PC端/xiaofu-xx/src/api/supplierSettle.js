import request from "@/utils/request";

export function submitSupplierSettleApply(data) {
  return request({
    url: "/school/supplierSettleApply",
    method: "post",
    headers: {
      isToken: false,
    },
    data,
  });
}
