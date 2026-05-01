import request from "@/utils/request";

// 查询班子会议列表
export function listMeeting(query) {
  return request({
    url: "/school/team/meeting/list",
    method: "get",
    params: query,
  });
}

// 查询班子会议详细
export function getMeeting(id) {
  return request({
    url: "/school/team/meeting/" + id,
    method: "get",
  });
}

// 新增班子会议
export function addMeeting(data) {
  return request({
    url: "/school/team/meeting/add",
    method: "post",
    data: data,
  });
}

// 修改班子会议
export function updateMeeting(data) {
  return request({
    url: "/school/team/meeting/edit",
    method: "post",
    data: data,
  });
}

// 删除班子会议
export function delMeeting(id) {
  return request({
    url: "/school/team/meeting/" + id,
    method: "delete",
  });
}

//撤回
export function modifyJyjStatus(data) {
  return request({
    url: "/school/team/meeting/modifyJyjStatus",
    method: "post",
    data,
  });
}
//删除班子会议id
// 删除班子会议id
export function deleteSonDataById(id) {
  return request({
    url: "/school/team/meeting/deleteSonDataById/" + id,
    method: "delete",
  });
}

//获取班子会议选购需求/jzbuy/opinions/getTeamXGCQ/1
export function getTeamXGCQ(id) {
  return request({
    url: "/school/jzbuy/opinions/getTeamXGCQ/" + id,
    method: "get",
  });
}
//获取本学校的年级列表
export function getCurrSchoolGradeList() {
  return request({
    url: "/school/gradeClass/getCurrSchoolGradeList",
    method: "get",
  });
}

//获取流程
export function getModeByTeamId(id) {
  return request({
    url: "/school/repmode/getModeByTeamId?teamId=" + id,
    method: "get",
  });
}
//获取流程进度状态
export function getProcessMenu(id) {
  return request({
    url: "/school/team/meeting/getProcessMenu?teamId=" + id,
    method: "get",
  });
}


//查询调查问卷  1意见征集   2满意度调查  4评定小组确定厂商
export function getConfigByType(type) {
  return request({
    url: "/school/question/config/getConfigByType?type=" + type,
    method: "get",
  });
}


//查询意见征集模板
export function getOpinionsMoban() {
  return request({
    url: "/school/jzbuy/opinions/lastConfig",
    method: "get",
  });
}


//查询问卷字段列表
export function getZiduanList(id) {
  return request({
    url: "/school/questionnaireSurvey/headingList?questionId=" + id,
    method: "get",
  });
}


//导出问卷结果表格
export function wjDaochu(id) {
  return request({
    url: "/school/questionnaireSurvey/exportResultExcel?questionId=" + id,
    method: "get",
  });
}

//获取采购类型
export function getCgType(id) {
  return request({
    url: "/school/team/meeting/getStatus?teamId=" + id,
    method: "get",
  });
}
