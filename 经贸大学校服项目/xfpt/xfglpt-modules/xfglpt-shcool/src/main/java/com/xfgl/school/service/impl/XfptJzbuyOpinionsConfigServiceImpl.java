package com.xfgl.school.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;

import com.xfgl.school.mapper.XfptJzbuyOpinionsConfigMapper;
import com.xfgl.school.domain.XfptJzbuyOpinionsConfig;
import com.xfgl.school.service.IXfptJzbuyOpinionsConfigService;

/**
 * 家长建议征集活动配置Service业务层处理
 *
 * @author wxm
 * @date 2025-04-26
 */
@Service
public class XfptJzbuyOpinionsConfigServiceImpl extends ServiceImpl<XfptJzbuyOpinionsConfigMapper, XfptJzbuyOpinionsConfig> implements IXfptJzbuyOpinionsConfigService {

    @Autowired
    private XfptJzbuyOpinionsConfigMapper xfptJzbuyOpinionsConfigMapper;

    /**
     * 查询家长建议征集活动配置列表
     *
     * @param xfptJzbuyOpinionsConfig 家长建议征集活动配置
     * @return 家长建议征集活动配置
     */
    @Override
    public List<XfptJzbuyOpinionsConfig> selectXfptJzbuyOpinionsConfigList(XfptJzbuyOpinionsConfig xfptJzbuyOpinionsConfig) {
        return xfptJzbuyOpinionsConfigMapper.selectXfptJzbuyOpinionsConfigList(xfptJzbuyOpinionsConfig);
    }


}
