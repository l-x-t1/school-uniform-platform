import request from "@/utils/request";

// 查询学校与供应商检验报告从列表
export function listSon(query) {
  return request({
    url: "/school/son/list",
    method: "get",
    params: query,
  });
}

// 查询学校与供应商检验报告从详细
export function getSon(id) {
  return request({
    url: "/school/son/" + id,
    method: "get",
  });
}

// 新增学校与供应商检验报告从
export function addSon(data) {
  return request({
    url: "/school/son",
    method: "post",
    data: data,
  });
}

// 修改学校与供应商检验报告从
export function updateSon(data) {
  return request({
    url: "/school/son",
    method: "put",
    data: data,
  });
}

// 删除学校与供应商检验报告从
export function delSon(id) {
  return request({
    url: "/school/son/" + id,
    method: "delete",
  });
}
// 添加问卷调查内容
export function questionnaireSurvey(query) {
  return request({
    url: "/school/questionnaireSurvey/list",
    method: "get",
    params: query,
  });
}

// 兼容现有页面中的旧导出名
export function listQuestionnaire(query) {
  return questionnaireSurvey(query);
}
// 年级列表
export function gradeList(query) {
  return request({
    url: "/school/gradeClass/list",
    method: "get",
    params: query,
  });
}
// 新增班级年级
export function addGradeClass(data) {
  return request({
    url: "/school/gradeClass",
    method: "post",
    data: data,
  });
}
// 修改班级年级
export function updateGradeClass(data) {
  return request({
    url: "/school/gradeClass",
    method: "put",
    params: data,
  });
}
// 删除班级年级
export function delGradeClass(id) {
  return request({
    url: "/school/gradeClass/" + id,
    method: "delete",
  });
}
// 添加总结报告
export function addSummarizeReport(data) {
  return request({
    url: "/school/satisfactionSummarize",
    method: "post",
    data: data,
  });
}
// 编辑问卷调查结果
export function editQuestionnaireSurvey(data) {
  return request({
    url: "/school/satisfactionSummarize",
    method: "put",
    data: data,
  });
}
// 总结报告列表
export function summarizeReportList(query) {
  return request({
    url: "school/satisfactionSummarize/list",
    method: "get",
    params: query,
  });
}
// 获取总结报告详情
export function getSummarizeReportDetail(id) {
  return request({
    url: "/school/satisfactionSummarize/" + id,
    method: "get",
  });
}

//删除总结报告
export function delSummarizeReport(id) {
  return request({
    url: "/school/satisfactionSummarize/" + id,
    method: "delete",
  });
}
