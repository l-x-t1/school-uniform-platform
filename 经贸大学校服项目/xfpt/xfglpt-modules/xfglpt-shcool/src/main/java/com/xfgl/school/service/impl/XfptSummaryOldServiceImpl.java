package com.xfgl.school.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;
import java.util.Map;
import com.xfgl.school.mapper.XfptSummaryOldMapper;
import com.xfgl.school.domain.XfptSummaryOld;
import com.xfgl.school.service.IXfptSummaryOldService;

/**
 * 采购历史数据Service业务层处理
 *
 * @author yinet
 * @date 2025-06-03
 */
@Service
public class XfptSummaryOldServiceImpl extends ServiceImpl<XfptSummaryOldMapper, XfptSummaryOld> implements IXfptSummaryOldService {

    @Autowired
    private XfptSummaryOldMapper xfptSummaryOldMapper;

    /**
     * 查询采购历史数据列表
     *
     * @param xfptSummaryOld 采购历史数据
     * @return 采购历史数据
     */
    @Override
    public List<XfptSummaryOld> selectXfptSummaryOldList(XfptSummaryOld xfptSummaryOld)
    {
        return xfptSummaryOldMapper.selectXfptSummaryOldList(xfptSummaryOld);
    }


}
