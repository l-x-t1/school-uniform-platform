import request from "@/utils/request";

//获取数据
export function getStatisticsDataList(data) {
  return request({
    url: "/school/team/meeting/getStatisticsDataList",
    method: "get",
    params: data,
  });
}

//获取报备情况
export function getSchoolBaoBeiSituationStatistics(data) {
  return request({
    url: "/school/team/meeting/getSchoolBaoBeiSituation",
    method: "get",
    params: data,
  });
}

//获取学校类别
export function getStatisticsSchoolDataList(data) {
  return request({
    url: "/school/team/meeting/getStatisticsSchoolDataList",
    method: "get",
    params: data,
  });
}
//获取部门
export function getDeptListByParentDeptId(data) {
  return request({
    url: "/school/edu/manager/statistics/getDeptListByParentDeptId",
    method: "get",
    params: data,
  });
}
//公告质检报告统计
export function getNoticeStatisticsDataList(data) {
  return request({
    url: "/school/edu/manager/statistics/getNoticeStatisticsDataList",
    method: "get",
    params: data,
  });
}

//学校质检报告统计
export function getSchoolZhiJianReportStatistics(data) {
  return request({
    url: "/school/edu/manager/statistics/getJYNoticeSchoolStatisticsList",
    method: "get",
    params: data,
  });
}

//获取对应的公告附件
export function getNoticeInspectSonFiles(data) {
  return request({
    url: "/school/edu/manager/statistics/getNoticeInspectSonFiles",
    method: "get",
    params: data,
  });
}
