package com.xfgl.auth.mp;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.Header;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson2.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;


@Slf4j
@Component
public class MpUtil {

    @Value("${mp.ip:}")
    private String ip;

    @Value("${mp.keyId:}")
    private String keyId;

    @Value("${mp.clientId:}")
    private String clientid;

    @Value("${mp.clientSecret:}")
    private String clientSecret;

    private void validateConfig() {
        if (StrUtil.hasBlank(ip, keyId, clientid, clientSecret)) {
            throw new IllegalStateException("mp configuration is missing, please configure mp.ip, mp.keyId, mp.clientId and mp.clientSecret");
        }
    }

    public String getToken(){
        validateConfig();
        String clientId = clientid+":"+clientSecret;
        String clientIdBase64 = RequestUtils.base64Encode(clientId.getBytes());
        HashMap<String, Object> parm=new HashMap<>();
        parm.put("grant_type","client_credentials");
        parm.put("scope","all");
        log.info("==========获取token==========：{}",ip+"/auth/oauth/token");
        String body = HttpUtil.createPost(ip+"/auth/oauth/token")
                .form(parm)
                .header(Header.AUTHORIZATION,"Basic "+clientIdBase64)
                .execute()
                .body();
        JSONObject parse = JSONObject.parseObject(body);
        String accessToken = parse.getString("access_token");
        if(StrUtil.isEmpty(accessToken)){
            log.error("获取token错误:{}",body);
            return "";
        }
        return accessToken;
    }

    /***
     * 加密
     * @param plaintext 加密内容
     * @return
     */
    public String encrypt(String plaintext,String token){
        plaintext=RequestUtils.base64Encode(plaintext.getBytes());
        HashMap<String, Object> parm=new HashMap<>();
        parm.put("alg","SM4_ECB");
        parm.put("keyIndex",keyId);
        parm.put("data",plaintext);
        log.info("==========数据加密请求地址==========：{}",ip+"/auth/oauth/encrypt");
        if(StrUtil.isEmpty(token)){
            token = getToken();
        }
        String body = HttpUtil.createPost(ip+"/dispatch/api/encrypt")
                .form(parm)
                .header(Header.AUTHORIZATION,"Bearer "+token)
                .execute()
                .body();
        JSONObject parse = JSONObject.parseObject(body);
        String data = parse.getString("data");
        if(StrUtil.isEmpty(data)){
            log.error("数据加密错误:{}",body);
            return "";
        }
        JSONObject jsonObject = JSONObject.parseObject(data);
        String ciphertext = jsonObject.getString("encryptData");
        return ciphertext;
    }
    /***
     * 解密
     * @param encryptData 解密内容
     * @return
     */
    public String decrypt(String encryptData,String token){
        //encryptData=RequestUtils.base64Encode(encryptData.getBytes());
        HashMap<String, Object> parm=new HashMap<>();
        parm.put("alg","SM4_ECB");
        parm.put("keyIndex",keyId);
        parm.put("encryptData",encryptData);
        log.info("==========数据解密请求地址==========：{}",ip+"/dispatch/api/decrypt");
        if(StrUtil.isEmpty(token)){
             token = getToken();
        }

        String body = HttpUtil.createPost(ip+"/dispatch/api/decrypt")
                .form(parm)
                .header(Header.AUTHORIZATION,"Bearer "+token)
                .execute()
                .body();
        JSONObject parse = JSONObject.parseObject(body);
        String data = parse.getString("data");
        if(StrUtil.isEmpty(data)){
            log.error("数据加密错误:{}",body);
            return "";
        }
        JSONObject jsonObject = JSONObject.parseObject(data);
        String ciphertext = jsonObject.getString("encryptData");
        byte[] decode = Base64.getDecoder().decode(ciphertext);
        String string = new String(decode);
        return string;
    }


    public String mac(String text){
        text=RequestUtils.base64Encode(text.getBytes());
        HashMap<String, Object> parm=new HashMap<>();
        parm.put("alg","SM4_MAC");
        parm.put("keyId",keyId);
        parm.put("data",text);
        log.info("==========完整性请求地址==========：{}",ip+"/dispatch/api/mac");
        String token = getToken();
        String body = HttpUtil.createPost(ip+"/dispatch/api/mac")
                .form(parm)
                .header(Header.AUTHORIZATION,"Bearer "+token)
                .execute()
                .body();
        JSONObject parse = JSONObject.parseObject(body);
        String data = parse.getString("data");
        if(StrUtil.isEmpty(data)){
            log.error("数据加密错误:{}",body);
            return "";
        }
        JSONObject jsonObject = JSONObject.parseObject(data);
        String ciphertext = jsonObject.getString("mac");

        return ciphertext;
    }
}
