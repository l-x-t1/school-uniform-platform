package com.xfgl.school.service;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xfgl.school.domain.XfptProcSummary;

/**
 * 采购汇总信息Service接口
 *
 * @author yinet
 * @date 2025-05-09
 */
public interface IXfptProcSummaryService extends IService<XfptProcSummary> {

    /**
     * 查询采购汇总信息列表
     *
     * @param xfptProcSummary 采购汇总信息
     * @return 采购汇总信息集合
     */
    public List<XfptProcSummary> selectXfptProcSummaryList(XfptProcSummary xfptProcSummary);

    public Boolean setDate(XfptProcSummary xfptProcSummary);

}
