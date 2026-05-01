package com.xfgl.school.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.xfgl.school.domain.XfptReportMode;
import com.xfgl.school.mapper.XfptReportModeMapper;
import com.xfgl.school.service.IXfptReportModeService;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;
import java.util.Map;

/**
 * 选用组织确定选购模式会议Service业务层处理
 *
 * @author yinet
 * @date 2025-04-23
 */
@Service
public class XfptReportModeServiceImpl extends ServiceImpl<XfptReportModeMapper, XfptReportMode> implements IXfptReportModeService {

    @Autowired
    private XfptReportModeMapper xfptReportModeMapper;

    /**
     * 查询选用组织确定选购模式会议列表
     *
     * @param xfptReportMode 选用组织确定选购模式会议
     * @return 选用组织确定选购模式会议
     */
    @Override
    public List<XfptReportMode> selectXfptReportModeList(XfptReportMode xfptReportMode)
    {
        return xfptReportModeMapper.selectXfptReportModeList(xfptReportMode);
    }


}
