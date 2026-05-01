import request from "@/utils/request";

//获取人数金额统计
export const getPersonMoney = () => {
  return request({
    url: "/school/index/statistics/peopleCount",
    method: "get",
  });
};

//获取今年订购人数
export function getOrderCount() {
  return request({
    url: "/school/index/statistics/getCount",
    method: "get",
  });
}

//获取今年金额统计柱状图
export function getOrderAmount() {
  return request({
    url: "/school/index/statistics/getMoney",
    method: "get",
  });
}

//查询是否需要修改密码
export function isChangePassword(userName) {
  return request({
    url: `/system/user/isPwdExpire?username=${userName}`,
    method: "get",
  });
}




//学校订购人数统计
export function getSchoolOrderCount() {
  return request({
    url: "/school/index/statistics/getSchool5YearCountData",
    method: "get",
  });
}


//学校订购金额统计
export function getSchoolOrderAmount() {
  return request({
    url: "/school/index/statistics/getSchool5YearMoneyData",
    method: "get",
  });
}


//查询校服品类种类统计图
export function getSchoolUniformType() {
  return request({
    url: "/school/index/statistics/schoolUniformType",
    method: "get",
  });
}


