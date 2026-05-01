import request from "@/utils/request";
import { tansParams } from "@/utils/ruoyi";

/**
 * CA证书登录相关API
 */

// CA证书登录 - 获取随机数
export function getCaRandom() {
  return request({
    url: "/system/getRandom", 
    headers: {
      isToken: false,
    },
    method: "get",
  });
}

// CA证书登录 - 验证签名并登录
export function caLogin(data) {
  return request({ 
    url: "/system/verifyCertAndSign",
    headers: {
      isToken: false,
      repeatSubmit: false,
      'Content-Type': 'application/x-www-form-urlencoded',
    },
    method: "post",
    data: tansParams(data),  // 转换为表单格式提交
  });
}



