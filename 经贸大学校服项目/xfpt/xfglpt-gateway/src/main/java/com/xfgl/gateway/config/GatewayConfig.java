package com.xfgl.gateway.config;

import com.alibaba.csp.sentinel.adapter.gateway.common.SentinelGatewayConstants;
import com.alibaba.csp.sentinel.adapter.gateway.common.api.ApiDefinition;
import com.alibaba.csp.sentinel.adapter.gateway.common.api.ApiPathPredicateItem;
import com.alibaba.csp.sentinel.adapter.gateway.common.api.ApiPredicateItem;
import com.alibaba.csp.sentinel.adapter.gateway.common.rule.GatewayFlowRule;
import com.alibaba.csp.sentinel.adapter.gateway.common.rule.GatewayRuleManager;
import com.alibaba.csp.sentinel.adapter.gateway.sc.SentinelGatewayFilter;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import com.xfgl.gateway.handler.SentinelFallbackHandler;

import javax.annotation.PostConstruct;
import java.util.HashSet;
import java.util.Set;

/**
 * 网关限流配置
 *
 * @author ruoyi
 */
@Configuration
public class GatewayConfig
{
    private static final String SCHOOL_SERVICE_URI = "lb://xfglpt-school";

    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public SentinelFallbackHandler sentinelGatewayExceptionHandler()
    {
        return new SentinelFallbackHandler();
    }

    @Bean
    public RouteLocator compatibilityRoutes(RouteLocatorBuilder builder)
    {
        return builder.routes()
                // Some existing pages still upload the roster through /team/meeting/importUserName
                // instead of the gateway-prefixed /school/team/meeting/importUserName path.
                .route("school-team-meeting-import-username-alias",
                        route -> route.path("/team/meeting/importUserName")
                                .uri(SCHOOL_SERVICE_URI))
                .build();
    }


//
//    @PostConstruct
//    public void doInit()
//    {
//        // 加载网关限流规则
//        initGatewayRules();
//    }
//
//    /**
//     * 网关限流规则
//     */
//    private void initGatewayRules()
//    {
//        Set<GatewayFlowRule> rules = new HashSet<>();
//        rules.add(new GatewayFlowRule("xfglpt-system")
//                .setCount(5) // 限流阈值
//                .setIntervalSec(60)); // 统计时间窗口，单位是秒，默认是 1 秒
//        // 加载网关限流规则
//        GatewayRuleManager.loadRules(rules);
//    }
}
