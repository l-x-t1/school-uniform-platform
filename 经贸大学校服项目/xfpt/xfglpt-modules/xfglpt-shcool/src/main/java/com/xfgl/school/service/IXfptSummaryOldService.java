package com.xfgl.school.service;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xfgl.school.domain.XfptSummaryOld;

/**
 * 采购历史数据Service接口
 *
 * @author yinet
 * @date 2025-06-03
 */
public interface IXfptSummaryOldService extends IService<XfptSummaryOld> {

    /**
     * 查询采购历史数据列表
     *
     * @param xfptSummaryOld 采购历史数据
     * @return 采购历史数据集合
     */
    public List<XfptSummaryOld> selectXfptSummaryOldList(XfptSummaryOld xfptSummaryOld);

}
