import request from "@/utils/request";

// 查询首页公告列表
export function listXfptnotice(query) {
  return request({
    url: "/system/xfptnotice/list",
    method: "get",
    params: query,
  });
}

// 查询首页公告详细
export function getXfptnotice(id) {
  return request({
    url: "/system/xfptnotice/" + id,
    method: "get",
  });
}

// 新增首页公告
export function addXfptnotice(data) {
  // 将参数转换为FormData格式
  const formData = new FormData();
  for (const key in data) {
    if (data[key] !== null && data[key] !== undefined) {
      formData.append(key, data[key]);
    }
  }
  return request({
    url: "/system/xfptnotice",
    method: "post",
    data: formData,
    headers: {
      "Content-Type": "multipart/form-data",
    },
  });
}

// 修改首页公告
export function updateXfptnotice(data) {
  return request({
    url: "/system/xfptnotice",
    method: "put",
    data: data,
  });
}

// 删除首页公告
export function delXfptnotice(id) {
  return request({
    url: "/system/xfptnotice/" + id,
    method: "delete",
  });
}

//查询分类
export function listCategory() {
  return request({
    url: "/system/category/listNotCGGG",
    method: "get",
  });
}
