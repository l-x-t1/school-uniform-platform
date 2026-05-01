package com.xfgl.school.service;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xfgl.school.domain.XfptReportMode;

/**
 * 选用组织确定选购模式会议Service接口
 *
 * @author yinet
 * @date 2025-04-23
 */
public interface IXfptReportModeService extends IService<XfptReportMode> {

    /**
     * 查询选用组织确定选购模式会议列表
     *
     * @param xfptReportMode 选用组织确定选购模式会议
     * @return 选用组织确定选购模式会议集合
     */
    public List<XfptReportMode> selectXfptReportModeList(XfptReportMode xfptReportMode);

}
