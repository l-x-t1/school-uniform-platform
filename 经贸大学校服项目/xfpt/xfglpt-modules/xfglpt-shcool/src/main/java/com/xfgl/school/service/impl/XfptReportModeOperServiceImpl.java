package com.xfgl.school.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;
import java.util.Map;
import com.xfgl.school.mapper.XfptReportModeOperMapper;
import com.xfgl.school.domain.XfptReportModeOper;
import com.xfgl.school.service.IXfptReportModeOperService;

/**
 * 确定选购模式操作记录Service业务层处理
 *
 * @author yinet
 * @date 2025-04-24
 */
@Service
public class XfptReportModeOperServiceImpl extends ServiceImpl<XfptReportModeOperMapper, XfptReportModeOper> implements IXfptReportModeOperService {

    @Autowired
    private XfptReportModeOperMapper xfptReportModeOperMapper;

    /**
     * 查询确定选购模式操作记录列表
     *
     * @param xfptReportModeOper 确定选购模式操作记录
     * @return 确定选购模式操作记录
     */
    @Override
    public List<XfptReportModeOper> selectXfptReportModeOperList(XfptReportModeOper xfptReportModeOper)
    {
        return xfptReportModeOperMapper.selectXfptReportModeOperList(xfptReportModeOper);
    }


}
