package com.xfgl.school.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;
import java.util.Map;
import com.xfgl.school.mapper.XfptPatriarchSatisfactionSummarizeMapper;
import com.xfgl.school.domain.XfptPatriarchSatisfactionSummarize;
import com.xfgl.school.service.IXfptPatriarchSatisfactionSummarizeService;

/**
 * 选用组织家长满意度总结报告报备与公开Service业务层处理
 *
 * @author yinet
 * @date 2025-04-27
 */
@Service
public class XfptPatriarchSatisfactionSummarizeServiceImpl extends ServiceImpl<XfptPatriarchSatisfactionSummarizeMapper, XfptPatriarchSatisfactionSummarize> implements IXfptPatriarchSatisfactionSummarizeService {

    @Autowired
    private XfptPatriarchSatisfactionSummarizeMapper xfptPatriarchSatisfactionSummarizeMapper;

    /**
     * 查询选用组织家长满意度总结报告报备与公开列表
     *
     * @param xfptPatriarchSatisfactionSummarize 选用组织家长满意度总结报告报备与公开
     * @return 选用组织家长满意度总结报告报备与公开
     */
    @Override
    public List<XfptPatriarchSatisfactionSummarize> selectXfptPatriarchSatisfactionSummarizeList(XfptPatriarchSatisfactionSummarize xfptPatriarchSatisfactionSummarize)
    {
        return xfptPatriarchSatisfactionSummarizeMapper.selectXfptPatriarchSatisfactionSummarizeList(xfptPatriarchSatisfactionSummarize);
    }


}
