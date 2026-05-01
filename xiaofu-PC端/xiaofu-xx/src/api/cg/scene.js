import request from "@/utils/request";

// 查询模式一组织确认选购模式列表
export function listOrganization(query) {
  return request({
    url: "/school/organization/list",
    method: "get",
    params: query,
  });
}

// 查询模式一组织确认选购模式详细
export function getOrganization(id) {
  return request({
    url: "/school/organization/" + id,
    method: "get",
  });
}

// 新增模式一组织确认选购模式
export function addOrganization(data) {
  return request({
    url: "/school/organization",
    method: "post",
    data: data,
  });
}

// 修改模式一组织确认选购模式
export function updateOrganization(data) {
  return request({
    url: "/school/organization",
    method: "put",
    data: data,
  });
}

// 删除模式一组织确认选购模式
export function delOrganization(id) {
  return request({
    url: "/school/organization/" + id,
    method: "delete",
  });
}


//撤回发布
export function withdrawOrganization(data) {
  return request({
    url:'/school/organization/withdraw',
    method:'post',
    params:data
  })
}