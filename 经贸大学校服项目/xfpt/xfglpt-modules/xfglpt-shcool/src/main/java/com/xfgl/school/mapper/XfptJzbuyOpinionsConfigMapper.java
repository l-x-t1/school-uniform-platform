package com.xfgl.school.mapper;

import java.util.List;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xfgl.school.domain.XfptJzbuyOpinionsConfig;

/**
 * 家长建议征集活动配置Mapper接口
 *
 * @author wxm
 * @date 2025-04-26
 */
public interface XfptJzbuyOpinionsConfigMapper extends BaseMapper<XfptJzbuyOpinionsConfig> {
    /**
     * 查询家长建议征集活动配置列表
     *
     * @param xfptJzbuyOpinionsConfig 家长建议征集活动配置
     * @return 家长建议征集活动配置集合
     */
    public List<XfptJzbuyOpinionsConfig> selectXfptJzbuyOpinionsConfigList(XfptJzbuyOpinionsConfig xfptJzbuyOpinionsConfig);
}
