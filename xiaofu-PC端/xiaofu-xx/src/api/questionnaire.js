import request from "@/utils/request";

/**
 * 获取问卷内容
 * @param {String} id 问卷ID
 * @returns {Promise} 请求Promise对象
 */
export function getQuestionnaireContent(data) {
  return request({
    url: `/school/question/config/content`,
    method: "post",
    params: data,
  });
}

/**
 * 保存问卷调查结果
 * @param {Object} data 问卷调查结果数据
 * @returns {Promise} 请求Promise对象
 */
export function saveQuestionnaireResult(data) {
  return request({
    url: "/school/questionnaireResult",
    method: "post",
    data: data,
  });
}

// 完善或者修改
export function saveSchoolInfo(data) {
  // 使用FormData处理数据
  const formData = new FormData();
  for (const key in data) {
    if (data[key] !== null && data[key] !== undefined) {
      formData.append(key, data[key]);
    }
  }
  data = formData;
  return request({
    url: "/school/schoolMessage",
    method: "post",
    data,
  });
}
//获取学校信息是否需要完善
export function getSchoolInfo(data) {
  return request({
    url: "/school/schoolMessage/isPerfect",
    method: "post",
    params: data,
  });
}

//获取学校详细信息/school/schoolMessage/{deptId}
export function getSchoolInfoDetail(id) {
  return request({
    url: `/school/schoolMessage/${id}`,
    method: "get",
  });
}

//获取是否填写过问卷
export function getQuestionnaireResult(data) {
  // 使用FormData处理数据

  const formData = new FormData();
  formData.append("id", data.id);

  return request({
    url: "/school/questionnaireResult/selIpAddress",
    method: "post",
    data: formData,
    headers: {
      "Content-Type": "multipart/form-data",
    },
  });
}


//查询学生姓名
export function getStudentName(data) {
  return request({
    url: "/school/question/config/selectName",
    method: "get",
    params: data,
  });
}

//验证学生姓名和手机号
export function getStudentNameAndPhone(data) {
  return request({
    url: "/system/open/link/checkUserAndPhone",
    params: data,
    token: false,
  });
}
