package com.xfgl.school.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;

import com.xfgl.school.mapper.XfptJzBuyOpinionsMapper;
import com.xfgl.school.domain.XfptJzBuyOpinions;
import com.xfgl.school.service.IXfptJzBuyOpinionsService;

/**
 * 家长购买意见征集Service业务层处理
 *
 * @author wxm
 * @date 2025-04-25
 */
@Service
public class XfptJzBuyOpinionsServiceImpl extends ServiceImpl<XfptJzBuyOpinionsMapper, XfptJzBuyOpinions> implements IXfptJzBuyOpinionsService {

    @Autowired
    private XfptJzBuyOpinionsMapper xfptJzBuyOpinionsMapper;

    /**
     * 查询家长购买意见征集列表
     *
     * @param xfptJzBuyOpinions 家长购买意见征集
     * @return 家长购买意见征集
     */
    @Override
    public List<XfptJzBuyOpinions> selectXfptJzBuyOpinionsList(XfptJzBuyOpinions xfptJzBuyOpinions) {
        return xfptJzBuyOpinionsMapper.selectXfptJzBuyOpinionsList(xfptJzBuyOpinions);
    }

    @Override
    public int updateXfptJzBuyOpinions(XfptJzBuyOpinions xfptJzBuyOpinions) {
        return xfptJzBuyOpinionsMapper.updateXfptJzBuyOpinions(xfptJzBuyOpinions);
    }
}
