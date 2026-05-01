package com.xfgl.school.service;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xfgl.school.domain.XfptReportModeOper;

/**
 * 确定选购模式操作记录Service接口
 *
 * @author yinet
 * @date 2025-04-24
 */
public interface IXfptReportModeOperService extends IService<XfptReportModeOper> {

    /**
     * 查询确定选购模式操作记录列表
     *
     * @param xfptReportModeOper 确定选购模式操作记录
     * @return 确定选购模式操作记录集合
     */
    public List<XfptReportModeOper> selectXfptReportModeOperList(XfptReportModeOper xfptReportModeOper);

}
