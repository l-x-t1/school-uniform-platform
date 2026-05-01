import request from "@/utils/request";

//根据分类标识文章列表
export function getArticleList(params) {
  return request({
    url: "/system/open/link/noticeList",
    method: "get",
    params,
  });
}

//查询公告栏目列表
export function getNoticeColumnList(params) {
  return request({
    url: "/system/open/link/noticeCategory",
    method: "get",
    params,
  });
}

//查询文章详情
export function getArticleDetail(id) {
  return request({
    url: "/system/open/link/noticeDetail?id=" + id,
    method: "get",
  });
}

//增加阅读量
export function addReadCount(id) {
  return request({
    url: "/system/open/link/readCount?id=" + id,
    method: "get",
  });
}

//查询学校列表
export function getSchoolList(params) {
  return request({
    url: "/system/open/link/getSchoolList",
    method: "get",
    params,
  });
}

//查询轮播图列表
export function getLbimageList() {
  return request({
    url: "/system/open/link/carouselList?type=0",
    method: "get",
  });
}

//查询学校详情
export function getSchoolDetail(id) {
  return request({
    url: "/system/open/link/getSchoolInfo?id=" + id,
    method: "get",
  });
}

//获取区县列表
export function getDeptList() {
  return request({
    url: "/system/open/link/getDeptList",
    method: "get",
  });
}

//提交意见反馈
export function feedback(data) {
  return request({
    url: "/school/school/open/addYjjy",
    method: "post",
    data,
  });
}

//获取意见反馈列表
export function getFeedbackList(params) {
  return request({
    url: "/system/open/link/getfeednackList",
    method: "get",
    params,
  });
}

//获取意见反馈详情
export function getFeedbackDetail(id) {
  return request({
    url: "/system/open/link/getfeednackInfo?id=" + id,
    method: "get",
  });
}

//内容搜索
export function searchContent(data) {
  const formDate = new FormData();
  formDate.append("name", data.name);
  return request({
    url: "/system/open/link/search",
    method: "post",
    data: formDate,
    headers: {
      "Content-Type": "multipart/form-data",
    },
  });
}

 

