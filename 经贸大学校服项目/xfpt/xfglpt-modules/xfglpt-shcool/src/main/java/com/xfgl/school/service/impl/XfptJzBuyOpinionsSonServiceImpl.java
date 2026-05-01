package com.xfgl.school.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;

import com.xfgl.school.mapper.XfptJzBuyOpinionsSonMapper;
import com.xfgl.school.domain.XfptJzBuyOpinionsSon;
import com.xfgl.school.service.IXfptJzBuyOpinionsSonService;

/**
 * 家长购买意见征集活动子Service业务层处理
 *
 * @author wxm
 * @date 2025-04-26
 */
@Service
public class XfptJzBuyOpinionsSonServiceImpl extends ServiceImpl<XfptJzBuyOpinionsSonMapper, XfptJzBuyOpinionsSon> implements IXfptJzBuyOpinionsSonService {

    @Autowired
    private XfptJzBuyOpinionsSonMapper xfptJzBuyOpinionsSonMapper;

    /**
     * 查询家长购买意见征集活动子列表
     *
     * @param xfptJzBuyOpinionsSon 家长购买意见征集活动子
     * @return 家长购买意见征集活动子
     */
    @Override
    public List<XfptJzBuyOpinionsSon> selectXfptJzBuyOpinionsSonList(XfptJzBuyOpinionsSon xfptJzBuyOpinionsSon) {
        return xfptJzBuyOpinionsSonMapper.selectXfptJzBuyOpinionsSonList(xfptJzBuyOpinionsSon);
    }

    @Override
    public XfptJzBuyOpinionsSon selectXfptJzBuyOpinionsSonById(Long id) {
        return xfptJzBuyOpinionsSonMapper.selectXfptJzBuyOpinionsSonById(id);
    }

    @Override
    public int insertXfptJzBuyOpinionsSon(XfptJzBuyOpinionsSon xfptJzBuyOpinionsSon) {
        return xfptJzBuyOpinionsSonMapper.insertXfptJzBuyOpinionsSon(xfptJzBuyOpinionsSon);
    }

    @Override
    public int updateXfptJzBuyOpinionsSon(XfptJzBuyOpinionsSon xfptJzBuyOpinionsSon) {
        return xfptJzBuyOpinionsSonMapper.updateXfptJzBuyOpinionsSon(xfptJzBuyOpinionsSon);
    }

    @Override
    public int deleteXfptJzBuyOpinionsSonById(Long id) {
        return xfptJzBuyOpinionsSonMapper.deleteXfptJzBuyOpinionsSonById(id);
    }

    @Override
    public int deleteXfptJzBuyOpinionsSon(XfptJzBuyOpinionsSon xfptJzBuyOpinionsSon) {
        return xfptJzBuyOpinionsSonMapper.deleteXfptJzBuyOpinionsSon(xfptJzBuyOpinionsSon);
    }
}
