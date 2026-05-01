import request from "@/utils/request";

// 查询问卷调查库列表
export function listSurvey(query) {
  return request({
    url: "/school/question/config/list",
    method: "get",
    params: query,
  });
}
//查询问卷列表
export function listSurveyNew(){
  return request({
    url: "/school/question/config/getQuestionnaireTemplateConfig",
    method: "get", 
  });
}



// 查询问卷调查库详细
export function getSurvey(id) {
  return request({
    url: "/school/question/config/" + id,
    method: "get",
  });
}

// 新增问卷调查库
export function addSurvey(data) {
  return request({
    url: "/school/question/config",
    method: "post",
    data: data,
  });
}

// 修改问卷调查库
export function updateSurvey(data) {
  return request({
    url: "/school/question/config",
    method: "put",
    data: data,
  });
}

// 删除问卷调查库
export function delSurvey(id) {
  return request({
    url: "/school/question/config/" + id,
    method: "delete",
  });
}
// 删除问卷内部数据
export function delSurveyData(id) {
  return request({
    url: "/school/question/config/removeSonData/" + id,
    method: "delete",
  });
}

// 获取问卷统计数据
export function getSurveyStatistics(data) {
  return request({
    url: "/school/question/config/statistics",
    method: "get",
    params: data,
  });
}
