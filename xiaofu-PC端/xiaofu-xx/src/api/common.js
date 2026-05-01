import request from "@/utils/request";

/**
 * 通用数据列表获取
 * @param {Object} query 查询参数
 * @param {String} url 请求地址
 * @returns {Promise} 请求Promise对象
 */
export function listData(query, url) {
  return request({
    url: url,
    method: "get",
    params: query,
  });
}

/**
 * 获取字典数据
 * @param {String} dictType 字典类型
 * @returns {Promise} 请求Promise对象
 */
export function getDicData(dictType) {
  return request({
    url: "/system/dict/data/type/" + dictType,
    method: "get",
  });
}

/**
 * 获取部门树列表
 * @returns {Promise} 请求Promise对象
 */
export function deptTreeSelect() {
  return request({
    url: "/system/dept/treeselect",
    method: "get",
  });
}

/**
 * 获取用户列表
 * @param {Object} query 查询参数
 * @returns {Promise} 请求Promise对象
 */
export function listUser(query) {
  return request({
    url: "/system/user/list",
    method: "get",
    params: query,
  });
}

/**
 * 上传文件
 * @param {FormData} data 上传数据
 * @returns {Promise} 请求Promise对象
 */
export function uploadFile(data) {
  return request({
    url: "/system/file/upload",
    method: "post",
    data: data,
    headers: {
      "Content-Type": "multipart/form-data",
    },
  });
}

/**
 * 下载文件
 * @param {String} fileId 文件ID
 * @returns {Promise} 请求Promise对象
 */
export function downloadFile(fileId) {
  return request({
    url: "/system/file/download/" + fileId,
    method: "get",
    responseType: "blob",
  });
}

//富文本上传视频
export function saveFile(fileId) {
  return request({
    url: "/system/file/download/" + fileId,
    method: "get",
    responseType: "blob",
  });
}
