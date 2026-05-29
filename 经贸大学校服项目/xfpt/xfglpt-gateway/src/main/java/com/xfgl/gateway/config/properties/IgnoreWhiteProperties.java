package com.xfgl.gateway.config.properties;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;

/**
 * 放行白名单配置
 * 
 * @author ruoyi
 */
@Configuration
@RefreshScope
@ConfigurationProperties(prefix = "security.ignore")
public class IgnoreWhiteProperties
{
    /**
     * 放行白名单配置，网关不校验此处的白名单
     */
    private List<String> whites = new ArrayList<>();

    /**
     * 供应商入驻上传接口强制放行（避免 Nacos 配置加载延迟或 namespace 不匹配导致失效）
     */
    @PostConstruct
    public void init()
    {
        if (!whites.contains("/file/settle/**"))
        {
            whites.add("/file/settle/**");
        }
    }

    public List<String> getWhites()
    {
        return whites;
    }

    public void setWhites(List<String> whites)
    {
        this.whites = whites;
    }
}