import request from "@/utils/request";

//获取学校信息列表
export const getSchoolList = (params) => {
  return request({
    url: "/system/dept/complete",
    method: "get",
    params: params,
  });
};

//获取纳入系统统一学校列表
export const getSchoolSecondLevelList = (params) => {
  return request({
    url: "/school/schoolMessage/schoolSecondLevel",
    method: "post",
    data: params,
  });
};
//获取学校列表
export const getSchoolListCompleteDetail = (params) => {
  return request({
    url: "/school/schoolMessage/completeDetail",
    method: "get",
    params: params,
  });
};


//获取学校详情
export function getSchoolDetail(id){
  return request({
    url: "/school/schoolMessage/getInfo2/" + id,
    method: "get", 
  });
}
