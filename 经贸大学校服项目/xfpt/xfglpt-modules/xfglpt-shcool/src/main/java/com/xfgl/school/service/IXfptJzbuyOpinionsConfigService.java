package com.xfgl.school.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xfgl.school.domain.XfptJzbuyOpinionsConfig;

/**
 * 家长建议征集活动配置Service接口
 *
 * @author wxm
 * @date 2025-04-26
 */
public interface IXfptJzbuyOpinionsConfigService extends IService<XfptJzbuyOpinionsConfig> {

    /**
     * 查询家长建议征集活动配置列表
     *
     * @param xfptJzbuyOpinionsConfig 家长建议征集活动配置
     * @return 家长建议征集活动配置集合
     */
    public List<XfptJzbuyOpinionsConfig> selectXfptJzbuyOpinionsConfigList(XfptJzbuyOpinionsConfig xfptJzbuyOpinionsConfig);

}
