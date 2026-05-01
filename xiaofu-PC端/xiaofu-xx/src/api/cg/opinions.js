import request from "@/utils/request";

// 查询家长购买意见征集列表
export function listOpinions(query) {
  return request({
    url: "/school/jzbuy/opinions/list",
    method: "get",
    params: query,
  });
}

// 查询家长购买意见征集详细
export function getOpinions(id) {
  return request({
    url: "/school/jzbuy/opinions/" + id,
    method: "get",
  });
}

// 新增家长购买意见征集
export function addOpinions(data) {
  return request({
    url: "/school/jzbuy/opinions",
    method: "post",
    data: data,
  });
}

// 修改家长购买意见征集
export function updateOpinions(data) {
  return request({
    url: "/school/jzbuy/opinions",
    method: "put",
    data: data,
  });
}

// 删除家长购买意见征集
export function delOpinions(id) {
  return request({
    url: "/school/jzbuy/opinions/" + id,
    method: "delete",
  });
}

// 删除子表
export function delNJGMData(ids) {
  return request({
    url: "/school/jzbuy/opinions/deleteSonData/" + ids,
    method: "delete",
  });
}

//获取投票人员列表
export function getVoteSignatureList(query) {
  return request({
    url: "/school/questionnaireResult/signatureList",
    method: "get",
    params: query,
  });
}
