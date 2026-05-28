import request from "@/utils/request";

export function listSchoolMessage(query) {
  return request({
    url: "/school/schoolMessage/list",
    method: "get",
    params: query,
  });
}

export function getSchoolMessageByDeptId(deptId) {
  return request({
    url: "/school/schoolMessage/" + deptId,
    method: "get",
  });
}
