package com.xfgl.system.tool;


import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.xfgl.common.core.utils.TokenUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

@Slf4j
@Component
public class SendSms {
    @Value("${sms.url}")
    private String url;
    @Value("${sms.appcode}")
    private String appcode;
    @Value("${sms.appSecret}")
    private String appSecret;
    @Value("${sms.templateId}")
    private String templateId;

    public Boolean send(String phone,String code){

        SimpleDateFormat sdf=new SimpleDateFormat("yyyyMMddHHmmssSSS");
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss SSS");
        //计算token的四个值

        String transId=null;//时间戳+随机数
        String timestamp=df.format(new Date());//时间戳


        //生成随机数
        String s = String.valueOf((Math.random() * 9 + 1) * 100000).substring(0,6);
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(sdf.format(new Date()));
        stringBuffer.append(s);
        transId=stringBuffer.toString();
        //
        String token = null;
        try {
            token = TokenUtil.tokenValue(appcode, timestamp, transId, appSecret);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        //输出
        log.info("appcode是：{}",appcode);
        log.info("timestamp是：{}",timestamp);
        log.info("transId是：{}",transId);
        log.info("计算的token是：{}",token);
        HashMap<String, Object> parm2 = getStringObjectHashMap(phone, code);
//        HashMap<String,String> header=new HashMap<>();
//        header.put("appCode",appcode);
//        header.put("Content-Type","application/json");
//        header.put("token",token);
//        header.put("timestamp",timestamp);
//        header.put("transId",transId);
        HttpRequest httpRequest = getHttpRequest(token, timestamp, transId, parm2);
        String url1 = httpRequest.getUrl();
        log.info("请求的url是：{}",url1);
        HttpResponse execute = httpRequest.execute();
        String body = execute.body();
        log.info("发送短信的返回结果是：{}",body);
        JSONObject jsonObject = JSONObject.parseObject(body);
        if("0".equals(jsonObject.getString("code"))){
            return true;
        }else {
            return false;
        }

    }

    private HashMap<String, Object> getStringObjectHashMap(String phone, String code) {
        HashMap<String,String> codeParm=new HashMap<>();
        codeParm.put("code", code);
        HashMap<String,Object> parm=new HashMap<>();
        parm.put("phoneNum", phone);
        parm.put("sendPlatform","10004");
        parm.put("sendTime","");
        parm.put("templateId",templateId);
        parm.put("templateParam", JSON.toJSONString(codeParm));
        HashMap<String,Object> parm2=new HashMap<>();
        parm2.put("sendOnlyMessageReq",parm);
        return parm2;
    }

    private HttpRequest getHttpRequest(String token, String timestamp, String transId, HashMap<String, Object> parm2) {
        HttpRequest httpRequest = HttpUtil.createPost(url).contentType("application/json");
        httpRequest.header("appCode",appcode);
        httpRequest.header("Content-Type","application/json");
        httpRequest.header("token", token);
        httpRequest.header("timestamp", timestamp);
        httpRequest.header("transId", transId);
        httpRequest.body(JSONObject.toJSONString(parm2));
        return httpRequest;
    }


}
