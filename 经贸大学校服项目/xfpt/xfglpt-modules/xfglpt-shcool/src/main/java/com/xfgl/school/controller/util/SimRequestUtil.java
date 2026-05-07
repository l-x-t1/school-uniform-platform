package com.xfgl.school.controller.util;

import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpRequest;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.xfgl.school.domain.XfptSimUser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;

@Slf4j
@Component
public class SimRequestUtil {

    @Value("${sim.ip:}")
    private String simIp;

    @Value("${sim.getCode:}")
    private String simGetCodeUri;

    @Value("${sim.getToken:}")
    private String simGetTokenUri;

    @Value("${sim.redirecturi:}")
    private String simRedirectUri;

    @Value("${sim.getUser:}")
    private String simGetUserUri;

    @Value("${sim.clientId:}")
    private String clientId;
    @Value("${sim.clientSecret:}")
    private String clientSecret;

    public void getCode() {

        LinkedHashMap urlParams = new LinkedHashMap();
        urlParams.put("client_id", clientId);
        urlParams.put("client_secret", clientSecret);
        urlParams.put("response_type", "code");
        urlParams.put("redirect_uri", simRedirectUri);
        urlParams.put("scope", "read");
        String join = MapUtil.join(urlParams, "&", "=");
        HttpRequest request = HttpRequest.post(StrUtil.format("{}{}?{}", simIp, simGetCodeUri, join));
        log.info("请求sim获取codeUrl:{}", request.getUrl());
        String body = request.execute().body();
        log.info("请求sim获取code返回:{}", body);
        //TODO 返回失败的结果
    }

    /**
     * sim获取token
     * @param code
     * @return
     */
    public String getToken(String code) {
        LinkedHashMap urlParams = new LinkedHashMap();
        urlParams.put("client_id", clientId);
        urlParams.put("client_secret", clientSecret);
        urlParams.put("grant_type", "authorization_code");
        urlParams.put("code", code);
        urlParams.put("redirect_uri", simRedirectUri);
        String join = MapUtil.join(urlParams, "&", "=");
        HttpRequest request = HttpRequest.post(StrUtil.format("{}{}?{}", simIp, simGetTokenUri, join));
        log.info("请求sim获取tokenUrl:{}", request.getUrl());
        String body = request.execute().body();
        log.info("请求sim获取token返回:{}", body);

        return body;
    }

    public String getUser(String accessToken,String userName) {
        LinkedHashMap urlParams = new LinkedHashMap();
        urlParams.put("access_token", accessToken);
        String userUri=simGetUserUri+"/"+userName;
        String join = MapUtil.join(urlParams, "&", "=");
        HttpRequest request = HttpRequest.post(StrUtil.format("{}{}?{}", simIp, userUri, join));
        log.info("请求sim获取用户Url:{}", request.getUrl());
        String body = request.execute().body();
        log.info("请求sim获取用户返回:{}", body);
        return body;
    }
}
